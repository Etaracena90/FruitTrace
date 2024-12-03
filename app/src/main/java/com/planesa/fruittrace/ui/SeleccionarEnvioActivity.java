package com.planesa.fruittrace.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.planesa.fruittrace.R;
import com.planesa.fruittrace.adapter.SeleccionarEnviosAdapter;
import com.planesa.fruittrace.dao.DAOCorte;
import com.planesa.fruittrace.model.Corte;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SeleccionarEnvioActivity extends AppCompatActivity {

    private ExecutorService executorService;
    private String usuarioLogeado;

    private RecyclerView recyclerView;
    private SeleccionarEnviosAdapter adapter;
    private List<Corte> listaDocumentos = new ArrayList<>();
    private DAOCorte daoCorte;
    private Button btnGuardarSeleccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_envio);

        // Obtener datos enviados desde la actividad anterior
        int idCorte = getIntent().getIntExtra("id_corte", -1); // Valor por defecto -1 si no se recibe
        usuarioLogeado = getIntent().getStringExtra("usuario");

        if (idCorte == -1) {
            Toast.makeText(this, "Error: No se recibió el ID de envío unificado", Toast.LENGTH_LONG).show();
            Log.e("SeleccionarEnvio", "ID Corte no recibido");
            finish();
            return;
        }

        if (usuarioLogeado == null || usuarioLogeado.isEmpty()) {
            Toast.makeText(this, "Usuario no recibido", Toast.LENGTH_SHORT).show();
            Log.e("SeleccionarEnvio", "Usuario no recibido");
            finish();
            return;
        }

        Log.d("SeleccionarEnvio", "ID Corte recibido: " + idCorte);
        Log.d("SeleccionarEnvio", "Usuario logeado recibido: " + usuarioLogeado);

        // Inicializa executorService
        executorService = Executors.newSingleThreadExecutor();

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerViewSeleccionar);
        btnGuardarSeleccion = findViewById(R.id.btnGuardarSeleccion);
        daoCorte = new DAOCorte();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cargarDocumentos(usuarioLogeado); // Cargar los documentos usando el usuario

        // Configurar el botón de guardar selección
        btnGuardarSeleccion.setOnClickListener(v -> guardarSeleccion(idCorte));
    }

    private void cargarDocumentos(String usuario) {
        executorService.execute(() -> {
            try {
                // Llamar al DAO para obtener la lista de documentos con el usuario como apuntador
                List<Corte> listaDocumentos = daoCorte.listarEnviosPorApuntador(usuario);

                runOnUiThread(() -> {
                    if (listaDocumentos != null && !listaDocumentos.isEmpty()) {
                        // Configurar el adaptador con los documentos obtenidos
                        if (adapter == null) {
                            adapter = new SeleccionarEnviosAdapter(listaDocumentos, this);
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter.updateData(listaDocumentos);
                        }
                    } else {
                        // Mostrar un mensaje si no hay documentos
                        Toast.makeText(this, "No hay documentos disponibles", Toast.LENGTH_SHORT).show();
                        Log.w("SeleccionarEnvio", "No se encontraron documentos para el usuario: " + usuario);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error al cargar documentos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("SeleccionarEnvio", "Error al cargar documentos", e);
                });
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

    private void guardarSeleccion(Integer noEnvioUnificado) {
        // Obtener los documentos seleccionados desde el adaptador
        Set<Integer> seleccionados = adapter.getDocumentosSeleccionados();
        if (seleccionados.isEmpty()) {
            Toast.makeText(this, "Seleccione al menos un documento", Toast.LENGTH_SHORT).show();
            Log.w("SeleccionarEnvio", "No se seleccionaron documentos");
            return;
        }

        // Procesar los documentos seleccionados en un hilo aparte
        executorService.execute(() -> {
            try {
                // Fecha actual en formato adecuado
                String fechaActual = java.time.LocalDate.now().toString();

                for (Integer id : seleccionados) {
                    // Crear el objeto Corte para cada documento seleccionado
                    Corte corte = new Corte();
                    corte.setNo_envio_unificado(noEnvioUnificado); // Número de envío unificado
                    corte.setNo_envio(id); // ID del documento seleccionado
                    corte.setFecha_envio_unificado(fechaActual); // Fecha actual
                    corte.setEstado_envio(6); // Nuevo estado
                    corte.setEstado_envio_unificado(3); // Estado para el envío unificado

                    // Llamar al DAO para unificar los envíos
                    daoCorte.Unificar_envios(corte);

                    // Actualizar el estado del documento para que no aparezca más
                    daoCorte.ActualizarEnc_Envio(corte);
                    daoCorte.ActualizarEnc_Envio_unificado(corte);

                    Log.d("SeleccionarEnvio", "Documento procesado: " + id);
                }

                // Volver a la actividad principal en la interfaz de usuario
                runOnUiThread(() -> {
                    Toast.makeText(this, "Documentos guardados y unificados correctamente", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra la actividad actual y regresa a la anterior
                });

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error al guardar documentos: " + e.getMessage(), Toast.LENGTH_LONG).show());
                Log.e("SeleccionarEnvio", "Error al guardar documentos", e);
            }
        });
    }

}
