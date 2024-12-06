package com.planesa.fruittrace.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.planesa.fruittrace.R;
import com.planesa.fruittrace.dao.DAOCorte;
import com.planesa.fruittrace.dao.DAOCultivo;
import com.planesa.fruittrace.dao.DAODestino;
import com.planesa.fruittrace.dao.DAOEtiqueta;
import com.planesa.fruittrace.dao.DAOFinca;
import com.planesa.fruittrace.dao.DAOPresentacion;
import com.planesa.fruittrace.model.Corte;
import com.planesa.fruittrace.model.Cultivo;
import com.planesa.fruittrace.model.Destino;
import com.planesa.fruittrace.model.Etiqueta;
import com.planesa.fruittrace.model.Finca;
import com.planesa.fruittrace.model.Presentacion;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FormularioActivity extends AppCompatActivity {
    private TextView etFecha, etApuntador;
    private Spinner spFinca, spCultivo, spPresentacion, spDestino, spEtiqueta;
    private CheckBox checkboxShowDia;

    private final int estado = 4;
    private Button btnRegistrar;

    private View progressBar;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        // Inicializar los elementos de la UI
        etFecha = findViewById(R.id.etFecha);
        etApuntador = findViewById(R.id.etApuntador);
        spFinca = findViewById(R.id.spFinca);
        spCultivo = findViewById(R.id.spCultivo);
        spPresentacion = findViewById(R.id.spPresentacion);
        spDestino = findViewById(R.id.spDestino);
        spEtiqueta = findViewById(R.id.spEtiqueta);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        progressBar = findViewById(R.id.progressBar);
        checkboxShowDia = findViewById(R.id.checkbox_show_dia);

        // Configurar la fecha actual y deshabilitar la edición
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        etFecha.setText(fechaActual);
        etFecha.setFocusable(false);

        // Obtener el usuario autenticado y mostrarlo en el campo de apuntador
        String usuario = getIntent().getStringExtra("usuario");
        if (usuario != null) {
            etApuntador.setText(usuario);
            etApuntador.setFocusable(false); // Deshabilitar la edición
        }

        // Cargar los datos en los Spinners
        cargarDatosEnSpinners();

        // Configurar el botón de registro
        // Configurar el botón de registro
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Deshabilitar el botón y mostrar el ProgressBar
                btnRegistrar.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                String fecha = etFecha.getText().toString();
                String apuntador = etApuntador.getText().toString();
                Finca fincaSeleccionada = (Finca) spFinca.getSelectedItem();
                Cultivo cultivoSeleccionado = (Cultivo) spCultivo.getSelectedItem();
                Presentacion presentacionSeleccionada = (Presentacion) spPresentacion.getSelectedItem();
                Destino destinoSeleccionado = (Destino) spDestino.getSelectedItem();
                Etiqueta etiquetaSeleccionada = (Etiqueta) spEtiqueta.getSelectedItem();

                // Verifica que los objetos seleccionados no sean nulos
                if (fincaSeleccionada != null && cultivoSeleccionado != null && presentacionSeleccionada != null &&
                        destinoSeleccionado != null && etiquetaSeleccionada != null) {

                    // Imprime el nombre del cultivo seleccionado en el log
                    String nombreCultivo = cultivoSeleccionado.getNombre_Cultivo();
                    Log.d("FormularioActivity", "Nombre del cultivo seleccionado: " + nombreCultivo);

                    // Continua con la lógica de registro
                    Corte corte = new Corte();
                    corte.setFecha(fecha);
                    corte.setApuntador(apuntador);
                    corte.setFinca(fincaSeleccionada.getId_Finca());
                    corte.setCultivo(nombreCultivo); // Verifica si aquí se asigna correctamente
                    corte.setPresentacion(presentacionSeleccionada.getCodigo_barras());
                    corte.setDestino(destinoSeleccionado.getCodigo_barra());
                    corte.setEtiqueta(etiquetaSeleccionada.getCodigo_barras());
                    if (checkboxShowDia.isChecked()) {
                        // Dia festivo
                        corte.setDia("FESTIVO");
                    } else {
                        // Dia Normal
                        corte.setDia("null");
                    }
                    corte.setEstado(estado);

                    // Registrar el documento
                    registrarDocumento(corte, apuntador);
                } else {
                    // Habilitar el botón y ocultar el ProgressBar si los campos están incompletos
                    btnRegistrar.setEnabled(true);
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(FormularioActivity.this, "Por favor, selecciona todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void cargarDatosEnSpinners() {
        executorService.execute(() -> {
            try {
                List<Finca> fincas = new DAOFinca().listarFinca();
                List<Cultivo> cultivos = new DAOCultivo().listarCultivo();
                List<Presentacion> presentaciones = new DAOPresentacion().listarPresentacion();
                List<Destino> destinos = new DAODestino().listarDestino();
                List<Etiqueta> etiquetas = new DAOEtiqueta().listarEtiqueta();

                runOnUiThread(() -> {
                    spFinca.setAdapter(new ArrayAdapter<>(FormularioActivity.this, android.R.layout.simple_spinner_dropdown_item, fincas));
                    spCultivo.setAdapter(new ArrayAdapter<>(FormularioActivity.this, android.R.layout.simple_spinner_dropdown_item, cultivos));
                    spPresentacion.setAdapter(new ArrayAdapter<>(FormularioActivity.this, android.R.layout.simple_spinner_dropdown_item, presentaciones));
                    spDestino.setAdapter(new ArrayAdapter<>(FormularioActivity.this, android.R.layout.simple_spinner_dropdown_item, destinos));
                    spEtiqueta.setAdapter(new ArrayAdapter<>(FormularioActivity.this, android.R.layout.simple_spinner_dropdown_item, etiquetas));
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(FormularioActivity.this, "Error al cargar los datos: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void registrarDocumento(Corte corte, String usuario) {
        // Deshabilitar el botón de registro y mostrar el ProgressBar
        runOnUiThread(() -> {
            btnRegistrar.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        });

        executorService.execute(() -> {
            try {
                // Registrar el documento
                new DAOCorte().registrar(corte);

                runOnUiThread(() -> {
                    Toast.makeText(FormularioActivity.this, "Documento registrado con éxito", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent); // Establece el resultado como OK

                    // Ocultar el ProgressBar y habilitar el botón
                    progressBar.setVisibility(View.GONE);
                    btnRegistrar.setEnabled(true);

                    finish(); // Finaliza la actividad
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(FormularioActivity.this, "Error al registrar el documento: " + e.getMessage(), Toast.LENGTH_LONG).show();

                    // Ocultar el ProgressBar y habilitar el botón
                    progressBar.setVisibility(View.GONE);
                    btnRegistrar.setEnabled(true);
                });
            }
        });
    }


}
