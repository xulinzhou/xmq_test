package com.xmq;

import com.xmq.message.BaseMessage;
import com.xmq.producer.MessageProducer;
import com.xmq.producer.impl.MessageProducerProvider;

/**
 * @ProjectName: xmq
 * @Package: com.xmq
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2019/1/6 0:48
 * @Version: 1.0
 */
public class ProducerStartup {
    public static void main(String[] args) {

        MessageProducer messageProducer = new MessageProducerProvider();
        BaseMessage message =new BaseMessage("11","test","group1");
        messageProducer.sendMessage(message);
    }
}
