package com.pikachu.constdu.repositories;

import com.pikachu.constdu.models.ChatEntry;
import com.pikachu.constdu.models.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Yuanfu Tian
 * Date: 2023-10-03
 */

public interface ChatEntryRepository extends CrudRepository<ChatEntry, Long> {

    List<ChatEntry> findAllByUser(User user);

    List<ChatEntry> findAllByUserAndRoleIn(User user, List<String> roles);

}
