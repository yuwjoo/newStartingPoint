package com.example.gamecontroller.entity;

/**
 * 解析的消息实体
 */
public class Message {
    public final int type; // 消息类型
    public final Object body; // 消息主体

    public Message(int type, Object body) {
        this.type = type;
        this.body = body;
    }
}
