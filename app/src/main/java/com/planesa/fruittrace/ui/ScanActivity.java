package com.planesa.fruittrace.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.planesa.fruittrace.R;

public class ScanActivity extends AppCompatActivity {

    private TextView scannedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Inicializar el TextView para mostrar el valor escaneado
        scannedTextView = findViewById(R.id.scanned_text);
        scannedTextView.setVisibility(View.GONE); // Ocultarlo inicialmente

        // Iniciar el escaneo
        initiateScan();
    }

    private void initiateScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Escanee un código de barras");
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(CustomCaptureActivity.class); // Clase personalizada
        integrator.setOrientationLocked(true); // Bloquear orientación en vertical
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && result.getContents() != null) {
            // Mostrar el texto escaneado en el TextView
            scannedTextView.setText(result.getContents());
            scannedTextView.setVisibility(View.VISIBLE);

            // Enviar el dato escaneado de vuelta
            Intent intent = new Intent();
            intent.putExtra("scannedData", result.getContents());
            setResult(Activity.RESULT_OK, intent);

            // Retraso antes de finalizar para que el usuario pueda ver el resultado
            scannedTextView.postDelayed(this::finish, 1500); // 1.5 segundos
        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }
}
