package com.daitan.messenger.message.service;

import com.daitan.messenger.message.model.Message;
import com.daitan.messenger.message.repository.MessageRepository;
import com.daitan.messenger.users.model.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void createMessage(Message message) {
        String messageId = UUID.randomUUID().toString();
        message.setMessageId(messageId);
        messageRepository.createMessage(message);
    }

    @Override
    public List<Message> findMessageByChatId(String chatId) {
        return messageRepository.findMessageByChatId(chatId);
    }

    @Override
    public PagedResponse<Message> findAllMessages(String emitter, String receptor,int page, int size) {
        return getPagedResponse(messageRepository.findAllMessages(emitter, receptor, page, size));
    }

    private PagedResponse<Message> getPagedResponse(Page<Message> messagePage) {
        PagedResponse<Message> pagedResponse;
        if (messagePage.getNumberOfElements() == 0) {
            pagedResponse = new PagedResponse<>(Collections.emptyList(), messagePage.getNumber(),
                    messagePage.getSize(), messagePage.getTotalElements(), messagePage.getTotalPages(), messagePage.isLast());

        }else {
            pagedResponse = new PagedResponse<>(messagePage.stream().collect(Collectors.toList()), messagePage.getNumber(),
                    messagePage.getSize(), messagePage.getTotalElements(), messagePage.getTotalPages(), messagePage.isLast());
        }
        return pagedResponse;
    }

    @Override
    public void deleteMessagesFromChat(String chatId) {
        messageRepository.deleteMessagesFromChat(chatId);
    }

    @Override
    public List<Message> findMessageByContent(String content){
        return messageRepository.findMessageByContent(content);
    }

}
