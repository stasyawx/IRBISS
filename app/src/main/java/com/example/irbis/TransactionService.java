package com.example.irbis;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TransactionService {
    private final DatabaseReference transactionsRef;

    public TransactionService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        transactionsRef = database.getReference("transactions");
    }

    public void addTransaction(Transaction transaction) {
        String id = transactionsRef.push().getKey();
        if (id != null) {
            transaction.setId(id);
            transactionsRef.child(id).setValue(transaction);
        }
    }

    public DatabaseReference getTransactionsReference() {
        return transactionsRef;
    }
}