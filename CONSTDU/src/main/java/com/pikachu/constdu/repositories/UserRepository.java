package com.pikachu.constdu.repositories;

import com.pikachu.constdu.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findById(int ID);
    User findByPhoneNumber(String phoneNumber);
    User findByEmail(String email);
}
