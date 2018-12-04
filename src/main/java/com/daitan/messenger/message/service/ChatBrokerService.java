package com.daitan.messenger.message.service;

import com.daitan.messenger.message.model.Message;
import org.springframework.stereotype.Service;

@Service
public interface ChatBrokerService {
    void sendMessage(Message message);

}
