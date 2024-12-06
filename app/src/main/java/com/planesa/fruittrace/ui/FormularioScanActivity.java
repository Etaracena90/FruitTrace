package com.planesa.fruittrace.ui;

import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

    String destino = "";
    String etiqueta = "";
    String presentacion = "";
    String um = "CJ";
    String dia = "NORMAL";
    private TextView tvCantidadCajas;
    private double cantidadCajasRegistradas = 0.0; // Inicializar en 0
    private CheckBox cbGuardarManual; // Declarar la variable para el CheckBox
    private EditText etCantidad; // Campo para ingresar la cantidad manual
    private Button btnGuardarManual; // Declaración del botón
    private EditText etLote, etCortador, etVariedad, etClasificador;
    private CheckBox checkboxShowActivity;
    private int currentScanIndex = 0; // Índice del input actual a llenar
    private int idCorte;
    private DAOCorte daoCorte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_scan);


        // Referencias a los nuevos elementos
        btnGuardarManual = findViewById(R.id.btnGuardarManual);
        cbGuardarManual = findViewById(R.id.cbGuardarManual);
        etCantidad = findViewById(R.id.etCantidad);
        LinearLayout linearManualSave = findViewById(R.id.linearManualSave);

        btnGuardarManual.setOnClickListener(v -> {
            // Validar que el checkbox esté activado
            if (!cbGuardarManual.isChecked()) {
                Toast.makeText(this, "Activa el guardado manual antes de registrar.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar la cantidad ingresada
            String cantidadStr = etCantidad.getText().toString().trim();
            if (cantidadStr.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa una cantidad antes de guardar.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double cantidadManual = Double.parseDouble(cantidadStr);
                // Validar que la cantidad esté en el rango permitido
                if (cantidadManual < 0.01 || cantidadManual > 0.99) {
                    Toast.makeText(this, "La cantidad debe estar entre 0.01 y 0.99.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Llama al método registerData con la cantidad válida
                registerData(cantidadManual);

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Por favor ingresa un número válido.", Toast.LENGTH_SHORT).show();
            }
        });


        // Configurar el comportamiento del CheckBox
        cbGuardarManual.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Mostrar el contenedor de cantidad y botón guardar
                linearManualSave.setVisibility(View.VISIBLE);
            } else {
                // Ocultar el contenedor y limpiar el campo de cantidad
                linearManualSave.setVisibility(View.GONE);
                EditText etCantidad = findViewById(R.id.etCantidad);
                etCantidad.setText(""); // Limpiar la cantidad al desactivar el CheckBox
            }
        });


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
                    Double cantidad = daoCorte.obtenerCantidadCajas(idCorte);

                    // Formatear la cantidad a dos decimales
                    DecimalFormat decimalFormat = new DecimalFormat("#.00");
                    String cantidadFormateada = decimalFormat.format(cantidad);

                    runOnUiThread(() -> {
                        // Mostrar la cantidad formateada en el TextView
                        tvCantidadCajas.setText(cantidadFormateada);
                        // Actualizar la variable para futuras actualizaciones
                        cantidadCajasRegistradas = cantidad;
                    });

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
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Obtener la cantidad total desde la base de datos
                double nuevaCantidad = daoCorte.obtenerCantidadCajas(idCorte); // Método que consulta la base de datos

                runOnUiThread(() -> {
                    // Formatear el número a 2 decimales
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    String cantidadFormateada = decimalFormat.format(nuevaCantidad);

                    // Actualizar el TextView con la cantidad formateada
                    tvCantidadCajas.setText(cantidadFormateada);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Error al actualizar la cantidad de cajas: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }


    private void setUpInputValidation() {
        etLote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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

            // Continuar escaneando o validar si se debe registrar
            if (currentScanIndex < 4) {
                startScan(currentScanIndex);
            } else {
                // Verificar si el CheckBox está activo
                if (cbGuardarManual.isChecked()) {
                    // Obtener la cantidad manual ingresada
                    String cantidadStr = etCantidad.getText().toString().trim();
                    if (cantidadStr.isEmpty()) {
                        mostrarMensajeDialog("Por favor, ingresa una cantidad antes de guardar.");
                        return;
                    }

                    try {
                        double cantidadManual = Double.parseDouble(cantidadStr);

                        if (cantidadManual <= 0.00 || cantidadManual > 0.99) {
                                Toast.makeText(this, "La cantidad debe estar entre 0.01 y 0.99.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Registrar datos con cantidad manual
                        registerData(cantidadManual);
                    } catch (NumberFormatException e) {
                        mostrarMensajeDialog("Por favor, ingresa una cantidad válida.");
                    }
                } else {
                    // Registrar datos con cantidad por defecto (1)
                    registerData(1.0);
                }
            }
        }
    }


    private void registerData(double cantidadManual) {
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
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    // Validar los datos
                    boolean loteValido = daoCorte.validarLote(lote);
                    boolean cortadorValido = daoCorte.validarCortador(cortador);
                    boolean variedadValido = daoCorte.validarVariedad(variedad);
                    boolean clasificadorValido = daoCorte.validarClasificador(clasificador);

                    runOnUiThread(() -> {
                        if (loteValido && cortadorValido && variedadValido && clasificadorValido) {
                            // Obtener la cantidad dependiendo del estado del CheckBox
                            double cantidad = 1.0; // Por defecto, cantidad 1
                            if (cbGuardarManual.isChecked()) {
                                String cantidadStr = etCantidad.getText().toString().trim();
                                if (cantidadStr.isEmpty()) {
                                    mostrarMensajeDialog("Por favor, ingresa una cantidad antes de guardar.");
                                    return;
                                }
                                try {
                                    cantidad = Double.parseDouble(cantidadStr);
                                    if (cantidad <= 0.00 || cantidad > 0.99)   {
                                        mostrarMensajeDialog("Cantidad no Permitida debe estar entre 0.01 y 0.99");
                                        return;
                                    }
                                } catch (NumberFormatException e) {
                                    mostrarMensajeDialog("Por favor, ingresa una cantidad válida.");
                                    return;
                                }
                            }

                            // Crear objeto Corte
                            Corte corte = new Corte();
                            corte.setLote(lote);
                            corte.setCodigo_cortador(cortador);
                            corte.setVariedad(variedad);
                            corte.setCodigo_clasificador(clasificador);
                            corte.setDestino(destino);
                            corte.setEtiqueta(etiqueta);
                            corte.setPresentacion(presentacion);
                            corte.setUnidad_medida(um);
                            corte.setCantidad(cantidad);
                            corte.setId_enc_corte(Integer.parseInt(idDocumento));
                            corte.setFecha(fecha);
                            corte.setNombrefinca(finca);
                            corte.setNombre_cultivo(cultivo);
                            corte.setCodigo_pte(codigoPTE);
                            corte.setDia("NORMAL");
                            corte.setCantidad(cantidadManual); // Aquí asignamos la cantidad

                            // Limpiar errores previos
                            etLote.setError(null);
                            etCortador.setError(null);
                            etVariedad.setError(null);
                            etClasificador.setError(null);


                            // Registrar los datos
                            registrarCorte(corte);
                        } else {
                            // Mostrar errores en los campos no válidos
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
                                etClasificador.setError("El clasificador no existe. Verifique el dato.");
                            }
                        }
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "Error al validar los datos: " + e.getMessage(), Toast.LENGTH_LONG).show());
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
                String usuarioLogeando = getIntent().getStringExtra("usuario");
                int result = daoCorte.RegistrarEscanCajasCorte(corte,usuarioLogeando);
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
        etCantidad.setText("");
    }


}
