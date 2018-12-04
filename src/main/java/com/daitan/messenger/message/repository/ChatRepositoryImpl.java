package com.daitan.messenger.message.repository;

import com.daitan.messenger.constants.ConstantsUtils;
import com.daitan.messenger.message.model.Chat;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatRepositoryImpl implements ChatRepository {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Override
    public void createChat(String chatName, String... userId) {
        List<Put> putList = new ArrayList<>();
        String chatId = UUID.randomUUID().toString();
        for (String u: userId) {
            Chat chat = new Chat(chatId, chatName, u);
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
    public void removeChat(String chatId) {

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
                Chat.bytesToChat(result.getValue(Chat.columnFamillyChatAsBytes, Chat.chatIdAsBytes),
                    result.getValue(Chat.columnFamillyChatAsBytes, Chat.chatNameAsBytes),
                    result.getValue(Chat.columnFamillyChatAsBytes, Chat.userIdAsBytes),
                    result.raw()[0].getTimestamp())
        );
    }
}
