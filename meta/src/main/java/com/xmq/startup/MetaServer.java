package com.xmq.startup;

import com.xmq.netty.server.NettyServer;
import com.xmq.processor.BrokerRegisterProcessor;
import com.xmq.util.MessageTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.startup
 * @Description: 注册中心启动
 * @Author: xulinzhou
 * @CreateDate: 2018/12/24 19:14
 * @Version: 1.0
 */
@Slf4j
public class MetaServer {


    public static void main(String[] args) {
        new MetaServer().start();
    }
      public void start() {
          log.info("start meta server");


          BrokerRegisterProcessor process = new BrokerRegisterProcessor();
          final NettyServer metaNettyServer = new NettyServer("meta",7777);
          metaNettyServer.registerProcessor(MessageTypeEnum.SYN_MESSAGE_BROKER.getType(),process,null);
          metaNettyServer.registerProcessor(1,null,null);
          metaNettyServer.registerProcessor(MessageTypeEnum.SYN_DATA.getType(),process,null);
          metaNettyServer.registerProcessor(MessageTypeEnum.CONSUMER_DATA.getType(),process,null);
          metaNettyServer.registerProcessor(MessageTypeEnum.CONSUMER_DATA_INFO.getType(),process,null);
          metaNettyServer.start();
          try {
              System.in.read();
          } catch (IOException e) {
              e.printStackTrace();
          }

      }
}
