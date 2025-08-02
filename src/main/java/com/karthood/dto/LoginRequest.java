package com.karthood.dto;

public record LoginRequest(String email, String password,String role,String userType) {
}
