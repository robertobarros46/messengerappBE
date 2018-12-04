package com.daitan.messenger.message.service;

import com.daitan.messenger.message.model.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface MessageService {

    void createMessage(Message message);

    List<Message> findMessageByChatId(String chatId);
}
