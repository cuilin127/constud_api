package com.pikachu.constdu.repositories;

import com.pikachu.constdu.models.Role;
import com.pikachu.constdu.models.User;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Integer> {
    Role findById(int id);
}
