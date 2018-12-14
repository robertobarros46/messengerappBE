package com.daitan.messenger.message.controller;

import com.daitan.messenger.message.model.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.daitan.messenger.constants.ConstantsUtils.SUBSCRIBE_TO_RECEIVE_MESSAGE_URN;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatBrokerControllerImplTest {

    private static final String SEND_MESSAGE = "/messenger/chat";

    @Value("${local.server.port}")
    private int port;
    private String URL;

    private Message message;

    private CompletableFuture<Message> completableFuture;

    @Before
    public void setUp() throws Exception {
        completableFuture = new CompletableFuture<>();
        URL = "ws://localhost:" + port + "/socket/";
        message = new Message(UUID.randomUUID().toString(),UUID.randomUUID().toString(),  "Hello, hello!!!", "5c07ee05d7e90c36d3a93a2a", "Roberto","e82df7f2-64ed-4127-ac49-57b3e3c65ef5");
    }

    @WithMockUser(authorities = "ROLE_ADMIN")
    @Test
    public void sendMessage() throws InterruptedException, ExecutionException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connect(URL, new StompSessionHandlerAdapter() {
        }).get(1, TimeUnit.SECONDS);

        System.out.println("URL " + URL);//+ "5bd9d69179fe6c209ae90bc1"

        stompSession.subscribe(SUBSCRIBE_TO_RECEIVE_MESSAGE_URN + "5c07ee38d7e90c36d3a93a2b", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders stompHeaders) {
                System.out.println(stompHeaders.toString());
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object o) {
                System.out.println((Message) o + " RECEIVED!!!");
                completableFuture.complete((Message) o);
            }
        });

        stompSession.send(SEND_MESSAGE, message);

        Message message = completableFuture.get(5, TimeUnit.SECONDS);

        assertNotNull(message);
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

}
