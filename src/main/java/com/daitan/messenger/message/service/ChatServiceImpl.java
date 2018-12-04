package com.daitan.messenger.message.service;

import com.daitan.messenger.message.model.Chat;
import com.daitan.messenger.message.repository.ChatRepository;
import com.daitan.messenger.users.model.User;
import com.daitan.messenger.users.service.UserService;
import com.google.common.collect.Lists;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatServiceImpl implements ChatService {

    ChatRepository chatRepository;

    UserService userService;

    public ChatServiceImpl(ChatRepository chatRepository, UserService userService) {
        this.chatRepository = chatRepository;
        this.userService = userService;
    }

    @Override
    public void createChatOneToOne(List<Chat> chats) {
        for(Chat chat: chats) {
            Optional<User> optionalUser = userService.findById(chat.getUserId());
            if (!optionalUser.isPresent()) {
                throw new IllegalArgumentException("User does not exists...");
            }
        }
        String chatId = UUID.randomUUID().toString();
        for(Chat chat: chats) {
            chat.setChatId(chatId);
            chatRepository.createChat(chat);
        }
    }

    @Override
    public List<Chat> findChat(String chatId) {
        return chatRepository.findChat(chatId);
    }

    @Override
    public List<Chat> findChatByUserId(String userId) {
        return chatRepository.findChatByUserId(userId);
    }
}
