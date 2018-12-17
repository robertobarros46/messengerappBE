package com.daitan.messenger.message.service;

import com.daitan.messenger.message.model.Message;
import com.daitan.messenger.users.model.PagedResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {

    void createMessage(Message message);

    List<Message> findMessageByChatId(String chatId);

    List<Message> findMessageByContent(String content);

    PagedResponse<Message> findAllMessages(String emitter, String receptor,int page, int size);

    void deleteMessagesFromChat(String chatId);
}
