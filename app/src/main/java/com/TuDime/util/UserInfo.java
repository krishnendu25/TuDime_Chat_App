package com.TuDime.util;


public class UserInfo {

    private final String[] infoArray;
    private final String chatUserName;
    private boolean isGroup = false;

    public UserInfo(String messageUserId) {
        this.chatUserName = messageUserId;
        infoArray = messageUserId.split("_");
    }

    public UserInfo setAsGroup(boolean isGroup) {
        this.isGroup = isGroup;
        return this;
    }

    public String getChatUserName() {
        return chatUserName;
    }

    public String getUserId() {
        try {
            int position = isGroup ? 1 : 3;
            return infoArray[position];
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public String getPhoneNum() {
        try {
            int position = isGroup ? 1 : 2;
            return infoArray[position];
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public String getCountryCode() {
        return infoArray[1];
    }
}
