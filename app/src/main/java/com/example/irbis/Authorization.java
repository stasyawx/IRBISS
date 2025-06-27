package com.example.irbis;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Authorization extends AppCompatActivity {

    private EditText loginEditText, passwordEditText;
    private Button loginButton;
    private TextView textViewRegistr, passwordTextView;
    private ImageView exitButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authorization);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("User");
        initViews();

        loginButton.setOnClickListener(v -> {
            if (!isNetworkAvailable()) {
                showErrorDialog("Нет подключения к интернету");
                return;
            }

            attemptLogin();
        });

        setupClickListeners();
    }

    private void showErrorDialog(String message) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Ошибка")
                .setContentText(message)
                .setConfirmText("OK")
                .show();
    }

    private void showSuccessDialog(String message, Runnable onConfirm) {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Успешно!")
                .setContentText(message)
                .setConfirmClickListener(sDialog -> {
                    sDialog.dismissWithAnimation();
                    if (onConfirm != null) onConfirm.run();
                })
                .show();
    }

    private void initViews() {
        loginEditText = findViewById(R.id.loginEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        textViewRegistr = findViewById(R.id.textViewRegistr);
        passwordTextView = findViewById(R.id.passwordTextView);
        exitButton = findViewById(R.id.exitButton);
    }

    private void attemptLogin() {
        clearErrors();

        String userLogin = loginEditText.getText().toString().trim();
        String userPassword = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(userLogin)) {
            loginEditText.setError("Введите email");
            return;
        }

        if (TextUtils.isEmpty(userPassword)) {
            passwordEditText.setError("Введите пароль");
            return;
        }

        if (!TextUtils.isEmpty(userLogin)) {
            if (isValidEmail(userLogin)) {
                signInWithEmail(userLogin, userPassword);
            } else {
                loginEditText.setError("Неверный формат email");
            }
        }

    }

    private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            mDatabase.child(firebaseUser.getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                User user = snapshot.getValue(User.class);
                                                if (user != null) {
                                                    UserData.getInstance().setUserData(
                                                            user.getId(),
                                                            user.getEmail(),
                                                            user.getFirstName(),
                                                            user.getLastName(),
                                                            user.getMiddleName(),
                                                            user.getBirthDate(),
                                                            user.getGender(),
                                                            user.getBonusPoints(),
                                                            user.getRoleId()
                                                    );
                                                    checkEmailVerification();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            new SweetAlertDialog(Authorization.this, SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Ошибка")
                                                    .setContentText("Не удалось загрузить данные пользователя")
                                                    .setConfirmText("OK")
                                                    .show();
                                        }
                                    });
                        }
                    } else {
                        handleLoginError(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    private void checkEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.isEmailVerified()) {
                mDatabase.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User userData = snapshot.getValue(User.class);
                            if (userData != null) {
                                showSuccessDialog("Авторизация прошла успешно!", () -> {
                                    if (userData.getRoleId() == 1) {
                                        navigateToAdminHome();
                                    } else {
                                        navigateToHome();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showErrorDialog("Ошибка загрузки данных пользователя");
                    }
                });
            } else {
                new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Email не подтвержден")
                        .setContentText("Подтвердите email. Проверьте вашу почту")
                        .setConfirmText("OK")
                        .setConfirmClickListener(sDialog -> {
                            sDialog.dismissWithAnimation();
                            mAuth.signOut();
                        })
                        .show();
            }
        }
    }

    private void handleLoginError(String error) {
        passwordEditText.setError("Ошибка входа");
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Ошибка входа")
                .setContentText(getHumanReadableError(error))
                .show();
    }

    private String getHumanReadableError(String error) {
        if (error.contains("password is invalid")) {
            return "Неверный пароль";
        } else if (error.contains("no user record")) {
            return "Пользователь не найден";
        } else if (error.contains("network error")) {
            return "Проблемы с интернет-соединением";
        }
        return "Ошибка: " + error;
    }

    private void setupClickListeners() {

        findViewById(R.id.textViewRegistr).setOnClickListener(v -> {
            startActivity(new Intent(this, Registration.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            overridePendingTransition(0, 0);
            finish();
        });

        findViewById(R.id.passwordTextView).setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPassword.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            overridePendingTransition(0, 0);
            finish();
        });

        findViewById(R.id.exitButton).setOnClickListener(v -> {
            startActivity(new Intent(this, Start.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void navigateToHome() {
        startActivity(new Intent(this, Home.class)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        overridePendingTransition(0, 0);
        finish();
    }

    private void navigateToAdminHome() {
        startActivity(new Intent(this, AdminHome.class)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        overridePendingTransition(0, 0);
        finish();
    }

    private void clearErrors() {
        loginEditText.setError(null);
        passwordEditText.setError(null);
    }
}