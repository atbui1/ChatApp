package com.edu.chatapp.service;

import androidx.annotation.NonNull;

import com.edu.chatapp.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FireBaseUtils {

    public FirebaseUser getListUserFB (String entity) {
        System.out.println("999999999999999999999999999999999999999999999999999");
        final List<User> userList = new ArrayList<>();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        if (firebaseUser == null) {
            return null;
        }

        DatabaseReference databaseReference = firebaseDatabase.getReference(entity);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot o:snapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                }
                System.out.println("ZZZZZZZZZZZZZZZZZZZ: " + userList.size());
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        System.out.println("QQQQQQQQQQQQQQQQQQQQQQQ qaz: " + userList);
        return null;
    }
}
