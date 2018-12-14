package com.daitan.messenger.message.repository;

import com.daitan.messenger.message.model.Message;
import com.daitan.messenger.users.model.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository {

    void createMessage(Message message);

    List<Message> findMessageByChatId(String chatId);

    Page<Message> findAllMessages(String emitter, String receptor, int page, int size);

    void deleteMessagesFromChat(String chatId);

}
