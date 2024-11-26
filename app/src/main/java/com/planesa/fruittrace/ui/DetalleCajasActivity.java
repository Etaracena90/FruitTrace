package com.planesa.fruittrace.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.planesa.fruittrace.R;
import com.planesa.fruittrace.adapter.CajasEscaneadasAdapter;
import com.planesa.fruittrace.dao.DAOCorte;
import com.planesa.fruittrace.model.Corte;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetalleCajasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CajasEscaneadasAdapter adapter;
    private DAOCorte daoCorte;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_cajas);

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerViewCajas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar DAO y ExecutorService
        daoCorte = new DAOCorte();
        executorService = Executors.newSingleThreadExecutor();

        // Obtener el ID del documento desde el Intent
        int idDocumento = getIntent().getIntExtra("id_corte", -1);

        if (idDocumento != -1) {
            Log.d("DetalleCajasActivity", "ID recibido: " + idDocumento);
            cargarDetalleCajas(idDocumento);
        } else {
            Toast.makeText(this, "No se pudo obtener el ID del documento. Intenta de nuevo.", Toast.LENGTH_LONG).show();
            finish(); // Finalizar la actividad si no se recibe un ID válido
        }
    }

    private void cargarDetalleCajas(int idDocumento) {
        executorService.execute(() -> {
            try {
                // Consultar los detalles de las cajas escaneadas
                List<Corte> listaCajas = daoCorte.listarCajasEscaneadasPorCortador(idDocumento);

                runOnUiThread(() -> {
                    if (listaCajas != null && !listaCajas.isEmpty()) {
                        // Configurar el adapter y pasar los datos
                        adapter = new CajasEscaneadasAdapter(listaCajas);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "No se encontraron cajas para este documento.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e("DetalleCajasActivity", "Error al cargar las cajas: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar los detalles: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cerrar el ExecutorService para evitar fugas de memoria
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
