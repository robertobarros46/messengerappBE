package com.daitan.messenger.users.repository;

import com.daitan.messenger.users.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    User findIdByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);


}
