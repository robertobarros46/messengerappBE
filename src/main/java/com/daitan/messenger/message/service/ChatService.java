package com.daitan.messenger.message.service;

import com.daitan.messenger.message.model.Chat;
import com.daitan.messenger.message.model.Message;
import com.daitan.messenger.users.model.PagedResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatService {

    void createChatOneToOne(List<Chat> chats);

    List<Chat> findChat(String chatId);

    List<Chat> findChatByUserId(String userId);

    PagedResponse<Chat> findAllChats(String emitter, String receptor, String content, int page, int size);

    void deleteChat(String chatId);

}

