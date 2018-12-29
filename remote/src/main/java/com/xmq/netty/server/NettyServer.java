package com.xmq.netty.server;
import com.xmq.handler.QueueHandler;
import com.xmq.netty.*;
import com.xmq.resolver.ZKClient;
import com.xmq.util.Constants;
import com.xmq.util.IpUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.netty
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/16 14:00
 * @Version: 1.0
 */

public class NettyServer {

    private Logger log = LoggerFactory.getLogger(NettyServer.class);

    /**
     * 端口号
     */
    /*@Value("${netty.port}")
    private int port = 1000;

    @Value("${broker.path}broker/")
    private String path;*/
    private int port = 1000;
    private QueueHandler queue;

    private final NioEventLoopGroup bossGroup;
    private final NioEventLoopGroup workerGroup;
    private final ServerBootstrap bootstrap;
    private volatile Channel channel;
    private final ServerChannelHandlerAdapter serverHandler;

    /**
     * 启动服务器方法
     * @param
     */
    public  NettyServer(String name,int port) {
        this.queue = queue;
        this.bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory(name + "-netty-server-boss", true));
        this.workerGroup = new NioEventLoopGroup(10, new DefaultThreadFactory(name + "-netty-server-worker", true));;
        this.bootstrap = new ServerBootstrap();
        this.serverHandler = new ServerChannelHandlerAdapter();
        this.port = port;

    }

    public void start() {
        log.info("netty服务启动: [port: {}]",port);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast("encoder", new EncodeHandler());
                        ch.pipeline().addLast("decoder", new DecodeHandler());
                        ch.pipeline().addLast("dispatcher", serverHandler);
                    }
                });
        try {
            channel = bootstrap.bind(port).await().channel();


            //ZKClient client = new ZKClient( IpUtil.getServerIp()+":2181");
            //client.addEphemeralNode(path+IpUtil.getServerIp());
          /*  // 绑定端口,开始接收进来的连接
            ChannelFuture channelFuture = bootstrap.bind(port).sync();*/
            // 等待服务器socket关闭
            channel.closeFuture().sync();

        } catch (Exception e) {
            log.error("server start fail", e);
            e.printStackTrace();
        }
        log.info("listen on port {}", port);
    }
    public void registerProcessor(final int requestCode,
                                  final RequestProcessor processor,
                                  final ExecutorService executorService) {
        serverHandler.registerProcessor(requestCode, processor, executorService);
    }

}
