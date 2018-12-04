package com.daitan.messenger.message.service;

import com.daitan.messenger.message.model.Message;
import com.daitan.messenger.message.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void createMessage(Message messageInfo) {
        String messageId = UUID.randomUUID().toString();
        messageInfo.setMessageId(messageId);
        messageRepository.createMessage(messageInfo);
    }

    @Override
    public List<Message> findMessageByChatId(String chatId) {
        return messageRepository.findMessageByChatId(chatId);
    }
}
