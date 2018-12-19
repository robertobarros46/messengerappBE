package com.daitan.messenger.message.repository;

import com.daitan.messenger.message.model.Chat;
import com.daitan.messenger.message.model.ChatResponse;
import com.daitan.messenger.message.model.Message;
import com.daitan.messenger.users.model.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository {

    void createChat(String chatName, String chatType, String... userId);

    void createChat(Chat chatInfo);

    void updateChat(List<Chat> chats, String chatId);

    void insertUserToChat(String chatId, String... userId);

    void updateChat(String chatId, String chatName);

    void removeUserByChatIdAndUserId(String chatId, String userId);

    void removeUser(String uniqueId);

    void deleteChat(String chatId);

    List<Chat> findChat(String chatId);

    Page<ChatResponse> findAllChats(String emitter, String receptor, String content, int page, int size);

    List<Chat> findChatByUserId(String userId);

    List<UserProfile> findUsersByChatId(String chatId);
}
