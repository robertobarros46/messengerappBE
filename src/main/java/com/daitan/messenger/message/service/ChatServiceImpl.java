package com.daitan.messenger.message.service;

import com.daitan.messenger.exception.UserNotFoundException;
import com.daitan.messenger.message.model.Chat;
import com.daitan.messenger.message.model.Message;
import com.daitan.messenger.message.repository.ChatRepository;
import com.daitan.messenger.users.model.PagedResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
                throw new UserNotFoundException("404","User: "+ chat.getUserId() +" not found please try again!! ");
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
    public PagedResponse<Chat> findAllChats(String emitter, String receptor, String content, int page, int size) {
        return getPagedResponse(chatRepository.findAllChats(emitter, receptor, content, page, size));
    }

    private PagedResponse<Chat> getPagedResponse(Page<Chat> chatPage) {
        PagedResponse<Chat> pagedResponse;
        if (chatPage.getNumberOfElements() == 0) {
            pagedResponse = new PagedResponse<>(Collections.emptyList(), chatPage.getNumber(),
                    chatPage.getSize(), chatPage.getTotalElements(), chatPage.getTotalPages(), chatPage.isLast());

        }else {
            pagedResponse = new PagedResponse<>(chatPage.stream().collect(Collectors.toList()), chatPage.getNumber(),
                    chatPage.getSize(), chatPage.getTotalElements(), chatPage.getTotalPages(), chatPage.isLast());
        }
        return pagedResponse;
    }

    @Override
    public List<Chat> findChatByUserId(String userId) {
        return chatRepository.findChatByUserId(userId);
    }

    @Override
    public void deleteChat(String chatId){
        chatRepository.deleteChat(chatId);
    }
}
