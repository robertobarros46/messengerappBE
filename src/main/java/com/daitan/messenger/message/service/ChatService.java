package com.daitan.messenger.message.service;

import com.daitan.messenger.message.model.Chat;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatService {

    void createChatOneToOne(List<Chat> chats);

    List<Chat> findChat(String chatId);

    List<Chat> findChatByUserId(String userId);

}

