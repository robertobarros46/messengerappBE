package com.daitan.messenger.user.controller;

import com.daitan.messenger.MessengerApplication;
import com.daitan.messenger.TestUtil;
import com.daitan.messenger.message.model.Chat;
import com.daitan.messenger.message.service.ChatService;
import com.daitan.messenger.message.service.MessageService;
import com.daitan.messenger.users.controller.UserController;
import com.daitan.messenger.users.model.User;
import com.daitan.messenger.users.service.UserService;
import com.google.common.collect.Lists;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MessengerApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String USER_URI_BASE = "/api/v1/users";
    private static final String CHAT_URI_BASE = "/api/v1/chats";


    private UserService userService = mock(UserService.class);
    private ChatService chatService = mock(ChatService.class);
    private MessageService messageService = mock(MessageService.class);

    private MockMvc mockMvc;

    private UserController userController;

    private User user;
    private List<Chat> chats;


    @Before
    public void setup() {
        user = new User("Roberto", "Barros", "roberto.netto@gmail.com.br", "admin", "ADMIN");
        chats = Lists.newArrayList(
                new Chat(UUID.randomUUID().toString(), "osad651fd65sfds3165fdsf-fdsf65165", "test", "kg91h5k1gh6jkg", "CHAT"),
                new Chat(UUID.randomUUID().toString(), "osad651fd65sfds3165fdsf-fdsf65165", "test1", "1651gfdg216d5f1gfdg2v", "CHAT"));
        userController = new UserController(userService, chatService, messageService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testGetUsers() throws Exception {
        String uri = USER_URI_BASE + "?page=1&size=10";
        this.mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isPartialContent());
    }

    @Test
    public void testGetUser() throws Exception {
        String uri = USER_URI_BASE + "/roberto.netto@gmail.com.br";

        when(userService.findByEmail(anyString())).thenReturn(Optional.of(user));

        mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateUsers() throws Exception {
        String uri = USER_URI_BASE;

        this.mockMvc.perform(post(uri)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateUsers() throws Exception {
        String uri = USER_URI_BASE;

        this.mockMvc.perform(put(uri)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteUsers() throws Exception {
        String uri = USER_URI_BASE + "?email=roberto.netto@gmail.com.br";

        when(userService.findIdByEmail(user.getEmail())).thenReturn(Optional.of(user));

        doNothing().when(userService).deleteById(user.getId());

        this.mockMvc.perform(delete(uri))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService, times(1)).findIdByEmail(user.getEmail());
        verify(userService, times(1)).deleteById(user.getId());
        verifyNoMoreInteractions(userService);
    }

    @Test
    public void saveChat() throws Exception {
        String uri = CHAT_URI_BASE;

        this.mockMvc.perform(put(uri)
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(chats)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void findAllMessages() throws Exception {
        String uri = CHAT_URI_BASE + "/messages" + "?emitter=&receptor=&page=0&size=5";

        mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isPartialContent());
    }

    @Test
    public void findMessagesByChatId() throws Exception{

        String uri = CHAT_URI_BASE + "/ddasfdgtrthrtgre65561fad16af" +"/messages";
        mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isAccepted());
    }

    @Test
    public void findChatsByUserId() throws Exception{
        String uri = CHAT_URI_BASE + "/s6a51d1as919fa5f";

        mockMvc.perform(get(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}