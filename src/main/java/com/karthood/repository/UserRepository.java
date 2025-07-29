package com.karthood.repository;



import com.karthood.dto.User;



import java.util.Optional;



public interface UserRepository  {

    Optional<User> findByEmail(String email);

    User save(User user);

}