package com.daitan.messenger.message.service;

import com.daitan.messenger.message.model.Chat;
import com.daitan.messenger.message.model.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.daitan.messenger.constants.ConstantsUtils.SUBSCRIBE_TO_RECEIVE_MESSAGE_URN;

@Service
public class ChatBrokerServiceImpl implements ChatBrokerService {

    private SimpMessagingTemplate simpMessagingTemplate;

    private ChatService chatService;

    private MessageService messageService;

    public ChatBrokerServiceImpl(SimpMessagingTemplate simpMessagingTemplate, ChatService chatService, MessageService messageService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatService = chatService;
        this.messageService = messageService;
    }

    @Override
    public void sendMessage(Message message) {

        String chatId = message.getChatId();
        List<Chat> chats = chatService.findChat(chatId);

        List<Chat> toSendChat = chats.stream()
                .filter(chat -> !chat.getUserId().equals(message.getFromUserId()))
                .collect(Collectors.toList());

        for (Chat c : toSendChat) {
            messageService.createMessage(message);
            simpMessagingTemplate.convertAndSend(SUBSCRIBE_TO_RECEIVE_MESSAGE_URN + c.getUserId(), message);
        }

    }
}
