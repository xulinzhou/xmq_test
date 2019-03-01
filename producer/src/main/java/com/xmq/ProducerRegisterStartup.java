package com.xmq;

import com.xmq.message.BaseMessage;
import com.xmq.producer.MessageProducer;
import com.xmq.producer.client.NettyClient;
import com.xmq.producer.client.NettyClientZk;
import com.xmq.producer.impl.MessageProducerProvider;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Benchmark;
/**
 * @ProjectName: xmq
 * @Package: com.xmq
 * @Description: mq发送消息
 * @Author: xulinzhou
 * @CreateDate: 2019/1/6 0:48
 * @Version: 1.0
 */

public class ProducerRegisterStartup {





    public static void main(String[] args) {
        ProducerRegisterStartup pro =  new ProducerRegisterStartup();
        pro.registerProducer();
        pro.sendMessage();

    }

    public void registerProducer(){
        String ip = "127.0.0.1";
        /*BrokerRegister register = new BrokerRegister(ip, 7777);
        register.start();*/
    }
    NettyClient client = null;
    public void sendMessage(){
        MessageProducer messageProducer = new MessageProducerProvider();
        BaseMessage message =new BaseMessage("11","test","group1");
        if(client!=null){
            client = new NettyClient();
            client.heatBeat();
            client.sendMessage(message);
        }else {
            client.sendMessage(message);
        }
    }
}
