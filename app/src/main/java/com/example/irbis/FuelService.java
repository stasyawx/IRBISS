package com.example.irbis;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FuelService {
    private final DatabaseReference fuelsRef;

    public FuelService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        fuelsRef = database.getReference("fuels");
    }

    public void addFuel(Fuel fuel) {
        String id = fuelsRef.push().getKey();
        if (id != null) {
            fuel.setId(id);
            fuelsRef.child(id).setValue(fuel);
        }
    }

    public void updateFuel(Fuel fuel) {
        fuelsRef.child(fuel.getId()).setValue(fuel);
    }

    public DatabaseReference getFuelsReference() {
        return fuelsRef;
    }
}
