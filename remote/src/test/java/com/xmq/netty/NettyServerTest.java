package com.xmq.netty;

import com.xmq.netty.server.NettyServer;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.netty
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/12/20 20:09
 * @Version: 1.0
 */
public class NettyServerTest {

    public static void main(String[] args) {
       NettyServer server = new  NettyServer();
       server.start();;
    }
}
