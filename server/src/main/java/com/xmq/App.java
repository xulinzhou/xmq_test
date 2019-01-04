package com.xmq;

import com.google.common.base.Preconditions;
import com.xmq.server.BrokerServer;
import qunar.tc.qmq.consumer.MessageConsumerProvider;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) throws IOException {
        /*ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring-test.xml");
        context.start();


        System.in.read();*/

     //Executor executor = null;
    //  Preconditions.checkNotNull(executor, "消费逻辑将在该线程池里执行");

        //推荐一个应用里只创建一个实例
        MessageConsumerProvider consumer = new MessageConsumerProvider();
        consumer.setAppCode("test");
        consumer.setMetaServer("http://127.0.0.1:8080/meta/address");
        consumer.init();

        consumer.addListener("test.subject", "group", (m) -> {
            System.out.println(m.getMessageId());
            //process message
        }, new ThreadPoolExecutor(2,2,  60L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>()));

        System.in.read();
   /*  BrokerServer abc = new BrokerServer();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("=============");
        }));
        System.out.println("bac");*/


    }




}
