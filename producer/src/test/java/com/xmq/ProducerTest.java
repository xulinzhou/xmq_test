package com.xmq;

import com.xmq.message.BaseMessage;
import com.xmq.producer.MessageProducer;
import com.xmq.producer.client.NettyClient;
import com.xmq.producer.impl.MessageProducerProvider;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @ProjectName: xmq
 * @Package: com.xmq
 * @Description: mq发送消息
 * @Author: xulinzhou
 * @CreateDate: 2019/1/6 0:48
 * @Version: 1.0
 */
@Threads(value = 1)
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@State(Scope.Thread)
public class ProducerTest {





    public static void main(String[] args) {
        ProducerRegisterStartup pro =  new ProducerRegisterStartup();
        pro.registerProducer();
        pro.sendMessage();



       /* Options opt = new OptionsBuilder()
                .include(ProducerTest.class.getSimpleName())
                .forks(1)
                .warmupIterations(3)
                .measurementIterations(5)
                .build();*/

        /*try {
            new Runner(opt).run();
        } catch (RunnerException e) {
            e.printStackTrace();
        }*/
    }

    public void registerProducer(){
        String ip = "127.0.0.1";
        /*BrokerRegister register = new BrokerRegister(ip, 7777);
        register.start();*/
    }
    NettyClient client = null;
    //@Benchmark
    public void sendMessage(){
        MessageProducer messageProducer = new MessageProducerProvider();
        BaseMessage message =new BaseMessage("11","test","group1");
        if(client==null){
            System.out.println("null");
            client = new NettyClient();
            client.connect();
            client.heatBeat();
            client.sendMessage(message);
        }else{
            System.out.println("not null send====================");
            client.sendMessage(message);
        }

    }
}
