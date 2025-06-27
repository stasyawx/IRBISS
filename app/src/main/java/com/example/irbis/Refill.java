package com.example.irbis;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Refill extends AppCompatActivity {

    private FuelService fuelService;
    private GasStationService gasStationService;
    private PumpService pumpService;
    private TransactionService transactionService;
    private UserService userService;

    private AppCompatSpinner gasStationSpinner, fuelTypeSpinner, pumpSpinner;
    private EditText litersInput;
    private TextView totalPriceText;
    private Button confirmButton;

    private List<Fuel> fuels = new ArrayList<>();
    private List<GasStation> gasStations = new ArrayList<>();
    private List<Pump> allPumps = new ArrayList<>();
    private List<Pump> filteredPumps = new ArrayList<>();

    private ArrayAdapter<GasStation> gasStationAdapter;
    private ArrayAdapter<Fuel> fuelAdapter;
    private ArrayAdapter<Pump> pumpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_refill);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fuelService = new FuelService();
        gasStationService = new GasStationService();
        pumpService = new PumpService();
        transactionService = new TransactionService();
        userService = new UserService();

        gasStationSpinner = findViewById(R.id.gasStationSpinner);
        fuelTypeSpinner = findViewById(R.id.fuelTypeSpinner);
        pumpSpinner = findViewById(R.id.pumpSpinner);
        litersInput = findViewById(R.id.litersInput);
        totalPriceText = findViewById(R.id.totalPriceText);
        confirmButton = findViewById(R.id.confirmButton);

        setupAdapters();
        loadData();
        setupListeners();
        setupNavigation();
    }

    private void setupAdapters() {
        gasStationAdapter = new ArrayAdapter<GasStation>(this, android.R.layout.simple_spinner_item, gasStations) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(ContextCompat.getColor(Refill.this, R.color.blackk));
                textView.setTextSize(18);
                textView.setPadding(30, 0, 0, 0);
                return textView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setBackgroundColor(ContextCompat.getColor(Refill.this, R.color.light_gray));
                textView.setTextColor(ContextCompat.getColor(Refill.this, R.color.blackk));
                textView.setTextSize(18);
                textView.setPadding(30, 20, 20, 20);
                return textView;
            }
        };
        gasStationSpinner.setAdapter(gasStationAdapter);

        fuelAdapter = new ArrayAdapter<Fuel>(this, android.R.layout.simple_spinner_item, fuels) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(ContextCompat.getColor(Refill.this, R.color.blackk));
                textView.setTextSize(18);
                textView.setPadding(30, 0, 0, 0);
                return textView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setBackgroundColor(ContextCompat.getColor(Refill.this, R.color.light_gray));
                textView.setTextColor(ContextCompat.getColor(Refill.this, R.color.blackk));
                textView.setTextSize(18);
                textView.setPadding(30, 20, 20, 20);
                return textView;
            }
        };
        fuelTypeSpinner.setAdapter(fuelAdapter);

        pumpAdapter = new ArrayAdapter<Pump>(this, android.R.layout.simple_spinner_item, filteredPumps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(ContextCompat.getColor(Refill.this, R.color.blackk));
                textView.setTextSize(18);
                textView.setPadding(30, 0, 0, 0);
                return textView;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setBackgroundColor(ContextCompat.getColor(Refill.this, R.color.light_gray));
                textView.setTextColor(ContextCompat.getColor(Refill.this, R.color.blackk));
                textView.setTextSize(18);
                textView.setPadding(30, 20, 20, 20);
                return textView;
            }
        };
        pumpSpinner.setAdapter(pumpAdapter);
    }

    private void loadData() {
        gasStationService.getGasStationsReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gasStations.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GasStation gasStation = snapshot.getValue(GasStation.class);
                    if (gasStation != null && gasStation.isActive()) {
                        gasStations.add(gasStation);
                    }
                }
                gasStationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Refill.this, "Ошибка загрузки АЗС", Toast.LENGTH_SHORT).show();
            }
        });

        fuelService.getFuelsReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fuels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Fuel fuel = snapshot.getValue(Fuel.class);
                    if (fuel != null && fuel.isActive()) {
                        fuels.add(fuel);
                    }
                }
                fuelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Refill.this, "Ошибка загрузки топлива", Toast.LENGTH_SHORT).show();
            }
        });

        pumpService.getPumpsReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allPumps.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Pump pump = snapshot.getValue(Pump.class);
                    if (pump != null && pump.isActive()) {
                        allPumps.add(pump);
                    }
                }
                if (gasStationSpinner.getSelectedItem() != null) {
                    filterPumpsByGasStation(((GasStation) gasStationSpinner.getSelectedItem()).getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Refill.this, "Ошибка загрузки колонок", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterPumpsByGasStation(String gasStationId) {
        filteredPumps.clear();
        for (Pump pump : allPumps) {
            if (pump.getGasStationId().equals(gasStationId)) {
                filteredPumps.add(pump);
            }
        }
        pumpAdapter.notifyDataSetChanged();
    }

    private void setupListeners() {
        litersInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                calculateTotalPrice();
            }
        });

        fuelTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateTotalPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        gasStationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GasStation selectedGasStation = (GasStation) parent.getItemAtPosition(position);
                if (selectedGasStation != null) {
                    filterPumpsByGasStation(selectedGasStation.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        confirmButton.setOnClickListener(v -> {
            if (validateInput()) {
                createTransaction();

                Toast.makeText(Refill.this, "Заправка начата...", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(Refill.this, RefuelingCompleted.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }, 5000);
            }
        });
    }

    private void calculateTotalPrice() {
        try {
            Fuel selectedFuel = (Fuel) fuelTypeSpinner.getSelectedItem();
            String litersStr = litersInput.getText().toString().trim();

            if (selectedFuel != null && !litersStr.isEmpty()) {
                double liters = Double.parseDouble(litersStr);
                double totalPrice = liters * selectedFuel.getPrice();
                totalPriceText.setText(String.format("Итого: %.2f ₽", totalPrice));
            } else {
                totalPriceText.setText("Итого: 0 ₽");
            }
        } catch (NumberFormatException e) {
            totalPriceText.setText("Итого: 0 ₽");
        }
    }

    private boolean validateInput() {
        if (gasStationSpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Выберите АЗС", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (fuelTypeSpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Выберите топливо", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (pumpSpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Выберите колонку", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (litersInput.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Введите количество литров", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double liters = Double.parseDouble(litersInput.getText().toString().trim());
            if (liters <= 0) {
                Toast.makeText(this, "Количество литров должно быть больше 0", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Введите корректное количество литров", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void createTransaction() {
        GasStation selectedGasStation = (GasStation) gasStationSpinner.getSelectedItem();
        Fuel selectedFuel = (Fuel) fuelTypeSpinner.getSelectedItem();
        Pump selectedPump = (Pump) pumpSpinner.getSelectedItem();
        double liters = Double.parseDouble(litersInput.getText().toString().trim());
        double totalPrice = liters * selectedFuel.getPrice();
        int bonusPoints = (int) Math.round(totalPrice * 0.02);

        Transaction transaction = new Transaction();
        transaction.setUserId(UserData.getInstance().getId());
        transaction.setGasStationId(selectedGasStation.getId());
        transaction.setFuelId(selectedFuel.getId());
        transaction.setPumpId(selectedPump.getId());
        transaction.setLiters(liters);
        transaction.setTotalPrice(totalPrice);
        transaction.setBonusPoints(bonusPoints);
        transaction.setTransactionDate(new Date());

        transactionService.addTransaction(transaction);

        userService.updateUserBonusPoints(UserData.getInstance().getId(), bonusPoints);

        litersInput.setText("");
        totalPriceText.setText("Итого: 0 ₽");
    }

    private void setupNavigation() {
        ImageView homeImageView = findViewById(R.id.homeImageView);
        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Refill.this, Home.class);
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
                Intent intent = new Intent(Refill.this, History.class);
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
                Intent intent = new Intent(Refill.this, Profile.class);
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
                Intent intent = new Intent(Refill.this, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    private void navigateTo(Class<?> cls) {
        Intent intent = new Intent(Refill.this, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}