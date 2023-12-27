package com.pikachu.constdu.services;

import com.pikachu.constdu.models.ApplicationDate;
import com.pikachu.constdu.models.User;
import com.pikachu.constdu.repositories.ApplicationDateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Created by Yuanfu Tian
 * Date: 2023-11-14
 */
@Service
public class ApplicationDateService {
    private final ApplicationDateRepository applicationDateRepository;

    public ApplicationDateService(ApplicationDateRepository applicationDateRepository) {
        this.applicationDateRepository = applicationDateRepository;
    }

    public ApplicationDate getApplicationDateByUser(User user) {
        return applicationDateRepository.findApplicationDateByUser(user);
    }

    public Boolean saveApplicationDate(User user) {
        ApplicationDate applicationDate = ApplicationDate.builder().applicationDate(Instant.now()).user(user).build();
        try {
            applicationDateRepository.save(applicationDate);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Boolean deleteApplicationDateByUser(User user) {
        return applicationDateRepository.deleteApplicationDateByUser(user);
    }

    public ApplicationDate updateApplicationDateByUser(User user) {
        ApplicationDate applicationDate = applicationDateRepository.findApplicationDateByUser(user);
        applicationDate.setApplicationDate(Instant.now());
        return applicationDateRepository.save(applicationDate);
    }

    public boolean registerApplicationDateAfterGettingPackage(User user) {
        ApplicationDate applicationDate = applicationDateRepository.findApplicationDateByUser(user);
        if (applicationDate == null) {
            applicationDate = ApplicationDate.builder().user(user).applicationDate(Instant.now()).build();
        }
        else {
            applicationDate.setApplicationDate(Instant.now());
        }

        try {
            applicationDateRepository.save(applicationDate);
        } catch (Exception e) {
            return false;
        }
        return true;
    }





}
