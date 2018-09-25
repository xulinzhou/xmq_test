package com.xmq.netty.server;
import com.xmq.handler.QueueHandler;
import com.xmq.netty.MsgpackDecoder;
import com.xmq.netty.MsgpackEncoder;
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

    private Logger log = LoggerFactory.getLogger(NettyServer.class);

    /**
     * 端口号
     */
    @Value("${netty.port}")
    private int port;

    @Value("${broker.path}")
    private String path;

    private QueueHandler queue;
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
                    ch.pipeline().addLast( new ServerChannelHandlerAdapter(queue));
                }
            });
            ZKClient client = new ZKClient( IpUtil.getServerIp()+":2181");
            client.addEphemeralNode(path+IpUtil.getServerIp());
            // 绑定端口,开始接收进来的连接
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
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
