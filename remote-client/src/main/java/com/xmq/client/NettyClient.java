package com.xmq.client;

import com.xmq.netty.MsgpackDecoder;
import com.xmq.netty.MsgpackEncoder;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NettyClient
 * <p>
 * create by 叶云轩 at 2018/3/3-下午2:07
 * contact by tdg_yyx@foxmail.com
 *
 * @author 叶云轩 contact by tdg_yyx@foxmail.com
 * @date 2018/8/15 - 12:30
 */
public class NettyClient {
    /**
     * NettyClient 日志控制器
     * Create by 叶云轩 at 2018/3/3 下午2:08
     * Concat at tdg_yyx@foxmail.com
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);

    /**
     * 主机
     */
    private String host;

    /**
     * 端口号
     */
    private int port;

    /**
     * 构造函数
     *
     * @param host
     * @param port
     */
    public NettyClient(String host, int port) {
        this.host = "127.0.0.1";
        this.port = 10000;
    }

    /**
     * 连接方法
     */
    public void connect() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            io.netty.bootstrap.Bootstrap bootstrap = new io.netty.bootstrap.Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                  //  ch.pipeline().addLast("msgpack decoder",new MsgpackDecoder());
                    //ch.pipeline().addLast("msgpack encoder",new MsgpackEncoder());
                    //这里设置通过增加包头表示报文长度来避免粘包
                    ch.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1024, 0, 2,0,2));
                    //增加解码器
                    ch.pipeline().addLast("msgpack decoder",new MsgpackDecoder());
                    //这里设置读取报文的包头长度来避免粘包
                    ch.pipeline().addLast("frameEncoder",new LengthFieldPrepender(2));
                    //增加编码器
                    ch.pipeline().addLast("msgpack encoder",new MsgpackEncoder());
                    ch.pipeline().addLast(new NettyClientHandler());
                }
            });
           /* Channel channel = bootstrap.connect(host, port).sync().channel();

            RemoteData data = new RemoteData();
            Class cls = com.xmq.message.BrokerMessageService.class;
            data.setInterfaceClass(cls);
            data.setMethodName("send");

            // 发送json字符串
            channel.writeAndFlush(data);
            channel.writeAndFlush(data);
            channel.closeFuture().sync();*/
            // Start the client.
            ChannelFuture f = bootstrap.connect(host, port).sync();
            // Wait until the connection is closed.
           f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
