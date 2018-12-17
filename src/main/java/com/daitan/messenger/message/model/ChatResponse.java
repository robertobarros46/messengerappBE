package com.daitan.messenger.message.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ChatResponse {

    @JsonProperty
    private String rowId;

    @JsonProperty
    private Set<String> chatsName;

    @JsonProperty
    private String chatId;

    @JsonProperty
    private List<String> userIds;

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public Set<String> getChatsName() {
        return chatsName;
    }

    public void setChatsName(Set<String> chatsName) {
        this.chatsName = chatsName;
    }

    public void setChatsName(String chatName) {
        this.chatsName.add(chatName);
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public void setUserIds(String userId) {
        this.userIds.add(userId);
    }

    @Override
    public String toString() {
        return "ChatResponse{" +
                "rowId='" + rowId + '\'' +
                ", chatsName=" + chatsName +
                ", chatId='" + chatId + '\'' +
                ", userIds=" + userIds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatResponse that = (ChatResponse) o;
        return Objects.equals(rowId, that.rowId) &&
                Objects.equals(chatsName, that.chatsName) &&
                Objects.equals(chatId, that.chatId) &&
                Objects.equals(userIds, that.userIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowId, chatsName, chatId, userIds);
    }
}
