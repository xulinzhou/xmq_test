package com.xmq;

import com.xmq.message.BaseMessage;
import com.xmq.producer.MessageProducer;
import com.xmq.producer.impl.MessageProducerProvider;

/**
 * @ProjectName: xmq
 * @Package: com.xmq
 * @Description: mq发送消息
 * @Author: xulinzhou
 * @CreateDate: 2019/1/6 0:48
 * @Version: 1.0
 */
public class ProducerStartup {
    public static void main(String[] args) {
        ProducerStartup pro =  new ProducerStartup();
        pro.registerProducer();
        pro.sendMessage();
    }

    public void registerProducer(){
        String ip = "127.0.0.1";
        /*BrokerRegister register = new BrokerRegister(ip, 7777);
        register.start();*/
    }

    public void sendMessage(){
        MessageProducer messageProducer = new MessageProducerProvider();
        BaseMessage message =new BaseMessage("11","test","group1");
        messageProducer.sendMessage(message);
    }
}