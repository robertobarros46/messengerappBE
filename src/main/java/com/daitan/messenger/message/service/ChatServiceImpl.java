package com.daitan.messenger.message.service;

import com.daitan.messenger.exception.UserNotFoundException;
import com.daitan.messenger.message.model.Chat;
import com.daitan.messenger.message.model.ChatResponse;
import com.daitan.messenger.message.repository.ChatRepository;
import com.daitan.messenger.users.model.PagedResponse;
import com.daitan.messenger.users.model.User;
import com.daitan.messenger.users.model.UserProfile;
import com.daitan.messenger.users.service.UserService;
import org.springframework.data.domain.Page;
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
        for (Chat chat : chats) {
            Optional<User> optionalUser = userService.findById(chat.getUserId());
            if (!optionalUser.isPresent()) {
                throw new UserNotFoundException("404", "User: " + chat.getUserId() + " not found please try again!! ");
            }
        }
        String chatId = UUID.randomUUID().toString();
        for (Chat chat : chats) {
            chat.setChatId(chatId);
            chatRepository.createChat(chat);
        }
    }

    @Override
    public void updateChat(List<Chat> chats, String chatId) {
        chatRepository.updateChat(chats, chatId);
    }


    @Override
    public List<Chat> findChat(String chatId) {
        return chatRepository.findChat(chatId);
    }

    @Override
    public PagedResponse<ChatResponse> findAllChats(String emitter, String receptor, String content, int page, int size) {
        return getPagedResponse(chatRepository.findAllChats(emitter, receptor, content, page, size));
    }

    private PagedResponse<ChatResponse> getPagedResponse(Page<ChatResponse> chatPage) {
        PagedResponse<ChatResponse> pagedResponse;
        if (chatPage.getNumberOfElements() == 0) {
            pagedResponse = new PagedResponse<>(Collections.emptyList(), chatPage.getNumber(),
                    chatPage.getSize(), chatPage.getTotalElements(), chatPage.getTotalPages(), chatPage.isLast());

        } else {
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
    public void deleteChat(String chatId) {
        chatRepository.deleteChat(chatId);
    }

    @Override
    public List<UserProfile> findUsersByChat(String chatId) {
        return chatRepository.findUsersByChatId(chatId);
    }

}
