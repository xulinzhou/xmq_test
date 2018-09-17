package com.xmq.netty;

import com.xmq.netty.server.NettyServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.netty
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/16 14:40
 * @Version: 1.0
 */
@SpringBootApplication
public class NettyApplication  implements CommandLineRunner {

    @Resource
    private NettyServer nettyServer;

    public static void main(String[] args) {
        SpringApplication.run(NettyApplication.class, args);
    }

    @Override
    public void run(String... args) {
        nettyServer.start();
    }
}
