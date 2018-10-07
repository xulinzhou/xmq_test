package com.xmq;

import com.xmq.config.Config;
import com.xmq.dispatch.DispatchThread;
import com.xmq.handler.*;
import com.xmq.netty.server.NettyServer;
import com.xmq.store.db.IDbStore;
import com.xmq.store.db.impl.DbStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
@AutoConfigureAfter(Handler.class)
@Slf4j
public class NettyApplication implements CommandLineRunner {

    @Resource
    private NettyServer nettyServer;


    @Resource
    Config config;

    public static void main(String[] args) {
        SpringApplication.run(NettyApplication.class, args);
    }

    @Override
    public void run(String... args) {
        start();
    }
    public void start() {
        try {
            IDbStore store  =    new DbStore(config);
            QueueHandler queue = new QueueHandler();
            DbHandler dbHandler = new DbHandler(store);
            FileHandler fileHandler = new FileHandler(config);
            queue.setNextHandler(dbHandler);
            dbHandler.setNextHandler(fileHandler);
            //多线程处理队列数据
            new DispatchThread(queue).start();
            nettyServer.start(queue);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
