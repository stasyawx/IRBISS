package com.example.irbis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EditFuel extends AppCompatActivity {

    private FuelService fuelService;
    private Spinner fuelTypeSpinner;
    private EditText priceInput;
    private EditText descriptionInput;
    private Spinner activeSpinner;
    private List<Fuel> fuels = new ArrayList<>();
    private ArrayAdapter<Fuel> fuelAdapter;
    private ArrayAdapter<CharSequence> activeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_fuel);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fuelService = new FuelService();

        initializeViews();
        setupAdapters();
        loadFuels();
        setupSaveButton();
        setupNavigation();
    }

    private void initializeViews() {
        fuelTypeSpinner = findViewById(R.id.fuelTypeSpinner);
        priceInput = findViewById(R.id.litersInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        activeSpinner = findViewById(R.id.activeSpinner);
    }

    private void setupAdapters() {
        fuelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fuels);
        fuelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fuelTypeSpinner.setAdapter(fuelAdapter);

        activeAdapter = ArrayAdapter.createFromResource(this,
                R.array.active_options, android.R.layout.simple_spinner_item);
        activeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activeSpinner.setAdapter(activeAdapter);
    }

    private void loadFuels() {
        fuelService.getFuelsReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fuels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Fuel fuel = snapshot.getValue(Fuel.class);
                    if (fuel != null) {
                        fuels.add(fuel);
                    }
                }
                fuelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditFuel.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSaveButton() {
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            Fuel selectedFuel = (Fuel) fuelTypeSpinner.getSelectedItem();
            if (selectedFuel != null) {
                try {
                    double newPrice = Double.parseDouble(priceInput.getText().toString());
                    String newDescription = descriptionInput.getText().toString();
                    boolean isActive = activeSpinner.getSelectedItemPosition() == 0;

                    selectedFuel.setPrice(newPrice);
                    selectedFuel.setDescription(newDescription);
                    selectedFuel.setActive(isActive);

                    fuelService.updateFuel(selectedFuel);
                    Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Введите корректную цену", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Выберите тип топлива", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupNavigation() {
        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditFuel.this, AdminHome.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        ImageView exitButton = findViewById(R.id.exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditFuel.this, AdminHome.class);
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
                Intent intent = new Intent(EditFuel.this, AdminHome.class);
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
                Intent intent = new Intent(EditFuel.this, AdminHistory.class);
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
                Intent intent = new Intent(EditFuel.this, AdminProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }
}