package com.daitan.messenger.user.service;

import com.daitan.messenger.message.model.Message;
import com.daitan.messenger.message.repository.MessageRepository;
import com.daitan.messenger.message.service.MessageServiceImpl;
import com.daitan.messenger.users.model.PagedResponse;
import com.daitan.messenger.users.model.User;
import com.daitan.messenger.users.service.UserService;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessageServiceTest {

    private MessageRepository messageRepositoryMock;
    private MessageServiceImpl messageServiceImpl;
    private UserService userServiceMock;
    private Message message;
    private Message message2;
    private List<Message> messages;
    private User user;



    @Before
    public void setUp() {
        user = new User("test", "test", "test@test.com.br", "123456", "ROLE_ADMIN");
        message = new Message(UUID.randomUUID().toString(), "41651s65d1g166ds1g51s1fd", "test", "11fsd1f65sd1fs65df", "tester", "f15sa9f1a9sd1f65s1651fasdf-asgag5");
        message2 = new Message(UUID.randomUUID().toString(), "41651s654fd166ds1g51s1fe", "test 2", "11fsd1f65sd1fs65df", "tester", "f15sa9f1a9sd1f65s1651fasdf-asgag5");
        messages = Lists.newArrayList(message, message2);
        messageRepositoryMock = mock(MessageRepository.class);
        userServiceMock = isNull(userServiceMock) ? mock(UserService.class) : userServiceMock ;
        messageServiceImpl = new MessageServiceImpl(messageRepositoryMock);
    }

    @Test
    public void createMessage(){
        messageServiceImpl.createMessage(message);
        verify(messageRepositoryMock, times(1)).createMessage(any());
    }

    @Test
    public void findMessageByChatId(){
        List<Message> expectedChats = Lists.newArrayList();
        expectedChats.add(message);
        expectedChats.add(message2);
        when(messageRepositoryMock.findMessageByChatId(any())).thenReturn(messages);
        List<Message> actualChats = messageServiceImpl.findMessageByChatId("f15sa9f1a9sd1f65s1651fasdf-asgag5");
        assertEquals(expectedChats, actualChats);
    }

    @Test
    public void findAllMessages(){
        List<Message> expectedMessages = Lists.newArrayList();
        expectedMessages.add(message);
        expectedMessages.add(message2);
        when(userServiceMock.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(messageRepositoryMock.findAllMessages(anyString(), anyString(), anyInt(), anyInt())).thenReturn(new PageImpl<>(expectedMessages));
        PagedResponse<Message> actualChats = messageServiceImpl.findAllMessages("tester","tester2", 0,  2);
        PagedResponse<Message> expectedPagedResponse = new PagedResponse<>(messages, 0, 0, 2, 1, true);
        assertEquals(expectedPagedResponse.toString().trim(), actualChats.toString().trim());
    }
}
