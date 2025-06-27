package com.example.irbis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminHome extends AppCompatActivity {

    private TextView ai95PriceTextView, ai92PriceTextView, ai92xtPriceTextView, dieselPriceTextView;
    private FuelService fuelService;
    private List<Fuel> fuels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fuelService = new FuelService();
        ai95PriceTextView = findViewById(R.id.ai95PriceTextView);
        ai92PriceTextView = findViewById(R.id.ai92PriceTextView);
        ai92xtPriceTextView = findViewById(R.id.ai92xtPriceTextView);
        dieselPriceTextView = findViewById(R.id.dieselPriceTextView);


        UserData userData = UserData.getInstance();
        TextView userNameTextView = findViewById(R.id.userNameTextView);

        if (userData != null) {
            userNameTextView.setText(userData.getFirstName());
        }

        loadFuelData();
        setupNavigation();
    }

    private void loadFuelData() {
        fuelService.getFuelsReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fuels.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Fuel fuel = snapshot.getValue(Fuel.class);
                    if (fuel != null) {
                        fuels.add(fuel);
                        updateFuelUI(fuel);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибки
            }
        });
    }

    private void updateFuelUI(Fuel fuel) {
        switch (fuel.getName()) {
            case "АИ-95 XTRim":
                ai95PriceTextView.setText(String.format("%.2f₽", fuel.getPrice()));
                break;
            case "АИ-92":
                ai92PriceTextView.setText(String.format("%.2f₽", fuel.getPrice()));
                break;
            case "АИ-92 XTRim":
                ai92xtPriceTextView.setText(String.format("%.2f₽", fuel.getPrice()));
                break;
            case "Дизель":
                dieselPriceTextView.setText(String.format("%.2f₽", fuel.getPrice()));
                break;
        }
    }

    private void setupNavigation() {
        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, EditFuel.class);
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
                Intent intent = new Intent(AdminHome.this, AdminHome.class);
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
                Intent intent = new Intent(AdminHome.this, AdminHistory.class);
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
                Intent intent = new Intent(AdminHome.this, AdminProfile.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }
}