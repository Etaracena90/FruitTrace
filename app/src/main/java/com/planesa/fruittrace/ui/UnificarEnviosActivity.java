package com.planesa.fruittrace.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.planesa.fruittrace.adapter.UnificarEnviosAdapter;
import com.planesa.fruittrace.dao.DAOCorte;
import com.planesa.fruittrace.model.Corte;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UnificarEnviosActivity extends AppCompatActivity implements CorteAdapter.OnScanClickListener, NavigationView.OnNavigationItemSelectedListener {
    private RecyclerView rvUnificarEnvios;
    private Button btnCrearDocumento;
    private FloatingActionButton btnRecargarUnificar;

    private DrawerLayout drawerLayout;
    private UnificarEnviosAdapter corteAdapter;
    private DAOCorte daoCorte;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unificar_envios);

        // Inicializar vistas
        rvUnificarEnvios = findViewById(R.id.rvUnificarEnvios);
        btnCrearDocumento = findViewById(R.id.btnNuevoDocumento);
        btnRecargarUnificar = findViewById(R.id.btnRecargarEnvios);
        drawerLayout = findViewById(R.id.drawerLayout);
        rvUnificarEnvios.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar DAO y ExecutorService
        daoCorte = new DAOCorte();
        executorService = Executors.newSingleThreadExecutor();

        // Cargar la lista de envíos unificados
        cargarListaEnvios();

        // Configurar el NavigationView
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Configurar el toggle del menú hamburguesa
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Configurar botón para crear un nuevo documento
        btnCrearDocumento.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirmar creación")
                    .setMessage("¿Está seguro de que desea crear un nuevo documento?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        crearNuevoDocumento();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Configurar botón de recargar
        btnRecargarUnificar.setOnClickListener(v -> {
            Toast.makeText(this, "Recargando documentos...", Toast.LENGTH_SHORT).show();
            cargarListaEnvios();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarListaEnvios(); // Recargar la lista al regresar a esta actividad
    }

    private void crearNuevoDocumento() {
        String usuarioAutenticado = getIntent().getStringExtra("usuario");
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Estado predeterminado para el documento
        int estado = 4;

        Corte nuevoCorte = new Corte();
        nuevoCorte.setApuntador(usuarioAutenticado);
        nuevoCorte.setFecha(fechaActual);
        nuevoCorte.setEstado(estado);

        executorService.execute(() -> {
            try {
                daoCorte.Generar_Envio_unificado(nuevoCorte);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Documento creado exitosamente", Toast.LENGTH_SHORT).show();
                    cargarListaEnvios(); // Recargar la lista para mostrar el nuevo documento
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error al crear documento: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }


    private void cargarListaEnvios() {
        // Obtener el usuario autenticado desde el Intent
        String usuarioAutenticado = getIntent().getStringExtra("usuario");

        executorService.execute(() -> {
            try {
                // Crear un objeto Corte para filtrar por apuntador
                Corte corteFiltro = new Corte();
                corteFiltro.setApuntador(usuarioAutenticado); // Usar el usuario autenticado como apuntador

                // Llamar al método del DAO para obtener la lista de documentos
                List<Corte> listaCorte = daoCorte.MostrarDocumento(corteFiltro);

                // Actualizar la UI en el hilo principal
                runOnUiThread(() -> {
                    if (listaCorte != null && !listaCorte.isEmpty()) {
                        if (corteAdapter == null) {
                            String usuarioLogeado = getIntent().getStringExtra("usuario");
                            corteAdapter = new UnificarEnviosAdapter(listaCorte,this, usuarioLogeado); // Configurar el adaptador
                            rvUnificarEnvios.setAdapter(corteAdapter); // Asignar el adaptador al RecyclerView
                        } else {
                            corteAdapter.updateData(listaCorte); // Actualizar los datos del adaptador
                        }
                    } else {
                        Toast.makeText(this, "No hay documentos disponibles", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                // Manejar errores en el hilo principal
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error al cargar la lista: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }


    @Override
    public void onScanClick(Corte corte) {
        Toast.makeText(this, "Clic en documento: " + corte.getId_enc_corte(), Toast.LENGTH_SHORT).show();
        // Aquí puedes agregar la lógica para manejar el clic en un elemento.
    }

    @Override
    public void onCancelClick(Corte corte) {

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_home) {
            // Navegar de vuelta a MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Reutiliza la actividad si ya existe
            startActivity(intent);
        } else if (itemId == R.id.menu_settings) {
            Toast.makeText(this, "Configuraciones seleccionadas", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.menu_logout) {
            Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START); // Cerrar el menú después de seleccionar
        return true;
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDocumentoId, tvFecha;
        Button btnSeleccionar, btnCancelar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDocumentoId = itemView.findViewById(R.id.tvDocumentoId);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            btnSeleccionar = itemView.findViewById(R.id.btnSeleccionar);
            btnCancelar = itemView.findViewById(R.id.btnCancelar);
        }
    }



}
