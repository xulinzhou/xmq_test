package com.xmq.message;

import lombok.Data;

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
public class BaseMessage implements Serializable {
    private String messageId;
    private String subject;
    private static final long expiredDelay = 10 * 3600;
    private String groupName;
    private Object msg;
    private Long expireTime;

}
