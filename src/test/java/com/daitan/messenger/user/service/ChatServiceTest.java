package com.daitan.messenger.user.service;

import com.daitan.messenger.message.model.Chat;
import com.daitan.messenger.message.repository.ChatRepository;
import com.daitan.messenger.message.service.ChatServiceImpl;
import com.daitan.messenger.users.model.User;
import com.daitan.messenger.users.service.UserService;
import com.google.common.collect.Lists;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChatServiceTest {

    private ChatServiceImpl chatServiceImpl;

    private ChatRepository chatRepositoryMock;

    private UserService userServiceMock;

    private List<Chat> chats;

    private User user;

    @Before
    public void setUp() {
        user = new User("test", "test", "test@test.com.br", "123456", "ROLE_ADMIN");
        chats = Lists.newArrayList(
                new Chat(UUID.randomUUID().toString(), "osad651fd65sfds3165fdsf-fdsf65165", "test", "5c07ee05d7e90c36d3a93a2a"),
                new Chat(UUID.randomUUID().toString(),"osad651fd65sfds3165fdsf-fdsf65165", "test1", "5c07ee38d7e90c36d3a93a2b"));
        chatRepositoryMock = mock(ChatRepository.class);
        userServiceMock = isNull(userServiceMock) ? mock(UserService.class) : userServiceMock ;
        chatServiceImpl = new ChatServiceImpl(chatRepositoryMock, userServiceMock);
    }


    @Test
    public void createChatOneToOne(){
        when(userServiceMock.findById(any())).thenReturn(Optional.of(user));
        chatServiceImpl.createChatOneToOne(chats);
        verify(chatRepositoryMock, times(2)).createChat(any());
    }

    @Test
    public void findChat(){
        List<Chat> expectedChats = Lists.newArrayList();
        expectedChats.addAll(chats);
        when(chatRepositoryMock.findChat(any())).thenReturn(chats);
        List<Chat> actualChats = chatServiceImpl.findChat("4f56d4sf89s4df1sfadfsd");
        assertEquals(expectedChats, actualChats);
    }

    @Test
    public void findChatByUserId(){
        List<Chat> expectedChats = Lists.newArrayList();
        expectedChats.addAll(chats);
        when(chatRepositoryMock.findChatByUserId(any())).thenReturn(chats);
        List<Chat> actualChats = chatServiceImpl.findChatByUserId("4f56d4sf89s4df1sfadfsd");
        assertEquals(expectedChats, actualChats);
    }
}
