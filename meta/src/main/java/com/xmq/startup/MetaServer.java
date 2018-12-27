package com.xmq.startup;

import com.xmq.netty.server.NettyServer;
import com.xmq.processor.BrokerRegisterProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.startup
 * @Description: 注册中心启动
 * @Author: xulinzhou
 * @CreateDate: 2018/12/24 19:14
 * @Version: 1.0
 */
public class MetaServer {
    private static final Logger log = LoggerFactory.getLogger(MetaServer.class);

      public void start(){
          log.info("start meta server");
          BrokerRegisterProcessor process = new BrokerRegisterProcessor();
          final NettyServer metaNettyServer = new NettyServer("meta",7777);
          metaNettyServer.registerProcessor((short)10,process,null);
          metaNettyServer.registerProcessor((short)1,null,null);

          metaNettyServer.start();


      }
}
