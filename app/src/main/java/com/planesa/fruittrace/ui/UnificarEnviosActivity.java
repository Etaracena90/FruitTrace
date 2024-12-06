package com.planesa.fruittrace.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.planesa.fruittrace.R;
import com.planesa.fruittrace.adapter.UnificarEnviosAdapter;
import com.planesa.fruittrace.dao.DAOCorte;
import com.planesa.fruittrace.model.Corte;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UnificarEnviosActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int SELECCIONAR_ENVIO_REQUEST_CODE = 100;

    private RecyclerView rvUnificarEnvios;
    private FloatingActionButton btnRecargarUnificar;
    private ImageButton btnVerUnificados;
    private Button btnNuevoDocumento;
    private DrawerLayout drawerLayout;

    private UnificarEnviosAdapter corteAdapter;
    private DAOCorte daoCorte;
    private ExecutorService executorService;

    private TextView userNameTextView;
    private TextView userEmailTextView;
    private String usuarioLogeado;
    private String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unificar_envios);

        // Inicializar vistas
        rvUnificarEnvios = findViewById(R.id.rvUnificarEnvios);
        btnRecargarUnificar = findViewById(R.id.btnRecargarEnvios);
        btnVerUnificados = findViewById(R.id.btnVerUnificados);
        btnNuevoDocumento = findViewById(R.id.btnNuevoDocumento);
        drawerLayout = findViewById(R.id.drawerLayout);
        rvUnificarEnvios.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar DAO y ExecutorService
        daoCorte = new DAOCorte();
        executorService = Executors.newSingleThreadExecutor();

        // Obtener usuario logeado y nombre
        usuarioLogeado = getIntent().getStringExtra("usuario");
        nombreUsuario = getIntent().getStringExtra("nombre");

        if (usuarioLogeado == null || usuarioLogeado.isEmpty()) {
            Toast.makeText(this, "Usuario no recibido. Finalizando actividad.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar NavigationView y encabezado
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        userNameTextView = headerView.findViewById(R.id.tvUserName);
        userEmailTextView = headerView.findViewById(R.id.tvUserEmail);

        // Asignar valores al encabezado
        userNameTextView.setText(nombreUsuario != null ? nombreUsuario : "Usuario");
        userEmailTextView.setText(usuarioLogeado != null ? usuarioLogeado : "email@dominio.com");

        // Configurar NavigationView
        navigationView.setNavigationItemSelectedListener(this);

        // Configurar el toggle del menú hamburguesa
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Cargar la lista de envíos unificados
        cargarListaEnvios();

        // Configurar botón para crear un nuevo documento
        btnNuevoDocumento.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirmar creación")
                    .setMessage("¿Está seguro de que desea crear un nuevo documento?")
                    .setPositiveButton("Sí", (dialog, which) -> crearNuevoDocumento())
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Configurar botón para recargar documentos
        btnRecargarUnificar.setOnClickListener(v -> {
            Toast.makeText(this, "Recargando documentos...", Toast.LENGTH_SHORT).show();
            cargarListaEnvios();
        });

        // Configurar botón para ver documentos unificados
        btnVerUnificados.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaUnificadosActivity.class);
            intent.putExtra("usuario", usuarioLogeado);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarListaEnvios();
    }

    private void crearNuevoDocumento() {
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        int estado = 4;

        Corte nuevoCorte = new Corte();
        nuevoCorte.setApuntador(usuarioLogeado);
        nuevoCorte.setFecha(fechaActual);
        nuevoCorte.setEstado(estado);

        executorService.execute(() -> {
            try {
                daoCorte.Generar_Envio_unificado(nuevoCorte);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Documento creado exitosamente", Toast.LENGTH_SHORT).show();
                    cargarListaEnvios();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al crear documento: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void cargarListaEnvios() {
        executorService.execute(() -> {
            try {
                Corte filtroCorte = new Corte();
                filtroCorte.setApuntador(usuarioLogeado);

                List<Corte> listaCorte = daoCorte.MostrarDocumento(filtroCorte);

                runOnUiThread(() -> {
                    if (listaCorte != null && !listaCorte.isEmpty()) {
                        if (corteAdapter == null) {
                            corteAdapter = new UnificarEnviosAdapter(listaCorte, this, usuarioLogeado);
                            rvUnificarEnvios.setAdapter(corteAdapter);
                        } else {
                            corteAdapter.updateData(listaCorte);
                        }
                    } else {
                        if (corteAdapter != null) {
                            corteAdapter.updateData(listaCorte);
                        }
                        Toast.makeText(this, "No hay documentos disponibles.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar documentos: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (itemId == R.id.menu_settings) {
            Toast.makeText(this, "Configuraciones seleccionadas", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.menu_logout) {
            cerrarSesion();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void cerrarSesion() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
