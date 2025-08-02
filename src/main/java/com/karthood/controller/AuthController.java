package com.karthood.controller;



import com.karthood.dto.AuthResponse;

import com.karthood.dto.LoginRequest;

import com.karthood.dto.SignupRequest;

import com.karthood.dto.User;

import com.karthood.repository.UserRepository;

import com.karthood.utility.JwtUtility;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;



import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")



@RestController

@RequestMapping("/api")

public class AuthController {



    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtility jwtUtil;



    public AuthController(UserRepository repo, PasswordEncoder encoder, JwtUtility jwtUtil) {

        this.userRepository = repo;

        this.passwordEncoder = encoder;

        this.jwtUtil = jwtUtil;

    }



    @PostMapping("/signup")

    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {

            return ResponseEntity.badRequest().body("Email already in use");

        }



        User user = new User();

        user.setName(request.name());

        user.setEmail(request.email());

        user.setPhone(request.phone());

        user.setPassword(passwordEncoder.encode(request.password()));

        user.setTower(request.tower());

        user.setFlatNumber(request.flatNumber());

        user.setRole(request.role());

        userRepository.save(user);



        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return ResponseEntity.ok(new AuthResponse(token, user));

    }



//    @PostMapping("/login")
//
//    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//
//        Optional<User> userOpt = userRepository.findByEmail(request.email());
//
//        if (userOpt.isEmpty() || !passwordEncoder.matches(request.password(), userOpt.get().getPassword())) {
//
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
//
//        } else if (userOpt.get().getRole().equalsIgnoreCase("BUYER")) {
//
//            String token = jwtUtil.generateToken(request.email(), userOpt.get().getRole());
//
//            return ResponseEntity.ok(new AuthResponse(token, userOpt.get()));
//
//        } else
//
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Only Buyer role should login");
//
//    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.email());

        if (userOpt.isEmpty() || !passwordEncoder.matches(request.password(), userOpt.get().getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        User user = userOpt.get();
        String actualRole = user.getRole();            // e.g., "BUYER", "PROPERTY_SELLER"
        String selectedUserType = request.userType(); // "Resident" or "Service Provider"

        if (selectedUserType.equalsIgnoreCase("Resident")) {
            // Only allow if actual role is 'BUYER'
            if (!actualRole.equalsIgnoreCase("BUYER")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("This account is registered as a service provider. Please select 'Service Provider' at login.");
            }
        } else if (selectedUserType.equalsIgnoreCase("Service Provider")) {
            // Only allow if actual role is one of the service provider roles
            if (!isServiceProvider(actualRole)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("This account is registered as a resident. Please select 'Resident' at login.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid user type selection.");
        }

        // Generate JWT token including actual role info
        String token = jwtUtil.generateToken(user.getEmail(), actualRole);

        return ResponseEntity.ok(new AuthResponse(token, user));
    }

    // Helper method to identify service provider roles
    private boolean isServiceProvider(String role) {
        return role.equalsIgnoreCase("PROPERTY_SELLER") ||
            role.equalsIgnoreCase("RESTAURANT_OWNER") ||
            role.equalsIgnoreCase("BAKERY_OWNER") ||
            role.equalsIgnoreCase("SALON_OWNER");
    }


}



