package com.example.irbis;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ForgotPassword extends AppCompatActivity {

    private EditText loginEditText;
    private Button resetButton;
    private TextView textViewLogin;
    private ImageView exitButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        loginEditText = findViewById(R.id.loginEditText);
        resetButton = findViewById(R.id.resetButton);
        textViewLogin = findViewById(R.id.textViewLogin);
        exitButton = findViewById(R.id.exitButton);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetButton.setEnabled(false);
                resetButton.setTextColor(Color.GRAY);
                attemptPasswordReset();
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this, Authorization.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this, Start.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private void attemptPasswordReset() {
        String email = loginEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            loginEditText.setError("Пожалуйста, введите свой адрес электронной почты");
            resetButton.setEnabled(true);
            resetButton.setTextColor(Color.BLACK);
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEditText.setError("Пожалуйста, введите действительный адрес электронной почты");
            resetButton.setEnabled(true);
            resetButton.setTextColor(Color.BLACK);
            return;
        }

        sendPasswordResetEmail(email);
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showSuccessMessage(email);
                        } else {
                            showErrorMessage(task.getException());
                        }
                        resetButton.setEnabled(true);
                        resetButton.setTextColor(Color.BLACK);
                    }
                });
    }

    private void showSuccessMessage(String email) {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Успешно!")
                .setContentText("Инструкции по сбросу пароля отправлены на " + email)
                .setConfirmText("OK")
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    loginEditText.setText("");
                })
                .show();
    }

    private void showErrorMessage(Exception exception) {
        String errorMessage = "Не удалось отправить электронное письмо для сброса";
        if (exception != null) {
            errorMessage = Objects.requireNonNull(exception.getMessage());
        }
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Ошибка")
                .setContentText(errorMessage)
                .setConfirmText("OK")
                .show();
    }

    private void navigateToAuthorization() {
        Intent intent = new Intent(ForgotPassword.this, Authorization.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateToAuthorization();
    }
}