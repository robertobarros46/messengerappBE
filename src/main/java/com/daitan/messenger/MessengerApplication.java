package com.daitan.messenger;

import com.daitan.messenger.users.model.User;
import com.daitan.messenger.users.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = "com.daitan.messenger", exclude = DataSourceAutoConfiguration.class)
@EntityScan(basePackageClasses = User.class)
@EnableMongoRepositories(basePackageClasses = UserRepository.class)
public class MessengerApplication {
    public static void main(String[] args) {
        SpringApplication.run(MessengerApplication.class, args);
    }
}
