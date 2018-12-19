package com.daitan.messenger.message.service;

import com.daitan.messenger.message.model.Chat;
import com.daitan.messenger.message.model.ChatResponse;
import com.daitan.messenger.message.model.Message;
import com.daitan.messenger.users.model.PagedResponse;
import com.daitan.messenger.users.model.UserProfile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatService {

    void createChatOneToOne(List<Chat> chats);

    void updateChat(List<Chat> chats, String chatId);

    List<Chat> findChat(String chatId);

    List<Chat> findChatByUserId(String userId);

    PagedResponse<ChatResponse> findAllChats(String emitter, String receptor, String content, int page, int size);

    void deleteChat(String chatId);

    List<UserProfile> findUsersByChat(String chatId);
}

