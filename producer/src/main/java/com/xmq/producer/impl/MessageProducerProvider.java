package com.xmq.producer.impl;

import com.xmq.message.BaseMessage;
import com.xmq.producer.MessageProducer;
import com.xmq.producer.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.producer.impl
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/18 19:40
 * @Version: 1.0
 */
@Component
public class MessageProducerProvider implements MessageProducer {
    @Resource
    private NettyClient client;

   public  void sendMessage(BaseMessage message){
       client.sendMessage(message);
    }
}
