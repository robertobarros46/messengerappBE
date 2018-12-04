package com.daitan.messenger.users.service;

import com.daitan.messenger.users.model.PagedResponse;
import com.daitan.messenger.users.model.User;
import com.daitan.messenger.users.model.UserProfile;
import com.daitan.messenger.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findIdByEmail(String email) {
        return userRepository.findIdByEmail(email);
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public void deleteById(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void insert(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.insert(user);
    }

    @Override
    public PagedResponse<UserProfile> findAll(int page, int size) {

        // Retrieve Users
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "email");
        Page<User> users = userRepository.findAll(pageable);

        //Return empty PagedResponse in case there is no user
        if (users.getNumberOfElements() == 0) {
            return new PagedResponse<>(Collections.emptyList(), users.getNumber(),
                    users.getSize(), users.getTotalElements(), users.getTotalPages(), users.isLast());

        }

        //mapping Users to UserProfiles
        List<UserProfile> userProfiles = users.stream()
                .map(user -> new UserProfile(user.getId(), user.getEmail(), user.getNome(), user.getRole()))
                .collect(Collectors.toList());


        return new PagedResponse<>(userProfiles, users.getNumber(),
                users.getSize(), users.getTotalElements(), users.getTotalPages(), users.isLast());
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<User> user =  userRepository.findByEmail(email);
        return user;
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }


}
