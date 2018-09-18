package com.xmq.producer;

import com.xmq.message.BaseMessage;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.producer
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/18 19:38
 * @Version: 1.0
 */
public interface MessageProducer {
    void sendMessage(BaseMessage message);
}
