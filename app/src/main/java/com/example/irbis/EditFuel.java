package com.example.irbis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

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
        fuelAdapter = new ArrayAdapter<Fuel>(this, android.R.layout.simple_spinner_item, fuels) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(ContextCompat.getColor(EditFuel.this, R.color.blackk));
                textView.setTextSize(18);
                textView.setPadding(30, 0, 0, 0);
                return textView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setBackgroundColor(ContextCompat.getColor(EditFuel.this, R.color.light_gray));
                textView.setTextColor(ContextCompat.getColor(EditFuel.this, R.color.blackk));
                textView.setTextSize(18);
                textView.setPadding(30, 20, 20, 20);
                return textView;
            }
        };

        fuelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fuelTypeSpinner.setAdapter(fuelAdapter);

        activeAdapter = new ArrayAdapter<CharSequence>(
                this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.active_options)) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(ContextCompat.getColor(EditFuel.this, R.color.blackk));
                textView.setTextSize(18);
                textView.setPadding(30, 0, 0, 0);
                return textView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setBackgroundColor(ContextCompat.getColor(EditFuel.this, R.color.light_gray));
                textView.setTextColor(ContextCompat.getColor(EditFuel.this, R.color.blackk));
                textView.setTextSize(18);
                textView.setPadding(30, 20, 20, 20);
                return textView;
            }
        };
        activeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activeSpinner.setAdapter(activeAdapter);

        fuelTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Fuel selectedFuel = (Fuel) parent.getItemAtPosition(position);
                if (selectedFuel != null) {
                    priceInput.setText(String.valueOf(selectedFuel.getPrice()));
                    descriptionInput.setText(selectedFuel.getDescription());

                    activeSpinner.setSelection(selectedFuel.isActive() ? 0 : 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                priceInput.setText("");
                descriptionInput.setText("");
                activeSpinner.setSelection(0);
            }
        });
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
                new SweetAlertDialog(EditFuel.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Ошибка")
                        .setContentText("Ошибка загрузки данных")
                        .show();
            }
        });
    }

    private void setupSaveButton() {
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> {
            Fuel selectedFuel = (Fuel) fuelTypeSpinner.getSelectedItem();
            if (selectedFuel != null) {
                try {
                    String priceStr = priceInput.getText().toString().trim();
                    if (priceStr.isEmpty()) {
                        new SweetAlertDialog(EditFuel.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Внимание")
                                .setContentText("Введите цену")
                                .show();
                        return;
                    }

                    double newPrice = Double.parseDouble(priceStr);
                    String newDescription = descriptionInput.getText().toString().trim();
                    boolean isActive = activeSpinner.getSelectedItemPosition() == 0;

                    selectedFuel.setPrice(newPrice);
                    selectedFuel.setDescription(newDescription);
                    selectedFuel.setActive(isActive);

                    fuelService.updateFuel(selectedFuel);

                    new SweetAlertDialog(EditFuel.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Успешно")
                            .setContentText("Данные обновлены")
                            .setConfirmClickListener(sDialog -> {
                                sDialog.dismissWithAnimation();
                                Intent intent = new Intent(EditFuel.this, AdminHome.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                finish();
                            })
                            .show();

                } catch (NumberFormatException e) {
                    new SweetAlertDialog(EditFuel.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Ошибка")
                            .setContentText("Введите корректную цену (например: 45.90)")
                            .show();
                }
            } else {
                new SweetAlertDialog(EditFuel.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Внимание")
                        .setContentText("Выберите тип топлива")
                        .show();
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