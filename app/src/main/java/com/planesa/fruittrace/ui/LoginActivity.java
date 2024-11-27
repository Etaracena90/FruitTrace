package com.planesa.fruittrace.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.planesa.fruittrace.R;
import com.planesa.fruittrace.dao.DAOLogin;
import com.planesa.fruittrace.model.Users;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private DAOLogin daoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        daoLogin = new DAOLogin();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    authenticateUser(username, password);
                }
            }
        });
    }

    private void authenticateUser(String username, String password) {
        executorService.execute(() -> {
            Users user = new Users();
            user.setUsers(username);
            user.setPassword(password);
            Users result = daoLogin.identificar(user);

            mainHandler.post(() -> {
                if (result != null) {
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    // Agregar log para verificar el nombre de usuario antes de pasar a MainActivity
                    Log.d("LoginActivity", "Nombre de usuario antes de pasar a MainActivity: " + username);
                    if (username != null && !username.isEmpty()) {
                        // Navegar a MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("usuario", username);
                        startActivity(intent);
                        finish(); // Finalizar LoginActivity para que no se pueda volver atrás con el botón de retroceso
                    } else {
                        Toast.makeText(LoginActivity.this, "Usuario no válido", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
