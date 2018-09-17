package com.xmq.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

    @Override
    public void run(String... args) throws Exception {
        //int sum = demoService.sum(5, 8);
        NettyClient clent = new NettyClient("",1000);
        clent.connect();
    }
}
