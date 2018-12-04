package com.daitan.messenger.message.controller;

import com.daitan.messenger.message.model.Message;
import com.daitan.messenger.message.service.ChatBrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class ChatBrokerController {

    private Logger LOGGER = LoggerFactory.getLogger(ChatBrokerController.class);

    private ChatBrokerService chatBrokerService;

    public ChatBrokerController(ChatBrokerService chatBrokerService) {
        this.chatBrokerService = chatBrokerService;
    }

    @MessageMapping("/chat")
    public void sendMessage(@Payload Message message) {
        LOGGER.info("Request made to endpoint sendMessage {}", message);
        chatBrokerService.sendMessage(message);
    }
}
