package com.daitan.messenger.message.repository;

import com.daitan.messenger.message.model.Message;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;

@Repository
public interface MessageRepository {

    void createMessage(Message message);

    List<Message> findMessageByChatId(String chatId);
}
