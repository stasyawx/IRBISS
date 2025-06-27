package com.example.irbis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Refill extends AppCompatActivity {

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

        setupSpinner(R.id.gasStationSpinner, new String[]{"АЗС №1", "АЗС №2", "АЗС №3"});
        setupSpinner(R.id.fuelTypeSpinner, new String[]{"АИ-95 XTRim", "АИ-92", "АИ-92 XTRim", "Дизель"});
        setupSpinner(R.id.pumpSpinner, new String[]{"Колонка 1", "Колонка 2", "Колонка 3"});

        setupNavigation();
    }

    private void setupSpinner(int spinnerId, String[] items) {

        AppCompatSpinner spinner = findViewById(spinnerId);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                items
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(ContextCompat.getColor(Refill.this, R.color.blackk));
                textView.setTextSize(18); // Размер шрифта
                textView.setPadding(30, 0, 0, 0);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                view.setBackgroundColor(ContextCompat.getColor(Refill.this, R.color.light_gray));
                textView.setTextColor(ContextCompat.getColor(Refill.this, R.color.blackk));
                textView.setTextSize(18);
                textView.setPadding(30, 20, 20, 20);
                return view;
            }
        };

        spinner.setAdapter(adapter);
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
}