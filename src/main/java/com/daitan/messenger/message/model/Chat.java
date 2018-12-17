package com.daitan.messenger.message.model;

import com.daitan.messenger.constants.ConstantsUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Objects;

public class Chat {

    public static final byte[] tableNameAsBytes = Bytes.toBytes(ConstantsUtils.CHAT_TABLE);
    public static final String columnFamillyChat = "CF_CHAT";
    public static final byte[] columnFamillyChatAsBytes = Bytes.toBytes(columnFamillyChat);
    public static final byte[] chatIdAsBytes = Bytes.toBytes("chatId");
    public static final byte[] chatNameAsBytes = Bytes.toBytes("chatName");
    public static final byte[] userIdAsBytes = Bytes.toBytes("userId");
    public static final byte[] chatTypeAsBytes = Bytes.toBytes("chatType");

    private String row;

    private String chatId;

    private String chatName;

    private String userId;

    private String chatType;

    private long timestamp;


    @JsonCreator
    public Chat(@JsonProperty(value = "row", required = false) String row,
                @JsonProperty(value = "chatId", required = false) String chatId,
                @JsonProperty(value = "chatName", required = false) String chatName,
                @JsonProperty(value = "userId", required = false) String userId,
                @JsonProperty(value = "chatType", required = false) String chatType) {
        this.row = row;
        this.chatId = chatId;
        this.chatName = chatName;
        this.userId = userId;
        this.chatType = chatType;
    }

    public static final Chat bytesToChat(byte[] row, byte[] chatId, byte[] chatName, byte[] userId, byte[] chatType, long timestamp) {
        Chat chat = new Chat(Bytes.toString(row), Bytes.toString(chatId), Bytes.toString(chatName), Bytes.toString(userId), Bytes.toString(chatType));
        chat.setTimestamp(timestamp);
        return chat;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        return timestamp == chat.timestamp &&
                Objects.equals(row, chat.row) &&
                Objects.equals(chatId, chat.chatId) &&
                Objects.equals(chatName, chat.chatName) &&
                Objects.equals(userId, chat.userId) &&
                Objects.equals(chatType, chat.chatType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, chatId, chatName, userId, chatType, timestamp);
    }

    @Override
    public String toString() {
        return "Chat{" +
                "row='" + row + '\'' +
                ", chatId='" + chatId + '\'' +
                ", chatName='" + chatName + '\'' +
                ", userId='" + userId + '\'' +
                ", chatType='" + chatType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
