package com.xmq.producer.client;

import com.xmq.message.BaseMessage;
import com.xmq.netty.MsgpackDecoder;
import com.xmq.netty.MsgpackEncoder;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component
public class NettyClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
    /**
     * 主机
     */
    private String host;

    /**
     * 端口号
     */
    @Value("${netty.port}")
    private int port;

    @Value("${zookeeper.ip}")
    private String zk;

    private BaseMessage message;
    /**
     * 构造函数
     */
    public NettyClient() {
        this.host = "127.0.0.1";
    }

    /**
     * 连接方法
     */
    public void connect() {
        LOGGER.info("port:"+port);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            io.netty.bootstrap.Bootstrap bootstrap = new io.netty.bootstrap.Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    //  ch.pipeline().addLast("msgpack decoder",new MsgpackDecoder());
                    //这里设置通过增加包头表示报文长度来避免粘包
                    ch.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1024, 0, 2,0,2));
                    //增加解码器
                    ch.pipeline().addLast("msgpack decoder",new MsgpackDecoder());
                    //这里设置读取报文的包头长度来避免粘包
                    ch.pipeline().addLast("frameEncoder",new LengthFieldPrepender(2));
                    //增加编码器
                    ch.pipeline().addLast("msgpack encoder",new MsgpackEncoder());
                    ch.pipeline().addLast(new NettyClientHandler(message));
                }
            });
            ChannelFuture f = bootstrap.connect(host, port).sync();
           f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
    public void sendMessage(BaseMessage message){
        this.message = message;
        connect();
    }
}
