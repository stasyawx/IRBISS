package com.example.irbis;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminEditProfile extends AppCompatActivity {

    private EditText nameEditText, familiaEditText, otchEditText, loginEditText, birthDateEditText;
    private Button saveButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("User");
        currentUser = mAuth.getCurrentUser();

        initViews();
        loadUserData();
        setupListeners();
    }

    private void initViews() {
        nameEditText = findViewById(R.id.nameEditText);
        familiaEditText = findViewById(R.id.familiaEditText);
        otchEditText = findViewById(R.id.otchEditText);
        loginEditText = findViewById(R.id.loginEditText);
        birthDateEditText = findViewById(R.id.birthDateEditText);
        saveButton = findViewById(R.id.saveButton);
        birthDateEditText.setOnClickListener(v -> showDatePickerDialog());
    }

    private void loadUserData() {
        if (currentUser == null) return;

        UserData userData = UserData.getInstance();
        if (userData != null && userData.getEmail() != null) {
            displayUserData(userData);
        }

        mDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

                        displayUserData(user);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showErrorDialog("Ошибка загрузки данных");
            }
        });
    }

    private void displayUserData(UserData userData) {
        nameEditText.setText(userData.getFirstName());
        familiaEditText.setText(userData.getLastName());
        otchEditText.setText(userData.getMiddleName());
        loginEditText.setText(userData.getEmail());
        birthDateEditText.setText(userData.getBirthDate());
    }

    private void displayUserData(User user) {
        nameEditText.setText(user.getFirstName());
        familiaEditText.setText(user.getLastName());
        otchEditText.setText(user.getMiddleName());
        loginEditText.setText(user.getEmail());
        birthDateEditText.setText(user.getBirthDate());
    }

    private void setupListeners() {
        saveButton.setOnClickListener(v -> validateAndSaveData());

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminEditProfile.this, AdminProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        setupNavigation();
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                AdminEditProfile.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format(Locale.getDefault(), "%02d.%02d.%d",
                            selectedDay, selectedMonth + 1, selectedYear);
                    birthDateEditText.setText(formattedDate);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void validateAndSaveData() {
        String name = nameEditText.getText().toString().trim();
        String familia = familiaEditText.getText().toString().trim();
        String otch = otchEditText.getText().toString().trim();
        String email = loginEditText.getText().toString().trim();
        String birthDate = birthDateEditText.getText().toString().trim();

        boolean valid = true;

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Введите имя");
            valid = false;
        } else if (!name.matches("[а-яА-ЯёЁa-zA-Z\\s]+")) {
            nameEditText.setError("Имя должно содержать только буквы");
            valid = false;
        }

        if (!TextUtils.isEmpty(familia) && !familia.matches("[а-яА-ЯёЁa-zA-Z\\s]+")) {
            familiaEditText.setError("Фамилия должна содержать только буквы");
            valid = false;
        }

        if (!TextUtils.isEmpty(otch) && !otch.matches("[а-яА-ЯёЁa-zA-Z\\s]+")) {
            otchEditText.setError("Отчество должно содержать только буквы");
            valid = false;
        }

        if (TextUtils.isEmpty(email)) {
            loginEditText.setError("Введите email");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEditText.setError("Неверный формат email");
            valid = false;
        }

        if (!TextUtils.isEmpty(birthDate)) {
            try {
                String[] dateParts = birthDate.split("\\.");
                if (dateParts.length != 3) {
                    birthDateEditText.setError("Неверный формат даты (дд.мм.гггг)");
                    valid = false;
                } else {
                    int day = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]) - 1; // Месяцы в Calendar начинаются с 0
                    int year = Integer.parseInt(dateParts[2]);

                    Calendar birthCalendar = Calendar.getInstance();
                    birthCalendar.set(year, month, day);

                    Calendar today = Calendar.getInstance();
                    Calendar minAdultDate = Calendar.getInstance();
                    minAdultDate.add(Calendar.YEAR, -18);

                    if (birthCalendar.after(today)) {
                        birthDateEditText.setError("Дата рождения не может быть в будущем");
                        valid = false;
                    } else if (birthCalendar.after(minAdultDate)) {
                        birthDateEditText.setError("Пользователь должен быть старше 18 лет");
                        valid = false;
                    }
                }
            } catch (NumberFormatException e) {
                birthDateEditText.setError("Неверный формат даты (дд.мм.гггг)");
                valid = false;
            }
        }

        if (!valid) return;

        if (!email.equals(UserData.getInstance().getEmail())) {
            showPasswordDialog(email, name, familia, otch, birthDate);
        } else {
            saveUserData(name, familia, otch, email, birthDate);
        }
    }

    private void showPasswordDialog(String newEmail, String name, String familia, String otch, String birthDate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Подтверждение смены email");
        builder.setMessage("Для изменения email введите ваш текущий пароль");

        final EditText passwordInput = new EditText(this);
        passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(passwordInput);

        builder.setPositiveButton("Подтвердить", (dialog, which) -> {
            String password = passwordInput.getText().toString().trim();
            if (!TextUtils.isEmpty(password)) {
                reauthenticateAndUpdateEmail(password, newEmail, name, familia, otch, birthDate);
            } else {
                showWarningDialog("Введите пароль");
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void reauthenticateAndUpdateEmail(String password, String newEmail,
                                              String name, String familia, String otch, String birthDate) {
        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), password);

        currentUser.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        saveUserData(name, familia, otch, currentUser.getEmail(), birthDate);
                        currentUser.verifyBeforeUpdateEmail(newEmail)
                                .addOnCompleteListener(emailTask -> {
                                    if (emailTask.isSuccessful()) {
                                        mDatabase.child(currentUser.getUid()).child("email").setValue(newEmail);
                                        UserData.getInstance().setUserData(
                                                currentUser.getUid(),
                                                newEmail,
                                                name,
                                                familia,
                                                otch,
                                                birthDate,
                                                UserData.getInstance().getGender(),
                                                UserData.getInstance().getBonusPoints(),
                                                UserData.getInstance().getRoleId()
                                        );

                                        new SweetAlertDialog(AdminEditProfile.this, SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Успешно")
                                                .setContentText("Письмо с подтверждением отправлено на новый email. Все остальные данные сохранены.")
                                                .show();
                                    } else {
                                        showErrorDialog("Ошибка при отправке письма подтверждения: " +
                                                emailTask.getException().getMessage());
                                    }
                                });
                    } else {
                        showErrorDialog("Неверный пароль. Попробуйте снова.");
                    }
                });
    }

    private void saveUserData(String name, String familia, String otch, String email, String birthDate) {
        if (currentUser == null) return;

        User updatedUser = new User();
        updatedUser.setId(currentUser.getUid());
        updatedUser.setEmail(email);
        updatedUser.setFirstName(name);
        updatedUser.setLastName(familia);
        updatedUser.setMiddleName(otch);
        updatedUser.setBirthDate(birthDate);
        updatedUser.setGender(UserData.getInstance().getGender());
        updatedUser.setBonusPoints(UserData.getInstance().getBonusPoints());
        updatedUser.setRoleId(UserData.getInstance().getRoleId());

        mDatabase.child(currentUser.getUid()).setValue(updatedUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserData.getInstance().setUserData(
                                currentUser.getUid(),
                                email,
                                name,
                                familia,
                                otch,
                                birthDate,
                                UserData.getInstance().getGender(),
                                UserData.getInstance().getBonusPoints(),
                                UserData.getInstance().getRoleId()
                        );

                        new SweetAlertDialog(AdminEditProfile.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Успешно")
                                .setContentText("Данные сохранены")
                                .setConfirmClickListener(sDialog -> {
                                    sDialog.dismissWithAnimation();
                                })
                                .show();
                    } else {
                        showErrorDialog("Ошибка сохранения");
                    }
                });
    }

    private void showErrorDialog(String message) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Ошибка")
                .setContentText(message)
                .show();
    }

    private void showWarningDialog(String message) {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Внимание")
                .setContentText(message)
                .show();
    }

    private void setupNavigation() {
        ImageView homeImageView = findViewById(R.id.homeImageView);
        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminEditProfile.this, AdminHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        ImageView historyImageView = findViewById(R.id.historyImageView);
        historyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminEditProfile.this, AdminHistory.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        ImageView profileImageView = findViewById(R.id.profileImageView);
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminEditProfile.this, AdminProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }
}