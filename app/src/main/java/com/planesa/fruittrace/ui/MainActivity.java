package com.planesa.fruittrace.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import com.planesa.fruittrace.R;
import com.planesa.fruittrace.adapter.CorteAdapter;
import com.planesa.fruittrace.dao.DAOCorte;
import com.planesa.fruittrace.model.Corte;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, CorteAdapter.OnScanClickListener {
    private RecyclerView rvCorte;
    private Button btnNuevoFormulario;
    private FloatingActionButton btnRecargar;
    private CorteAdapter corteAdapter;
    private DAOCorte daoCorte;
    private ExecutorService executorService;

    private DrawerLayout drawerLayout;
    private TextView userNameTextView;
    private TextView userEmailTextView;

    private String nombreUsuario;
    private String usuarioAutenticado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configura el comportamiento personalizado del botón atrás
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    // Cierra el menú lateral si está abierto
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // Minimiza la aplicación en lugar de cerrarla
                    moveTaskToBack(true);
                }
            }
        });

        // Inicializar vistas
        rvCorte = findViewById(R.id.rvCorte);
        btnNuevoFormulario = findViewById(R.id.btnNuevoFormulario);
        btnRecargar = findViewById(R.id.btnRecargar); // Inicializamos el botón redondo
        rvCorte.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar DAO y ExecutorService
        daoCorte = new DAOCorte();
        executorService = Executors.newSingleThreadExecutor();

        // Obtener el usuario autenticado
        usuarioAutenticado = getIntent().getStringExtra("usuario");
        nombreUsuario = getIntent().getStringExtra("nombre"); // Supongo que estás pasando el nombre del usuario desde LoginActivity
        Log.d("MainActivity", "Usuario autenticado: " + usuarioAutenticado);

        // Configurar el menú lateral (DrawerLayout)
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigation_view);

        // Configurar el encabezado del menú lateral
        View headerView = navigationView.getHeaderView(0);
        userNameTextView = headerView.findViewById(R.id.tvUserName);
        userEmailTextView = headerView.findViewById(R.id.tvUserEmail);

        // Asignar valores al encabezado del menú
        userNameTextView.setText(nombreUsuario != null ? nombreUsuario : "Usuario");
        userEmailTextView.setText(usuarioAutenticado != null ? usuarioAutenticado : "email@dominio.com");

        // Configurar el NavigationView
        navigationView.setNavigationItemSelectedListener(this);

        // Configurar el toggle del menú hamburguesa
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Mostrar el ícono del menú en la barra de acciones
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Cargar la lista de cortes basados en el usuario autenticado
        cargarListaCorte(usuarioAutenticado);

        // Configurar botón para abrir un nuevo formulario
        btnNuevoFormulario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
                intent.putExtra("usuario", usuarioAutenticado);
                startActivityForResult(intent, 100); // Código de solicitud 100
            }
        });

        // Configurar botón de recargar
        btnRecargar.setOnClickListener(v -> {
            Toast.makeText(this, "Recargando documentos...", Toast.LENGTH_SHORT).show();
            cargarListaCorte(usuarioAutenticado);
        });

        corteAdapter = new CorteAdapter(new ArrayList<>(), this, this);
        corteAdapter.setOnDocumentoEnviadoListener(() -> cargarListaCorte(usuarioAutenticado));
        rvCorte.setAdapter(corteAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_home) {
            Toast.makeText(this, "Ya estás en Inicio", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.menu_settings) {
            Toast.makeText(this, "Configuraciones seleccionadas", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.menu_logout) {
            cerrarSesion(); // Llama al método para cerrar sesión
        } else if (itemId == R.id.menu_unificar_envios) {
            String usuarioAutenticado = getIntent().getStringExtra("usuario");
            Intent intent = new Intent(MainActivity.this, UnificarEnviosActivity.class);
            intent.putExtra("usuario", usuarioAutenticado); // Pasa el usuario autenticado
            intent.putExtra("nombre", nombreUsuario);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Reutiliza la actividad si ya existe
            startActivity(intent);
        } else {
            Toast.makeText(this, "Elemento no reconocido", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void cerrarSesion() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpiar pila de actividades
        startActivity(intent);
        finish(); // Finalizar la actividad actual
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            String usuarioAutenticado = getIntent().getStringExtra("usuario");
            cargarListaCorte(usuarioAutenticado);
        }
    }

    private void cargarListaCorte(String usuario) {
        executorService.execute(() -> {
            try {
                List<Corte> listaCorte = daoCorte.listarCortePorUsuario(usuario);
                runOnUiThread(() -> {
                    if (listaCorte != null && !listaCorte.isEmpty()) {
                        if (corteAdapter == null) {
                            corteAdapter = new CorteAdapter(listaCorte, this, this);
                            rvCorte.setAdapter(corteAdapter);
                        } else {
                            corteAdapter.updateData(listaCorte);
                        }
                    } else {
                        if (corteAdapter != null) {
                            corteAdapter.updateData(listaCorte);
                        }
                        Toast.makeText(MainActivity.this, "No hay documentos disponibles", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Error al cargar la lista de cortes: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onScanClick(Corte corte) {
        String usuarioAutenticado = getIntent().getStringExtra("usuario");
        Intent intent = new Intent(MainActivity.this, FormularioScanActivity.class);
        intent.putExtra("id_corte", corte.getId_enc_corte());
        intent.putExtra("usuario", usuarioAutenticado);
        Log.d("MainActivity", "ID del documento pasado: " + corte.getId());
        startActivity(intent);
    }

    @Override
    public void onCancelClick(Corte corte) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Confirmar cancelación")
                .setMessage("¿Está seguro de que desea cancelar el documento número " + corte.getId_enc_corte() + "?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    executorService.execute(() -> {
                        try {
                            daoCorte.actualizarEstadoCorte(corte.getId_enc_corte(), 5);
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Documento cancelado exitosamente", Toast.LENGTH_SHORT).show();
                                cargarListaCorte(getIntent().getStringExtra("usuario"));
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(this, "Error al cancelar el documento: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
