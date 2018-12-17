package com.daitan.messenger.message.repository;

import com.daitan.messenger.constants.ConstantsUtils;
import com.daitan.messenger.exception.UserNotFoundException;
import com.daitan.messenger.message.model.Chat;
import com.daitan.messenger.message.model.ChatResponse;
import com.daitan.messenger.message.model.Message;
import com.daitan.messenger.message.service.MessageService;
import com.daitan.messenger.users.model.User;
import com.daitan.messenger.users.model.UserProfile;
import com.daitan.messenger.users.service.UserService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChatRepositoryImpl implements ChatRepository {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Autowired
    private UserService userService;

    private MessageRepository messageRepository;

    @Override
    public void createChat(String chatName, String chatType, String... userId) {
        List<Put> putList = new ArrayList<>();
        String chatId = UUID.randomUUID().toString();
        for (String u: userId) {
            Chat chat = new Chat(UUID.randomUUID().toString(), chatId, chatName, u, chatType);
            Put put = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
            mapperChat(chat, put);
            putList.add(put);
        }

        hbaseTemplate.execute(ConstantsUtils.CHAT_TABLE, hTableInterface -> {
            hTableInterface.put(putList);
            return null;
        });
    }

    @Override
    public void createChat(Chat chat) {
        saveChat(chat);
    }

    @Override
    public void insertUserToChat(String chatId, String... userId) {

    }

    @Override
    public void updateChat(String chatId, String chatName) {

    }

    @Override
    public void removeUserByChatIdAndUserId(String chatId, String userId) {

    }

    @Override
    public void removeUser(String uniqueId) {

    }

    @Override
    public Page<ChatResponse> findAllChats(String emitter, String receptor, String content, int page, int size) {
        Map<String, ChatResponse> chatMap = Maps.newHashMap();
        List<Chat> chats;
        Pageable pageable = PageRequest.of(page, size);
        if(!Strings.isBlank(emitter) && !Strings.isBlank(receptor) && !Strings.isBlank(content)){
            String emitterId = userService.findByEmail(emitter).map(User::getId).orElseThrow(UserNotFoundException::new);
            String receptorId = userService.findByEmail(receptor).map(User::getId).orElseThrow(UserNotFoundException::new);
            List<Chat> chatsFromEmitter = findChatByUserId(emitterId);
            List<Chat> chatsFromReceptor = findChatByUserId(receptorId);
            List<Chat> chatsFromContent = messageRepository.findMessageByContent(content).stream()
                    .map(message -> findChat(message.getChatId()))
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            chatsFromEmitter.retainAll(chatsFromReceptor);
            chatsFromEmitter.retainAll(chatsFromContent);
            chats = chatsFromEmitter;
        }else{
            Scan scan = new Scan();
            scan.addFamily(Chat.columnFamillyChatAsBytes);
            List<Chat> foundChats = findChatByScan(scan);
            chats = foundChats;
        }

        for (Chat chat: chats) {
            if (chatMap.containsKey(chat.getChatId())) {
                chatMap.get(chat.getChatId()).setChatsName(chat.getChatName());
                chatMap.get(chat.getChatId()).setUserIds(chat.getUserId());
            }else{
                ChatResponse chatResponse = new ChatResponse();
                chatResponse.setChatId(chat.getChatId());
                chatResponse.setRowId(chat.getRow());
                chatResponse.setChatsName(Sets.newHashSet(chat.getChatName()));
                chatResponse.setUserIds(Lists.newArrayList(chat.getUserId()));
                chatMap.put(chat.getChatId(), chatResponse);
            }
        }

        List<ChatResponse> chatsResponse = new ArrayList<>(chatMap.values());
        if(chatsResponse.size() < size){
            size = chatsResponse.size();
        }
        List<ChatResponse> subList = chatsResponse.subList(page*size, size*( page + 1));
        Page<ChatResponse> messagePage = new PageImpl<>(subList, pageable, chatsResponse.size());
        return messagePage;
    }

    @Override
    public void deleteChat(String chatId) {
        List<Chat> chats = findChat(chatId);
        for(Chat chat: chats){
            Delete delete = new Delete(Bytes.toBytes(chat.getRow()));
            hbaseTemplate.execute(ConstantsUtils.CHAT_TABLE, hTableInterface -> {
                hTableInterface.delete(delete);
                return null;
            });
        }
    }

    private void saveChat(Chat chat) {
        Put put = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        mapperChat(chat, put);

        hbaseTemplate.execute(ConstantsUtils.CHAT_TABLE, hTableInterface -> {
            hTableInterface.put(put);
            return null;
        });
    }

    private void mapperChat(Chat chat, Put put) {
        put.add(Chat.columnFamillyChatAsBytes,
                Chat.chatIdAsBytes,
                Bytes.toBytes(chat.getChatId()));
        put.add(Chat.columnFamillyChatAsBytes,
                Chat.chatNameAsBytes,
                Bytes.toBytes(chat.getChatName()));
        put.add(Chat.columnFamillyChatAsBytes,
                Chat.userIdAsBytes,
                Bytes.toBytes(chat.getUserId()));
        put.add(Chat.columnFamillyChatAsBytes,
                Chat.chatTypeAsBytes,
                Bytes.toBytes(chat.getChatType()));
    }

    @Override
    public List<Chat> findChat(String chatId) {
        SingleColumnValueFilter filter = new SingleColumnValueFilter(Chat.columnFamillyChatAsBytes,
                Chat.chatIdAsBytes,
                CompareFilter.CompareOp.EQUAL,
                new BinaryComparator(Bytes.toBytes(chatId)));
        return findChatByFilter(filter);
    }

    @Override
    public List<Chat> findChatByUserId(String userId) {
        SingleColumnValueFilter filter = new SingleColumnValueFilter(Chat.columnFamillyChatAsBytes,
                Chat.userIdAsBytes,
                CompareFilter.CompareOp.EQUAL,
                new BinaryComparator(Bytes.toBytes(userId)));
        filter.setFilterIfMissing(true);

        return findChatByFilter(filter);
    }

    private List<Chat> findChatByFilter(Filter filter) {
        Scan scan = new Scan();
        scan.setFilter(filter);
        scan.addFamily(Chat.columnFamillyChatAsBytes);
        return findChatByScan(scan);
    }

    private List<Chat> findChatByScan(Scan scan) {
        return hbaseTemplate.find(ConstantsUtils.CHAT_TABLE, scan, (result, i) ->
                Chat.bytesToChat(result.getRow(),
                        result.getValue(Chat.columnFamillyChatAsBytes, Chat.chatIdAsBytes),
                        result.getValue(Chat.columnFamillyChatAsBytes, Chat.chatNameAsBytes),
                        result.getValue(Chat.columnFamillyChatAsBytes, Chat.userIdAsBytes),
                        result.getValue(Chat.columnFamillyChatAsBytes, Chat.chatTypeAsBytes),
                        result.raw()[0].getTimestamp())
        );
    }

    @Override
    public List<UserProfile> findUsersByChatId(String chatId){
        List<Chat> chats = findChat(chatId);
        return chats.stream()
                .map(chat -> userService.findById(chat.getUserId()))
                .map(Optional::get)
                .map(user -> new UserProfile(user.getId(), user.getEmail(), user.getName(), user.getRole()))
                .collect(Collectors.toList());
    }
}
