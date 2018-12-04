package com.daitan.messenger.config;

import com.daitan.messenger.message.repository.ChatRepository;
import com.daitan.messenger.message.repository.ChatRepositoryImpl;
import com.daitan.messenger.message.repository.MessageRepository;
import com.daitan.messenger.message.repository.MessageRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class WebConfiguration implements WebMvcConfigurer {

    @Bean
    ChatRepository chatRepository() {
        return new ChatRepositoryImpl();
    }

    @Bean
    MessageRepository messageRepository() {
        return new MessageRepositoryImpl();
    }



    private final long MAX_AGE_SECS = 3600;

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/api/v1/**")
//                .allowedOrigins("*")
//                .allowedHeaders("*")
//                .allowCredentials(false)
//                .allowedMethods("*")
//                .maxAge(MAX_AGE_SECS);
//    }
}
