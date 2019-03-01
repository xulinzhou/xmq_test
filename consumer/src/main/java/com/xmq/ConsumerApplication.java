package com.xmq;

import com.xmq.consumer.MessageConsumer;
import com.xmq.consumer.MessageConsumerNew;
import com.xmq.consumer.MessageListener;
import com.xmq.message.BaseMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

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
@Slf4j
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class ConsumerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);


    }
    @Resource
    private MessageConsumerNew messageConsumer;
    @Override
    public void run(String... args) throws Exception {

        messageConsumer.addListener("test1", "group1", new MessageListener() {
            public void onMessage(BaseMessage msg) {
                log.info("normal messageId is " + msg.getMessageId());
            }
        });

        System.in.read();

    }
}
