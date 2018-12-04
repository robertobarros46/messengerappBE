package com.daitan.messenger.message.model;

import com.daitan.messenger.constants.ConstantsUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Objects;

public class Message implements Comparable<Message>{

    public static final byte[] tableNameAsBytes = Bytes.toBytes(ConstantsUtils.MESSAGE_TABLE);
    public static final String columnFamillyMessageInfo = "CF_MESSAGE_INFO";
    public static final byte[] columnFamillyMessageAsBytes = Bytes.toBytes(columnFamillyMessageInfo);
    public static final byte[] messageIdAsBytes = Bytes.toBytes("messageId");
    public static final byte[] chatIdAsBytes = Bytes.toBytes("chatId");
    public static final byte[] contentAsBytes = Bytes.toBytes("content");
    public static final byte[] fromUserIdAsBytes = Bytes.toBytes("fromUserId");
    public static final byte[] fromNameAsBytes = Bytes.toBytes("fromName");

    public static final Message bytesToMessage(byte[] messageId, byte[] content, byte[] fromUserId, byte[] fromName, byte[] chatId, long timestamp) {
        Message messageInfo = new Message(Bytes.toString(messageId), Bytes.toString(content), Bytes.toString(fromUserId), Bytes.toString(fromName), Bytes.toString(chatId));
        messageInfo.setTimestamp(timestamp);
        return messageInfo;
    }

    @JsonCreator
    public Message(@JsonProperty(value = "messageId", required = false) String messageId,
                       @JsonProperty(value = "content", required = false) String content,
                       @JsonProperty(value = "fromUserId", required = false) String fromUserId,
                       @JsonProperty(value = "fromName", required = false) String fromName,
                       @JsonProperty(value = "chatId", required = false) String chatId) {
        this.messageId = messageId;
        this.content = content;
        this.fromUserId = fromUserId;
        this.fromName = fromName;
        this.chatId = chatId;
    }

    private String messageId;

    private String content;

    private String fromUserId;

    private String fromName;

    private String chatId;

    private long timestamp;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message that = (Message) o;
        return timestamp == that.timestamp &&
                Objects.equals(messageId, that.messageId) &&
                Objects.equals(fromUserId, that.fromUserId) &&
                Objects.equals(chatId, that.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageId, fromUserId, chatId, timestamp);
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "messageId='" + messageId + '\'' +
                ", content='" + content + '\'' +
                ", fromUserId='" + fromUserId + '\'' +
                ", fromName='" + fromName + '\'' +
                ", chatId='" + chatId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public int compareTo(Message o) {
        return o.getTimestamp() < timestamp ? 1 : -1;
    }

}
