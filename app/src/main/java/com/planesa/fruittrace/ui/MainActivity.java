package com.planesa.fruittrace.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.planesa.fruittrace.R;
import com.planesa.fruittrace.adapter.CorteAdapter;
import com.planesa.fruittrace.dao.DAOCorte;
import com.planesa.fruittrace.model.Corte;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements CorteAdapter.OnScanClickListener {
    private RecyclerView rvCorte;
    private Button btnNuevoFormulario;
    private FloatingActionButton btnRecargar;
    private CorteAdapter corteAdapter;
    private DAOCorte daoCorte;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        rvCorte = findViewById(R.id.rvCorte);
        btnNuevoFormulario = findViewById(R.id.btnNuevoFormulario);
        btnRecargar = findViewById(R.id.btnRecargar); // Inicializamos el botón redondo
        rvCorte.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar DAO y ExecutorService
        daoCorte = new DAOCorte();
        executorService = Executors.newSingleThreadExecutor();

        // Obtener el usuario autenticado
        String usuarioAutenticado = getIntent().getStringExtra("usuario");

        // Agrega el log aquí para verificar que el usuario se esté pasando correctamente
        Log.d("MainActivity", "Usuario autenticado: " + usuarioAutenticado);

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
            cargarListaCorte(usuarioAutenticado); // Recargar la lista de documentos
        });

        corteAdapter = new CorteAdapter(new ArrayList<>(), this, this);
        corteAdapter.setOnDocumentoEnviadoListener(() -> cargarListaCorte(usuarioAutenticado));
        rvCorte.setAdapter(corteAdapter);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Recarga la lista de cortes después de registrar un formulario
            String usuarioAutenticado = getIntent().getStringExtra("usuario");
            cargarListaCorte(usuarioAutenticado);
        }
    }

    private void cargarListaCorte(String usuario) {
        executorService.execute(() -> {
            try {
                // Obtener la lista actualizada desde la base de datos
                List<Corte> listaCorte = daoCorte.listarCortePorUsuario(usuario);
                Log.d("MainActivity", "Cantidad de registros obtenidos: " + listaCorte.size());

                runOnUiThread(() -> {
                    if (listaCorte != null && !listaCorte.isEmpty()) {
                        // Actualizar o inicializar el adaptador con la nueva lista
                        if (corteAdapter == null) {
                            corteAdapter = new CorteAdapter(listaCorte, this, this); // Inicializar el adaptador
                            rvCorte.setAdapter(corteAdapter);
                        } else {
                            corteAdapter.updateData(listaCorte); // Actualizar los datos en el adaptador
                        }
                    } else {
                        // Manejar el caso de lista vacía
                        if (corteAdapter != null) {
                            corteAdapter.updateData(listaCorte); // Pasar una lista vacía al adaptador
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
    public void onScanClick(Corte corte) {
        String usuarioAutenticado = getIntent().getStringExtra("usuario");
        Intent intent = new Intent(MainActivity.this, FormularioScanActivity.class);
        intent.putExtra("id_corte", corte.getId_enc_corte()); // Pasar el ID del documento si es necesario
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
                            daoCorte.actualizarEstadoCorte(corte.getId_enc_corte(), 5); // Cambiar el estado a 5
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Documento cancelado exitosamente", Toast.LENGTH_SHORT).show();
                                cargarListaCorte(getIntent().getStringExtra("usuario")); // Recargar la lista
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> Toast.makeText(this, "Error al cancelar el documento: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }
                    });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // No hacer nada si selecciona "No"
                .show();
    }
}
