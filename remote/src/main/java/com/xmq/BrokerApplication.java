package com.xmq;

import com.xmq.config.Config;
import com.xmq.dispatch.DispatchThread;
import com.xmq.handler.*;
import com.xmq.netty.server.NettyServer;
import com.xmq.netty.server.NettyServerZk;
import com.xmq.store.db.IDbStore;
import com.xmq.store.db.impl.DbStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
@AutoConfigureAfter(Handler.class)
@Slf4j
public class BrokerApplication implements CommandLineRunner {



    @Resource
    Config config;
    @Resource
    private NettyServerZk nettyServerZk;

    public static void main(String[] args) {
        SpringApplication.run(BrokerApplication.class, args);
    }

    /*@Override
    public void run(String... args) {
        start();
    }
    public void start() {
        try {
             NettyServer nettyServer = new NettyServer("",8888);
            nettyServer.start();;

        }catch (Exception e){
            e.printStackTrace();
        }

    }
*/

    @Override
    public void run(String... args) {
        start();
    }
    public void start(){
        IDbStore store  =    new DbStore(config);
        QueueHandler queue = new QueueHandler();
        DbHandler dbHandler = new DbHandler(store);
        FileHandler fileHandler = new FileHandler(config);
        queue.setNextHandler(dbHandler);
        dbHandler.setNextHandler(fileHandler);
        //多线程处理队列数据
        new DispatchThread(queue).start();

        nettyServerZk.start(queue);
    }
}
