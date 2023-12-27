package com.pikachu.constdu.repositories;

import com.pikachu.constdu.models.ApplicationDate;
import com.pikachu.constdu.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Yuanfu Tian
 * Date: 2023-11-14
 */
public interface ApplicationDateRepository extends JpaRepository<ApplicationDate, Long> {
    ApplicationDate findApplicationDateByUser(User user);

    Boolean deleteApplicationDateByUser(User user);

}
