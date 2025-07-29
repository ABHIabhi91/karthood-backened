package com.karthood.repository.impl;



import com.karthood.dto.User;

import com.karthood.repository.UserRepository;

import com.karthood.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;



import java.util.Optional;

import java.util.concurrent.ExecutionException;



@Repository

public class UserRepositoryImpl implements UserRepository {



    @Autowired

    private UserService userService;



    @Override

    public Optional<User> findByEmail(String email) {

        try {

            return userService.findByEmail(email);

        } catch (ExecutionException | InterruptedException e) {

            throw new RuntimeException("Error finding user by email", e);

        }
    }
        @Override
        public User save(User user) {

            try {

                return userService.save(user);

            } catch (ExecutionException | InterruptedException e) {

                throw new RuntimeException("Error saving user", e);

            }

        }

    }


