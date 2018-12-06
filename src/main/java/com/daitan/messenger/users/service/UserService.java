package com.daitan.messenger.users.service;

import com.daitan.messenger.users.model.PagedResponse;
import com.daitan.messenger.users.model.User;
import com.daitan.messenger.users.model.UserProfile;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    User findIdByEmail(String email);

    Optional<User> findById(String id);

    void deleteById(String id);

    User save(User user);

    void insert(User user);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    PagedResponse<UserProfile> findByNameAndOrLastName(String name, String lastName, int page, int size);

    void deleteAll();

}
