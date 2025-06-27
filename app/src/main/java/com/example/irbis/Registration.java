package com.example.irbis;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Registration extends AppCompatActivity {

    private EditText nameEditText, loginEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView textViewLogin;
    private ImageView exitButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private final String USER_KEY = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference(USER_KEY);

        initViews();

        setupListeners();
    }

    private void initViews() {
        nameEditText = findViewById(R.id.nameEditText);
        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        textViewLogin = findViewById(R.id.textViewLogin);
        exitButton = findViewById(R.id.exitButton);
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> {
            clearErrors();
            String name = nameEditText.getText().toString().trim();
            String email = loginEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (validateInputs(name, email, password, confirmPassword)) {
                registerUser(name, email, password);
            }
        });

        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(Registration.this, Authorization.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });

        exitButton.setOnClickListener(v -> {
            Intent intent = new Intent(Registration.this, Start.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private boolean validateInputs(String name, String email, String password, String confirmPassword) {
        boolean valid = true;

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Введите имя");
            valid = false;
        } else if (!name.matches("[а-яА-ЯёЁa-zA-Z\\s]+")) {
            nameEditText.setError("Имя должно содержать только буквы");
            valid = false;
        }

        if (TextUtils.isEmpty(email)) {
            loginEditText.setError("Введите email");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEditText.setError("Неверный формат email");
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Введите пароль");
            valid = false;
        } else if (password.length() < 8) {
            passwordEditText.setError("Минимум 8 символов");
            valid = false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Пароли не совпадают");
            valid = false;
        }

        return valid;
    }

    private void registerUser(String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            sendEmailVerification(user);
                            String userId = user.getUid();
                            User newUser = new User(email, password, name, userId);
                            newUser.setRoleId(2); // Явно устанавливаем роль клиента
                            newUser.setBonusPoints(0); // Явно устанавливаем 0 баллов
                            mDatabase.child(userId).setValue(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        new SweetAlertDialog(Registration.this, SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Успешно!")
                                                .setContentText("Регистрация успешна! Проверьте email для подтверждения")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(sDialog -> {
                                                    sDialog.dismissWithAnimation();
                                                    clearFields();
                                                    navigateToAuthorization();
                                                })
                                                .show();
                                    })
                                    .addOnFailureListener(e -> {
                                        new SweetAlertDialog(Registration.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Ошибка")
                                                .setContentText("Ошибка сохранения данных: " + e.getMessage())
                                                .setConfirmText("OK")
                                                .show();
                                    });
                        }
                    } else {
                        new SweetAlertDialog(Registration.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Ошибка регистрации")
                                .setContentText(Objects.requireNonNull(task.getException()).getMessage())
                                .setConfirmText("OK")
                                .show();
                    }
                });
    }

    private void navigateToAuthorization() {
        Intent intent = new Intent(this, Authorization.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        new SweetAlertDialog(Registration.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Письмо отправлено")
                                .setContentText("Письмо с подтверждением отправлено на " + user.getEmail())
                                .setConfirmText("OK")
                                .show();
                    } else {
                        new SweetAlertDialog(Registration.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Ошибка")
                                .setContentText("Не удалось отправить письмо: " +
                                        Objects.requireNonNull(task.getException()).getMessage())
                                .setConfirmText("OK")
                                .show();
                    }
                });
    }

    private void clearFields() {
        nameEditText.setText("");
        loginEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
    }

    private void clearErrors() {
        nameEditText.setError(null);
        loginEditText.setError(null);
        passwordEditText.setError(null);
        confirmPasswordEditText.setError(null);
    }
}