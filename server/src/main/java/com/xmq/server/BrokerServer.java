package com.xmq.server;

import com.xmq.netty.server.NettyServer;
import com.xmq.processor.BrokerRegisterProcessor;
import com.xmq.util.MessageTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.server
 * @Description: broker 启动类
 * @Author: xulinzhou
 * @CreateDate: 2018/12/25 14:41
 * @Version: 1.0
 */
public class BrokerServer {
    private static final Logger LOG = LoggerFactory.getLogger(BrokerServer.class);


    public static void main(String[] args) {

        LOG.info("broker init started");
        new BrokerServer().register();
        new BrokerServer().server();
        //NettyServer nettyServer = new NettyServer("",8888);
        //nettyServer.start();
    }

    private void register(){
        String ip = "127.0.0.1";

        BrokerRegister register =     new BrokerRegister(ip,7777);

        register.start();
        //register.online();
    }

    private void server(){
        BrokerRegisterProcessor process = new BrokerRegisterProcessor();
        NettyServer server = new NettyServer("server",12345);
        server.registerProcessor(MessageTypeEnum.SYN_MESSAGE_BROKER.getType(),process,null);
        server.registerProcessor(1,null,null);
        server.registerProcessor(MessageTypeEnum.SYN_DATA.getType(),process,null);
        server.registerProcessor(MessageTypeEnum.CONSUMER_DATA.getType(),process,null);
        server.registerProcessor(MessageTypeEnum.CONSUMER_DATA_INFO.getType(),process,null);

        server.start();

    }
}
