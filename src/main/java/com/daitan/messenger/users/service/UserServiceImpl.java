package com.daitan.messenger.users.service;

import com.daitan.messenger.users.model.PagedResponse;
import com.daitan.messenger.users.model.User;
import com.daitan.messenger.users.model.UserProfile;
import com.daitan.messenger.users.repository.UserRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private MongoTemplate mongoTemplate;
    private PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> findIdByEmail(String email) {
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
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public PagedResponse<UserProfile> findByNameAndOrLastName(String name, String lastName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "email");
        PagedResponse pagedResponse;
        Page<User> usersPage;
//        if(Strings.isBlank(name) && Strings.isBlank(lastName)){
//            usersPage = userRepository.findAll(pageable);
//        }else{
//            Criteria criteria = new Criteria();
//            criteria.orOperator(Criteria.where("name").is(name), Criteria.where("lastName").is(lastName));
//            Query query = new Query(criteria).with(pageable);
//            List<User> users = mongoTemplate.find(query, User.class);
//            usersPage = PageableExecutionUtils.getPage(
//                    users,
//                    pageable,
//                    () -> mongoTemplate.count(query, User.class));
//        }

        if (Strings.isNotBlank(name) && Strings.isNotBlank(lastName)) {
            usersPage = userRepository.findByNameAndLastName(name, lastName, pageable);
        } else if (Strings.isNotBlank(name)) {
            usersPage = userRepository.findByName(name, pageable);
        } else if (Strings.isNotBlank(lastName)) {
            usersPage = userRepository.findByLastName(lastName, pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }
        pagedResponse = getPagedResponse(usersPage);

        return pagedResponse;
    }

    private PagedResponse getPagedResponse(Page<User> usersPage) {
        PagedResponse pagedResponse;
        if (usersPage.getNumberOfElements() == 0) {
            pagedResponse = new PagedResponse<>(Collections.emptyList(), usersPage.getNumber(),
                    usersPage.getSize(), usersPage.getTotalElements(), usersPage.getTotalPages(), usersPage.isLast());

        }else {
            List<UserProfile> userProfiles = usersPage.stream()
                    .map(user -> new UserProfile(user.getId(), user.getEmail(), user.getName(), user.getRole()))
                    .collect(Collectors.toList());
            pagedResponse = new PagedResponse<>(userProfiles, usersPage.getNumber(),
                    usersPage.getSize(), usersPage.getTotalElements(), usersPage.getTotalPages(), usersPage.isLast());
        }
        return pagedResponse;
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }


}
