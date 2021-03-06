package com.xmq;

import com.xmq.message.BaseMessage;
import com.xmq.producer.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
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
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class ProducerApplication implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }
    @Resource
    private MessageProducer messageProducer;
    @Override
    public void run(String... args) throws Exception {

        BaseMessage message =new BaseMessage("11","test","group1");
        messageProducer.sendMessage(message);
    }
}
