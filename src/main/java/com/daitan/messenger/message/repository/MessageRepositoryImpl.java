package com.daitan.messenger.message.repository;

import com.daitan.messenger.constants.ConstantsUtils;
import com.daitan.messenger.message.model.Chat;
import com.daitan.messenger.message.model.Message;
import com.daitan.messenger.users.model.PagedResponse;
import com.daitan.messenger.users.model.User;
import com.daitan.messenger.users.service.UserService;
import com.google.common.collect.Lists;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.hadoop.hbase.HbaseTemplate;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MessageRepositoryImpl implements MessageRepository {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserService userService;

    @Override
    public void createMessage(Message message) {
        saveMessage(message);
    }

    @Override
    public List<Message> findMessageByChatId(String chatId) {
        SingleColumnValueFilter singleColumnValueFilter = createSingleColumnFilter(chatId, Message.chatIdAsBytes);
        singleColumnValueFilter.setReversed(true);

        Scan scan = createScan(singleColumnValueFilter);
        List<Message> messages = findMessageByScan(scan);

        Collections.sort(messages, Comparator.naturalOrder());

        return messages;
    }

    @Override
    public PagedResponse findAllMessages(String emitter, String receptor, int page, int size) {
        List<Message> messages = Lists.newArrayList();
        Pageable pageable = PageRequest.of(page, size);
        if(!Strings.isBlank(emitter) && Strings.isBlank(receptor)){
            SingleColumnValueFilter singleColumnValueFilter = createSingleColumnFilter(emitter, Message.fromNameAsBytes);
            singleColumnValueFilter.setReversed(true);
            Scan scan = createScan(singleColumnValueFilter);
            messages = findMessageByScan(scan);
        }else if (Strings.isBlank(emitter) && !Strings.isBlank(receptor)){
            Optional<User> user = userService.findByEmail(receptor);
            if(user.isPresent()){
                String id = user.get().getId();
                List<Chat> chats = chatRepository.findChatByUserId(id);
                messages = chats.stream()
                        .map(chat -> getAllMessagesFromChats(chat.getChatId()))
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
            }
        }else if(!Strings.isBlank(emitter) && !Strings.isBlank(receptor)){
            Optional<User> emitterUser = userService.findByEmail(emitter);
            Optional<User> receptorUser = userService.findByEmail(receptor);
            if(emitterUser.isPresent() && receptorUser.isPresent()){
                List<String> chatsFromEmitter = chatRepository.findChatByUserId(emitterUser.get().getId()).stream()
                        .map(Chat::getChatId).collect(Collectors.toList());
                List<String> chatsFromReceptor = chatRepository.findChatByUserId(receptorUser.get().getId()).stream()
                        .map(Chat::getChatId).collect(Collectors.toList());;
                chatsFromEmitter.retainAll(chatsFromReceptor);
                messages = chatsFromEmitter.stream()
                        .map(this::getAllMessagesFromChats)
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
            }
        }else{
            Scan scan = createScan();
            messages = findMessageByScan(scan);
            messages.sort(Comparator.comparing(Message::getTimestamp));
        }
        if(messages.size() < size){
            size = messages.size();
        }
        List<Message> subList = messages.subList(page*size, size*( page + 1));
        Page<Message> messagePage = new PageImpl<>(subList, pageable, messages.size());
        return getPagedResponse(messagePage);
    }

    private SingleColumnValueFilter createSingleColumnFilter(String column, byte[] byteColumn ) {
        return new SingleColumnValueFilter(Message.columnFamillyMessageAsBytes,
                byteColumn,
                CompareFilter.CompareOp.EQUAL,
                new BinaryComparator(Bytes.toBytes(column)));
    }

    private PagedResponse getPagedResponse(Page<Message> messagePage) {
        PagedResponse pagedResponse;
        if (messagePage.getNumberOfElements() == 0) {
            pagedResponse = new PagedResponse<>(Collections.emptyList(), messagePage.getNumber(),
                    messagePage.getSize(), messagePage.getTotalElements(), messagePage.getTotalPages(), messagePage.isLast());

        }else {
            pagedResponse = new PagedResponse<>(messagePage.stream().collect(Collectors.toList()), messagePage.getNumber(),
                    messagePage.getSize(), messagePage.getTotalElements(), messagePage.getTotalPages(), messagePage.isLast());
        }
        return pagedResponse;
    }

    private List<Message> getAllMessagesFromChats(String chatId) {
        List<Message> messages = findMessageByChatId(chatId);
        return messages;
    }

    private void saveMessage(Message message) {
        Put put = new Put(Bytes.toBytes(message.getMessageId()));
        mapperMessage(message, put);
        saveMessage(put);
    }

    private void saveMessage(Put put) {
        hbaseTemplate.execute(ConstantsUtils.MESSAGE_TABLE, hTableInterface -> {
            hTableInterface.put(put);
            return null;
        });
    }

    private void mapperMessage(Message message, Put put) {
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

    private Scan createScan(Filter filter) {
        Scan scan = createScan();
        scan.setFilter(filter);
        return scan;
    }

    private Scan createScan() {
        Scan scan = new Scan();
        scan.addFamily(Message.columnFamillyMessageAsBytes);
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
