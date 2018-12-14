package com.daitan.messenger.message.repository;

import com.daitan.messenger.constants.ConstantsUtils;
import com.daitan.messenger.message.model.Chat;
import com.daitan.messenger.message.model.Message;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChatRepositoryImpl implements ChatRepository {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Override
    public void createChat(String chatName, String... userId) {
        List<Put> putList = new ArrayList<>();
        String chatId = UUID.randomUUID().toString();
        for (String u: userId) {
            Chat chat = new Chat(UUID.randomUUID().toString(), chatId, chatName, u);
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
    public Page<Chat> findAllChats(String emitter, String receptor, String content, int page, int size) {
        Map<String, Chat> chatMap = Maps.newHashMap();
        List<Chat> chats = Lists.newArrayList();
        Pageable pageable = PageRequest.of(page, size);
        if(!Strings.isBlank(emitter) && !Strings.isBlank(receptor) && !Strings.isBlank(content)){


        }else{
            Scan scan = new Scan();
            scan.addFamily(Chat.columnFamillyChatAsBytes);
            List<Chat> foundChats = findChatByScan(scan);
            chatMap = foundChats.stream().collect(Collectors.toMap(Chat::getChatName, Function.identity()));

        }

        Page<Chat> messagePage = new PageImpl<>(chats, pageable, chats.size());
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
                        result.raw()[0].getTimestamp())
        );
    }
}
