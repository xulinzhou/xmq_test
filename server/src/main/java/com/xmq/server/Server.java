package com.xmq.server;

import com.xmq.netty.server.NettyServer;
import com.xmq.util.IpUtil;
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
public class Server {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);


    public static void main(String[] args) {

        LOG.info("broker init started");
        new Server().register();
        /*NettyServer nettyServer = new NettyServer("",8888);
        nettyServer.start();*/
    }

    private void register(){
        String ip = IpUtil.getServerIp();
        BrokerRegister register =     new BrokerRegister(ip,7777);
        register.start();
    }
}
