package com.xmq;

import com.xmq.message.BaseMessage;
import com.xmq.producer.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private NettyClient nettyClient;
    @Override
    public void run(String... args) throws Exception {


        BaseMessage message =new BaseMessage("11","1111","222");

        nettyClient.sendMessage(message);

    }
}
