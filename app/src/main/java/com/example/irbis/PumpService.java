package com.example.irbis;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PumpService {
    private final DatabaseReference pumpsRef;

    public PumpService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        pumpsRef = database.getReference("pumps");
    }

    public void addPump(Pump pump) {
        String id = pumpsRef.push().getKey();
        if (id != null) {
            pump.setId(id);
            pumpsRef.child(id).setValue(pump);
        }
    }

    public DatabaseReference getPumpsReference() {
        return pumpsRef;
    }
}