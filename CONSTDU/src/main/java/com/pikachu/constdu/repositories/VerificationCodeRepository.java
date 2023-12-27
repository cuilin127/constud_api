package com.pikachu.constdu.repositories;


import com.pikachu.constdu.models.VerificationCode;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VerificationCodeRepository extends CrudRepository<VerificationCode, Integer> {
    List<VerificationCode> findByEmail(String email, Sort sort);
}
