package com.shared.dto;

import java.io.Serializable;



public class Message implements Serializable {

    private MessageType msgType;
    private String payload;

    public MessageType getMsgType() {
        return msgType;
    }

    public void setMsgType(MessageType msgType) {
        this.msgType = msgType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Message(MessageType msgType, String data) {
        this.msgType = msgType;
        this.payload = data;
    }

    public static Message single(String payload){return new Message(MessageType.SINGLE, payload);}

    public static Message broadcast(String payload){
        return new Message(MessageType.BROADCAST, payload);
    }

}
