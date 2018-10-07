package com.xmq.message;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.annotation.Message;

import java.io.Serializable;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.client
 * @Description: 消息基础类
 * @Author: xulinzhou
 * @CreateDate: 2018/9/17 9:52
 * @Version: 1.0
 */
@Data
@Message
public class BaseMessage  {
    private String messageId;
    private String subject;
    private static final long expiredDelay = 10 * 3600;
    private String groupName;

    public BaseMessage(String messageId, String subject,String groupName) {
        this.messageId = messageId;
        this.subject = subject;
        this.groupName = groupName;
    }
    public BaseMessage(){

    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public static long getExpiredDelay() {
        return expiredDelay;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
