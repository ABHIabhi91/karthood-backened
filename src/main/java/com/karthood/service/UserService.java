package com.karthood.service;



import com.google.api.core.ApiFuture;

import com.google.cloud.firestore.DocumentReference;

import com.google.cloud.firestore.DocumentSnapshot;

import com.google.cloud.firestore.Firestore;

import com.google.cloud.firestore.QuerySnapshot;

import com.karthood.dto.User;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;



import java.util.Optional;

import java.util.concurrent.ExecutionException;



@Service

public class UserService {



    @Autowired

    private Firestore firestore;



    @Autowired

    private PasswordEncoder passwordEncoder;



    // Method to find user by email (used in both signup and login)

    public Optional<User> findByEmail(String email) throws ExecutionException, InterruptedException {

        ApiFuture<QuerySnapshot> query = firestore.collection(User.COLLECTION_PATH)

            .whereEqualTo("email", email)

            .get();

        QuerySnapshot querySnapshot = query.get();



        if (!querySnapshot.getDocuments().isEmpty()) {

            DocumentSnapshot document = querySnapshot.getDocuments().get(0);

            User user = document.toObject(User.class);

            user.setId(document.getId());

            return Optional.of(user);

        }

        return Optional.empty();

    }



    // Method to save user (used in signup)

    public User save(User user) throws ExecutionException, InterruptedException {

        // Hash password before storing

        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {

            user.setPassword(passwordEncoder.encode(user.getPassword()));

        }



        user.setId(null); // Let Firestore generate the ID

        ApiFuture<DocumentReference> future = firestore.collection(User.COLLECTION_PATH).add(user);

        DocumentReference documentReference = future.get();



        user.setId(documentReference.getId());

        return user;

    }

}

