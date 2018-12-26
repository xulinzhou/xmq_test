package com.xmq.server;

import com.xmq.producer.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.server
 * @Description: 连接meta 服务器，发送 heat beat 消息
 * @Author: xulinzhou
 * @CreateDate: 2018/12/25 14:45
 * @Version: 1.0
 */
public class BrokerRegister {
    private static final Logger LOG = LoggerFactory.getLogger(BrokerRegister.class);

    private  NettyClient client;
    private final ScheduledExecutorService heartbeatScheduler;

    public BrokerRegister(String ip,int port) {
        client = new NettyClient();
        client.connect();
        heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
    }
    public void  start(){
        heartbeatScheduler.scheduleAtFixedRate(()->heartbeat() ,0, 1, TimeUnit.SECONDS);
    }

    private void heartbeat() {
        LOG.info("send message");
        client.sendMessage(null);
    }
}
