package com.xmq;

import com.xmq.consumer.MessageConsumer;
import com.xmq.consumer.MessageListener;
import com.xmq.message.BaseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.client
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/16 15:21
 * @Version: 1.0
 */
@SpringBootApplication
public class ClientApplication implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
    @Resource
    private MessageConsumer messageConsumer;
    @Override
    public void run(String... args) throws Exception {
         messageConsumer.addListener("test", "group1", new MessageListener() {
            public void onMessage(BaseMessage msg) {
                System.out.println("normal messageId is " + msg.getMessageId());
            }
        });
    }
}
