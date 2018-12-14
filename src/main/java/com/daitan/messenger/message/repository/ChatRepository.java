package com.daitan.messenger.message.repository;

import com.daitan.messenger.message.model.Chat;
import com.daitan.messenger.message.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository {

    void createChat(String chatName, String... userId);

    void createChat(Chat chatInfo);

    void insertUserToChat(String chatId, String... userId);

    void updateChat(String chatId, String chatName);

    void removeUserByChatIdAndUserId(String chatId, String userId);

    void removeUser(String uniqueId);

    void deleteChat(String chatId);

    List<Chat> findChat(String chatId);

    Page<Chat> findAllChats(String emitter, String receptor, String content, int page, int size);

    List<Chat> findChatByUserId(String userId);
}
