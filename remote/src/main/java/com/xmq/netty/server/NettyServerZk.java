package com.xmq.netty.server;

import com.xmq.handler.QueueHandler;
import com.xmq.netty.MsgpackDecoder;
import com.xmq.netty.MsgpackEncoder;
import com.xmq.resolver.ZKClient;
import com.xmq.util.IpUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
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

/**
 * @ProjectName: xmq
 * @Package: com.xmq.netty
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/16 14:00
 * @Version: 1.0
 */
@Component
public class NettyServerZk {

    private Logger log = LoggerFactory.getLogger(NettyServerZk.class);

    /**
     * 端口号
     */
    @Value("${netty.port}")
    private int port = 1000;

    @Value("${broker.path}broker/")
    private String path;
    //private int port = 1000;
    private QueueHandler queue;

   /* private final NioEventLoopGroup bossGroup;
    private final NioEventLoopGroup workerGroup;
    private final ServerBootstrap bootstrap;
    private volatile Channel channel;
    private final ServerChannelHandlerAdapter serverHandler;
*/
    /**
     * 启动服务器方法
     * @param
     *//*
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
          *//*  // 绑定端口,开始接收进来的连接
            ChannelFuture channelFuture = bootstrap.bind(port).sync();*//*
            // 等待服务器socket关闭
            //channel.closeFuture().sync();

        } catch (Exception e) {
            log.error("server start fail", e);
            e.printStackTrace();
        }
        log.info("listen on port {}", port);
    }*/


    /*public void registerProcessor(final int requestCode,
                                  final RequestProcessor processor,
                                  final ExecutorService executorService) {
        serverHandler.registerProcessor(requestCode, processor, executorService);
    }*/


    /**
     * 启动服务器方法
     * @param
     */
    public void start(final QueueHandler queue) {
        this.queue = queue;
        log.info("netty服务启动: [port: {}]",port);
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
            serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1024, 0, 2,0,2));
                    ch.pipeline().addLast("msgpack decoder",new MsgpackDecoder());
                    ch.pipeline().addLast("frameEncoder",new LengthFieldPrepender(2));
                    ch.pipeline().addLast("msgpack encoder",new MsgpackEncoder());
                    ch.pipeline().addLast( new ServerChannelHandlerAdapterZk(queue));
                }
            });
            String ip = IpUtil.getServerIp();

            ZKClient client = new ZKClient( ip+":2181");
            log.info("broker ip "+ip);
            client.addEphemeralNode(path+ip);
            // 绑定端口,开始接收进来的连接
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            // 等待服务器socket关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("netty服务启动异常-" + e.getMessage());
        } finally {
            //bossGroup.shutdownGracefully();
            //workerGroup.shutdownGracefully();
        }
    }


}
