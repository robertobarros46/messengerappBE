package com.daitan.messenger.message.repository;

import com.daitan.messenger.constants.ConstantsUtils;
import com.daitan.messenger.message.model.Message;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MessageRepositoryImpl implements MessageRepository {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Override
    public void createMessage(Message message) {
        saveMessage(message);
    }

    @Override
    public List<Message> findMessageByChatId(String chatId) {

        SingleColumnValueFilter singleColumnValueFilter = createChatIdFilter(chatId);
        singleColumnValueFilter.setReversed(true);

        Scan scan = createScan(singleColumnValueFilter);
        List<Message> messages = findMessageByScan(scan);

        Collections.sort(messages, Comparator.naturalOrder());

        return messages;
    }

    private SingleColumnValueFilter createChatIdFilter(String chatId) {
        return new SingleColumnValueFilter(Message.columnFamillyMessageAsBytes,
                Message.chatIdAsBytes,
                CompareFilter.CompareOp.EQUAL,
                new BinaryComparator(Bytes.toBytes(chatId)));
    }

    private void saveMessage(Message message) {
        Put put = new Put(Bytes.toBytes(message.getMessageId()));
        mapperMessageInfo(message, put);
        saveMessage(put);
    }

    private void saveMessage(Put put) {
        hbaseTemplate.execute(ConstantsUtils.MESSAGE_TABLE, hTableInterface -> {
            hTableInterface.put(put);
            return null;
        });
    }

    private void mapperMessageInfo(Message message, Put put) {
        put.add(Message.columnFamillyMessageAsBytes,
                Message.messageIdAsBytes,
                Bytes.toBytes(message.getMessageId()));
        put.add(Message.columnFamillyMessageAsBytes,
                Message.chatIdAsBytes,
                Bytes.toBytes(message.getChatId()));
        put.add(Message.columnFamillyMessageAsBytes,
                Message.fromUserIdAsBytes,
                Bytes.toBytes(message.getFromUserId()));
        put.add(Message.columnFamillyMessageAsBytes,
                Message.fromNameAsBytes,
                Bytes.toBytes(message.getFromName()));
        put.add(Message.columnFamillyMessageAsBytes,
                Message.contentAsBytes,
                Bytes.toBytes(message.getContent()));
    }

    private Scan createScan() {
        Scan scan = new Scan();
        scan.addFamily(Message.columnFamillyMessageAsBytes);
        return scan;
    }

    private Scan createScan(Filter filter) {
        Scan scan = createScan();
        scan.setFilter(filter);
        return scan;
    }

    private List<Message> findMessageByScan(Scan scan) {
        return hbaseTemplate.find(ConstantsUtils.MESSAGE_TABLE, scan, (result, i) ->
                Message.bytesToMessage(result.getValue(Message.columnFamillyMessageAsBytes, Message.messageIdAsBytes),
                        result.getValue(Message.columnFamillyMessageAsBytes, Message.contentAsBytes),
                        result.getValue(Message.columnFamillyMessageAsBytes, Message.fromUserIdAsBytes),
                        result.getValue(Message.columnFamillyMessageAsBytes, Message.fromNameAsBytes),
                        result.getValue(Message.columnFamillyMessageAsBytes, Message.chatIdAsBytes),
                        result.raw()[0].getTimestamp())
        );
    }
}
