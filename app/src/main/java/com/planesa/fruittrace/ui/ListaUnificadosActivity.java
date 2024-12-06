package com.planesa.fruittrace.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.planesa.fruittrace.R;
import com.planesa.fruittrace.adapter.ListaUnificadosAdapter;
import com.planesa.fruittrace.dao.DAOCorte;
import com.planesa.fruittrace.model.Corte;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListaUnificadosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewUnificados;
    private ListaUnificadosAdapter adapter;
    private DAOCorte daoCorte;
    private ExecutorService executorService;
    private List<Corte> listaUnificados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_unificados);

        recyclerViewUnificados = findViewById(R.id.recyclerViewUnificados);
        recyclerViewUnificados.setLayoutManager(new LinearLayoutManager(this));

        daoCorte = new DAOCorte();
        executorService = Executors.newSingleThreadExecutor();
        listaUnificados = new ArrayList<>();

        adapter = new ListaUnificadosAdapter(listaUnificados, this);
        recyclerViewUnificados.setAdapter(adapter);

        cargarDocumentosUnificados();
    }

    private void cargarDocumentosUnificados() {
        executorService.execute(() -> {
            try {
                // Crear un objeto Corte para filtrar por apuntador
                Corte corteFiltro = new Corte();
                String usuarioLogeado = getIntent().getStringExtra("usuario"); // Obtener el usuario logeado
                corteFiltro.setApuntador(usuarioLogeado);

                // Llamar al método MostrarDocumentoCerrados
                List<Corte> documentosUnificados = daoCorte.MostrarDocumentoCerrados(corteFiltro);

                runOnUiThread(() -> {
                    if (documentosUnificados != null && !documentosUnificados.isEmpty()) {
                        adapter.updateData(documentosUnificados);
                    } else {
                        Toast.makeText(this, "No hay documentos unificados disponibles", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar documentos: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
