package com.example.irbis;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class UserService {
    private final DatabaseReference usersRef;
    private final FirebaseDatabase database;

    public UserService() {
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("User");
    }

    public void updateUserBonusPoints(String userId, int bonusPointsToAdd) {
        DatabaseReference userBonusRef = usersRef.child(userId).child("bonusPoints");

        userBonusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Integer currentPoints = dataSnapshot.getValue(Integer.class);
                if (currentPoints == null) {
                    currentPoints = 0;
                }
                userBonusRef.setValue(currentPoints + bonusPointsToAdd);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Можно добавить обработку ошибки, например, логирование
            }
        });
    }

    public void getUserBonusPoints(String userId, BonusPointsListener listener) {
        usersRef.child(userId).child("bonusPoints")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Integer points = dataSnapshot.getValue(Integer.class);
                        listener.onPointsReceived(points != null ? points : 0);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onError(databaseError.getMessage());
                    }
                });
    }

    public interface BonusPointsListener {
        void onPointsReceived(int points);
        void onError(String errorMessage);
    }

    public DatabaseReference getUserReference(String userId) {
        return usersRef.child(userId);
    }

    public DatabaseReference getUserBonusPointsReference(String userId) {
        return usersRef.child(userId).child("bonusPoints");
    }
}