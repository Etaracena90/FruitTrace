package com.planesa.fruittrace.ui;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.planesa.fruittrace.R;
import com.planesa.fruittrace.dao.DAOCorte;
import com.planesa.fruittrace.model.Corte;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FormularioScanActivity extends AppCompatActivity {

    private TextView tvCantidadCajas;
    private int cantidadCajasRegistradas = 0; // Inicializar en 0

    private EditText etLote, etCortador, etVariedad, etClasificador;

    String destino ="";
    String etiqueta ="";
    String presentacion = "";

    String um = "CJ";

    String dia = "NORMAL";
    private CheckBox checkboxShowActivity;
    private int currentScanIndex = 0; // Índice del input actual a llenar
    private int idCorte;
    private DAOCorte daoCorte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_scan);

        // Inicializar el TextView para la cantidad de cajas
        tvCantidadCajas = findViewById(R.id.tvCantidadCajas);
        daoCorte = new DAOCorte();

        // Cargar cantidad inicial de cajas registradas
        cargarCantidadCajas();

        // Botón para abrir el detalle de las cajas
        ImageButton btnDetalleCajas = findViewById(R.id.btnDetalleCajas);

        // Obtener el ID del documento
         idCorte = getIntent().getIntExtra("id_corte", -1);

        btnDetalleCajas.setOnClickListener(v -> {
            if (idCorte != -1) {
                Intent intent = new Intent(FormularioScanActivity.this, DetalleCajasActivity.class);
                intent.putExtra("id_corte", idCorte); // Pasar el ID del documento
                startActivity(intent);
            } else {
                Toast.makeText(FormularioScanActivity.this, "ID del documento inválido", Toast.LENGTH_SHORT).show();
            }
        });



        // Inicializar los TextView para mostrar los datos del documento
        TextView tvEnvio = findViewById(R.id.tvEnvio);
        TextView tvFecha = findViewById(R.id.tvFecha);
        TextView tvFinca = findViewById(R.id.tvFinca);
        TextView tvCultivo = findViewById(R.id.tvCultivo);
        TextView tvCodigoPTE = findViewById(R.id.tvCodigoPTE);

        // Inicializar EditText y CheckBox
        etLote = findViewById(R.id.etLote);
        etCortador = findViewById(R.id.etCortador);
        etVariedad = findViewById(R.id.etVariedad);
        etClasificador = findViewById(R.id.etClasificador);
        checkboxShowActivity = findViewById(R.id.checkbox_show_activity);



        // Configurar validaciones en tiempo real
        setUpInputValidation();


        // Obtener el ID del documento seleccionado desde el Intent
        idCorte = getIntent().getIntExtra("id_corte", -1);
        String usuarioAutenticado = getIntent().getStringExtra("usuario");
        Log.d("FormularioScanActivity", "ID recibido: " + idCorte);
        String apuntador = usuarioAutenticado; // Aquí obtén el apuntador de SharedPreferences o una sesión



        if (idCorte != -1) {
            cargarDatosCorte(idCorte, apuntador, tvEnvio, tvFecha, tvFinca, tvCultivo, tvCodigoPTE);
        } else {
            Log.e("FormularioScanActivity", "ID del documento inválido.");
        }

        // Configurar los EditText para iniciar el escaneo
        setUpInputClickListeners();
    }

    private void cargarCantidadCajas() {
        // Obtener el ID del documento seleccionado desde el Intent
        idCorte = getIntent().getIntExtra("id_corte", -1);

        if (idCorte != -1) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    // Obtener la cantidad de cajas registradas desde la base de datos
                    int cantidad = daoCorte.obtenerCantidadCajas(idCorte);
                    runOnUiThread(() -> tvCantidadCajas.setText(String.valueOf(cantidad)));

                    // Actualizar la variable para futuras actualizaciones
                    cantidadCajasRegistradas = cantidad;
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "Error al cargar la cantidad de cajas: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }
            });
        } else {
            // Manejo de caso cuando el ID es inválido
            runOnUiThread(() -> {
                tvCantidadCajas.setText("0");
                Toast.makeText(this, "ID del documento inválido.", Toast.LENGTH_LONG).show();
            });
        }
    }



    private void actualizarCantidadCajas() {
        // Incrementar el contador de cajas y actualizar el TextView
        cantidadCajasRegistradas++;

        tvCantidadCajas.setText(String.valueOf(cantidadCajasRegistradas));
    }






    private void setUpInputValidation() {
        etLote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String lote = s.toString().trim();
                if (!lote.isEmpty()) {
                    validarCampo("lote", lote, etLote);
                }
            }
        });

        etCortador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String cortador = s.toString().trim();
                if (!cortador.isEmpty()) {
                    validarCampo("cortador", cortador, etCortador);
                }
            }
        });

        etVariedad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String variedad = s.toString().trim();
                if (!variedad.isEmpty()) {
                    validarCampo("variedad", variedad, etVariedad);
                }
            }
        });

        etClasificador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String clasificador = s.toString().trim();
                if (!clasificador.isEmpty()) {
                    validarCampo("clasificador", clasificador, etClasificador);
                }
            }
        });
    }

    private void validarCampo(String tipo, String valor, EditText editText) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                boolean valido = false;
                switch (tipo) {
                    case "lote":
                        valido = daoCorte.validarLote(valor);
                        break;
                    case "cortador":
                        valido = daoCorte.validarCortador(valor);
                        break;
                    case "variedad":
                        valido = daoCorte.validarVariedad(valor);
                        break;
                    case "clasificador":
                        valido = daoCorte.validarClasificador(valor);
                        break;
                }

                boolean finalValido = valido;
                runOnUiThread(() -> {
                    if (!finalValido) {
                        editText.setError("El " + tipo + " no existe. Por favor, verifique.");
                    } else {
                        editText.setError(null);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error al validar el " + tipo + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    private void cargarDatosCorte(int idCorte, String apuntador, TextView tvEnvio, TextView tvFecha,
                                  TextView tvFinca, TextView tvCultivo, TextView tvCodigoPTE) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                Log.d("FormularioScanActivity", "Intentando cargar datos del corte con ID: " + idCorte);
                Corte corte = daoCorte.leercorte(idCorte, apuntador);
                if (corte != null) {
                    Log.d("FormularioScanActivity", "Datos cargados: Envío=" + corte.getId_enc_corte() + ", Fecha=" + corte.getFecha());
                    runOnUiThread(() -> {
                        tvEnvio.setText("Envío: " + corte.getId_enc_corte());
                        tvFecha.setText("Fecha: " + corte.getFecha());
                        tvFinca.setText("Finca: " + corte.getNombrefinca());
                        tvCultivo.setText("Cultivo: " + corte.getNombre_cultivo());
                        tvCodigoPTE.setText("Código PTE: " + corte.getCodigo_pte());
                        destino = corte.getDestino();
                        etiqueta = corte.getEtiqueta();
                        presentacion = corte.getPresentacion();

                    });
                } else {
                    Log.e("FormularioScanActivity", "No se encontraron datos para el ID: " + idCorte);
                    runOnUiThread(() -> Toast.makeText(this, "No se encontraron datos del documento", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                Log.e("FormularioScanActivity", "Error al cargar los datos del corte: " + e.getMessage(), e);
                runOnUiThread(() -> Toast.makeText(this, "Error al cargar los datos: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }


    private void setUpInputClickListeners() {
        etLote.setOnClickListener(v -> startScan(0));
        etCortador.setOnClickListener(v -> startScan(1));
        etVariedad.setOnClickListener(v -> startScan(2));
        etClasificador.setOnClickListener(v -> startScan(3));
    }

    private void startScan(int index) {
        currentScanIndex = index; // Establecer cuál input será llenado
        if (checkboxShowActivity.isChecked()) {
            // Iniciar ScanActivity
            Intent intent = new Intent(this, ScanActivity.class);
            startActivityForResult(intent, 1);
        } else {
            // Realizar escaneo directo
            initiateDirectScan();
        }
    }

    private void initiateDirectScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Escanee un código de barras");
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CustomCaptureActivity.class); // Clase personalizada
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1 && data != null) {
            // Procesar datos de ScanActivity
            String scannedData = data.getStringExtra("scannedData");
            processScannedData(scannedData);
        } else if (result != null && result.getContents() != null) {
            // Escaneo directo
            processScannedData(result.getContents());
        }
    }

    private void processScannedData(String scannedData) {
        if (scannedData != null) {
            switch (currentScanIndex) {
                case 0:
                    etLote.setText(scannedData);
                    break;
                case 1:
                    etCortador.setText(scannedData);
                    break;
                case 2:
                    etVariedad.setText(scannedData);
                    break;
                case 3:
                    etClasificador.setText(scannedData);
                    break;
            }
            currentScanIndex++;

            // Continuar escaneando o registrar si están llenos
            if (currentScanIndex < 4) {
                startScan(currentScanIndex);
            } else {
                registerData();
            }
        }
    }

    private void registerData() {
        String lote = etLote.getText().toString();
        String cortador = etCortador.getText().toString();
        String variedad = etVariedad.getText().toString();
        String clasificador = etClasificador.getText().toString();

        // Obtener los datos del documento mostrados en pantalla
        TextView tvEnvio = findViewById(R.id.tvEnvio);
        TextView tvFecha = findViewById(R.id.tvFecha);
        TextView tvFinca = findViewById(R.id.tvFinca);
        TextView tvCultivo = findViewById(R.id.tvCultivo);
        TextView tvCodigoPTE = findViewById(R.id.tvCodigoPTE);

        String idDocumento = tvEnvio.getText().toString().replace("Envío: ", "").trim();
        String fecha = tvFecha.getText().toString().replace("Fecha: ", "").trim();
        String finca = tvFinca.getText().toString().replace("Finca: ", "").trim();
        String cultivo = tvCultivo.getText().toString().replace("Cultivo: ", "").trim();
        String codigoPTE = tvCodigoPTE.getText().toString().replace("Código PTE: ", "").trim();

        if (!lote.isEmpty() && !cortador.isEmpty() && !variedad.isEmpty() && !clasificador.isEmpty()) {
            // Ejecutar la validación en un hilo de fondo
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    // Validar el lote
                    boolean loteValido = daoCorte.validarLote(lote);
                    boolean cortadorValido = daoCorte.validarCortador(cortador);
                    boolean variedadValido = daoCorte.validarVariedad(variedad);
                    boolean clasificadorValido = daoCorte.validarClasificador(clasificador);

                    runOnUiThread(() -> {
                        if (loteValido && cortadorValido && variedadValido && clasificadorValido ) {
                            // Si el lote es válido, continuar con el registro
                            Corte corte = new Corte();
                            // Asignar los datos del formulario
                            corte.setLote(lote);
                            corte.setCodigo_cortador(cortador);
                            corte.setVariedad(variedad);
                            corte.setCodigo_clasificador(clasificador);
                            corte.setDestino(destino);
                            corte.setEtiqueta(etiqueta);
                            corte.setPresentacion(presentacion);
                            corte.setUnidad_medida(um);

                            // Asignar los datos del documento
                            corte.setId_enc_corte(Integer.parseInt(idDocumento)); // ID del documento
                            corte.setFecha(fecha);
                            corte.setNombrefinca(finca);
                            corte.setNombre_cultivo(cultivo);
                            corte.setCodigo_pte(codigoPTE);
                            corte.setDia("NORMAL"); // Configuración por defecto

                            etLote.setError(null);
                            etCortador.setError(null);
                            etVariedad.setError(null);
                            etClasificador.setError(null);

                            // Registrar los datos
                            registrarCorte(corte);
                        } else {
                            // Mostrar mensajes flotantes y errores visuales
                            if (!loteValido) {
                                mostrarMensajeDialog("El lote no existe. Por favor, corríjalo.");
                                etLote.setError("El lote no existe. Verifique el dato.");
                            }
                            if (!cortadorValido) {
                                mostrarMensajeDialog("El cortador no existe. Por favor, corríjalo.");
                                etCortador.setError("El cortador no existe. Verifique el dato.");
                            }
                            if (!variedadValido) {
                                mostrarMensajeDialog("La variedad no existe. Por favor, corríjalo.");
                                etVariedad.setError("La variedad no existe. Verifique el dato.");
                            }
                            if (!clasificadorValido) {
                                mostrarMensajeDialog("El clasificador no existe. Por favor, corríjalo.");
                                etClasificador.setError("El clasificador  no existe. Verifique el dato.");
                            }

                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Error al validar el lote: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            });
        } else {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
        }
    }
    private void mostrarMensajeDialog(String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle("Advertencia")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void registrarCorte(Corte corte) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                int result = daoCorte.RegistrarEscanCajasCorte(corte);
                runOnUiThread(() -> {
                    if (result > 0) {
                        Toast.makeText(this, "Datos registrados exitosamente", Toast.LENGTH_SHORT).show();
                        // Eliminar cualquier error en el EditText del lote
                        etLote.setError(null);
                        etCortador.setError(null);
                        etVariedad.setError(null);
                        etClasificador.setError(null);
                        // Actualizar la cantidad de cajas
                        actualizarCantidadCajas();

                        limpiarCampos();
                        currentScanIndex = 0; // Reiniciar el índice
                    } else {
                        Toast.makeText(this, "Error al registrar los datos", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private String obtenerFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void limpiarCampos() {
        etLote.setText("");
        etCortador.setText("");
        etVariedad.setText("");
        etClasificador.setText("");
    }



}
