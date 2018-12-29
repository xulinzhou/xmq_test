package com.xmq;

import com.google.common.base.Preconditions;
import com.xmq.server.Server;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.consumer.MessageConsumerProvider;
import qunar.tc.qmq.consumer.annotation.QmqConsumer;

import java.io.IOException;
import java.util.concurrent.*;

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

     /* Executor executor = null;
      Preconditions.checkNotNull(executor, "消费逻辑将在该线程池里执行");

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

        System.in.read();*/
     Server abc = new Server();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("=============");
        }));
        System.out.println("bac");


    }




}
