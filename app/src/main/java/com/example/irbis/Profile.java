package com.example.irbis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        UserData userData = UserData.getInstance();
        TextView userNameTextView = findViewById(R.id.userNameTextView);

        if (userData != null) {
            userNameTextView.setText(userData.getFirstName());
        }

        TextView exitTextView = findViewById(R.id.exitTextView);
        exitTextView.setOnClickListener(v -> showLogoutConfirmationDialog());

        TextView deletePrompt = findViewById(R.id.deletePrompt);
        deletePrompt.setOnClickListener(v -> showDeleteAccountConfirmationDialog());

        setupNavigation();
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Выход из профиля")
                .setMessage("Вы уверены, что хотите выйти из своего аккаунта?")
                .setPositiveButton("Да", (dialog, which) -> logoutUser())
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showDeleteAccountConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Удаление аккаунта")
                .setMessage("Вы уверены, что хотите удалить свой аккаунт? Это действие нельзя отменить.")
                .setPositiveButton("Удалить", (dialog, which) -> deleteUserAccount())
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void logoutUser() {
        mAuth.signOut();
        navigateToStartActivity();
    }

    private void deleteUserAccount() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child("Users").child(userId).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            currentUser.delete()
                                    .addOnCompleteListener(authTask -> {
                                        if (authTask.isSuccessful()) {
                                            mAuth.signOut();
                                            showSuccessDialog("Аккаунт успешно удален");
                                        } else {
                                            showErrorDialog("Ошибка при удалении аккаунта: " +
                                                    authTask.getException().getMessage());
                                        }
                                    });
                        } else {
                            showErrorDialog("Ошибка при удалении данных пользователя");
                        }
                    });
        }
    }

    private void showSuccessDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Успешно")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> navigateToStartActivity())
                .show();
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Ошибка")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void navigateToStartActivity() {
        Intent intent = new Intent(Profile.this, Start.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setupNavigation() {
        ImageView editprofile = findViewById(R.id.editprofile);
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, EditProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        ImageView homeImageView = findViewById(R.id.homeImageView);
        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Home.class);
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
                Intent intent = new Intent(Profile.this, History.class);
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
                Intent intent = new Intent(Profile.this, Profile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        TextView helpTextView = findViewById(R.id.helpTextView);
        helpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, HelpSupport.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        TextView contactTextView = findViewById(R.id.contactTextView);
        contactTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, ContactUs.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        TextView privacyTextView = findViewById(R.id.privacyTextView);
        privacyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, PrivacyPolicy.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }
}