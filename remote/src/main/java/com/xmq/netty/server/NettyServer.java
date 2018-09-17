package com.xmq.netty.server;

import com.xmq.netty.MsgpackDecoder;
import com.xmq.netty.MsgpackEncoder;
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
public class NettyServer {
    /**
     * 日志
     */
    private Logger log = LoggerFactory.getLogger(NettyServer.class);


    private int port  = 10000;

    /**
     * 启动服务器方法
     *
     * @param
     */
    public void start() {
        System.out.println("netty服务启动: [port:" + port + "]");
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

                    ch.pipeline().addLast( new ServerChannelHandlerAdapter());
                }
            });
            // 绑定端口,开始接收进来的连接
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            log.info("netty服务启动: [port:" + port + "]");
            // 等待服务器socket关闭
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("netty服务启动异常-" + e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
