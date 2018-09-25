package com.xmq;

import com.xmq.dispatch.DispatchThread;
import com.xmq.handler.*;
import com.xmq.netty.server.NettyServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

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
public class NettyApplication implements CommandLineRunner {

    @Resource
    private NettyServer nettyServer;

    public static void main(String[] args) {
        SpringApplication.run(NettyApplication.class, args);
    }


    @Override
    public void run(String... args) {
        start();
    }
    public void start(){
        QueueHandler queue = new QueueHandler();
        DbHandler dbHandler = new DbHandler();

        FileHandler fileHandler = new FileHandler();
        queue.setNextHandler(dbHandler);
        dbHandler.setNextHandler(fileHandler);

        new DispatchThread(queue).start();

        nettyServer.start(queue);
    }
}
