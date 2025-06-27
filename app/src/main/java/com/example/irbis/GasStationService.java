package com.example.irbis;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GasStationService {
    private final DatabaseReference gasStationsRef;

    public GasStationService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        gasStationsRef = database.getReference("gasStations");
    }

    public void addGasStation(GasStation gasStation) {
        String id = gasStationsRef.push().getKey();
        if (id != null) {
            gasStation.setId(id);
            gasStationsRef.child(id).setValue(gasStation);
        }
    }

    public DatabaseReference getGasStationsReference() {
        return gasStationsRef;
    }
}