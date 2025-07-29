package com.karthood.dto;



import lombok.Data;

import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;



@Data

@NoArgsConstructor

@AllArgsConstructor

public class User {

    public static final String COLLECTION_PATH = "users";



    private String id; // Changed from Long to String (Firestore uses String IDs)

    private String name;

    private String email;

    private String password;

    private String phone;

    private String tower;

    private String flatNumber;

    private String role;

}