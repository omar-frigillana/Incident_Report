package com.example.incident_report;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Database {
    private DatabaseReference databaseReference;

    public Database() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(Person.class.getSimpleName());
    }

    public Task<Void> add(Person person) {
        return databaseReference.push().setValue(person);
    }
}
