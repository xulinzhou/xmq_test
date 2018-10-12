package com.xmq.consumer;

import com.alibaba.fastjson.JSON;
import com.xmq.message.BaseMessage;
import com.xmq.netty.MsgpackDecoder;
import com.xmq.netty.MsgpackEncoder;
import com.xmq.resolver.ZKClient;
import com.xmq.util.Constants;
import com.xmq.util.IpUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.consumer
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/20 21:14
 * @Version: 1.0
 */

@Component
public class ConsumerHandler implements MessageConsumer{

    private int port = 10001;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerHandler.class);

    @Override
    public void addListener(String subjectPrefix, String group, final MessageListener listener) {
        LOGGER.info("account info");
        {
            LOGGER.info("port:"+port);
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
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1024, 0, 2,0,2));
                        ch.pipeline().addLast("msgpack decoder",new MsgpackDecoder());
                        ch.pipeline().addLast("frameEncoder",new LengthFieldPrepender(2));
                        ch.pipeline().addLast("msgpack encoder",new MsgpackEncoder());
                        ch.pipeline().addLast( new ConsumerMessageHandler(listener));
                    }
                });

                ZKClient client =    new ZKClient(IpUtil.getServerIp()+":2181");
                client.addPersistentNode(Constants.MQ_ZK_ROOT);
                client.addPersistentNode(Constants.MQ_ZK_ROOT+"/"+subjectPrefix);
                client.addPersistentNode(Constants.MQ_ZK_ROOT+"/"+subjectPrefix+"/"+group);
                client.addPersistentNode(Constants.MQ_ZK_ROOT+"/"+subjectPrefix+"/"+group+"/consumer");
                String path  = Constants.MQ_ZK_ROOT+"/"+subjectPrefix+"/"+group+"/consumer";

                client.addEphemeralNode(path+ "/"+IpUtil.getServerIp());

                List<String> paths = client.getChildren(path);
                if(null == paths  && paths.size() == 0 ){
                    LOGGER.error("没有可以提供服务的broker");
                }else{
                    LOGGER.info("server list"+JSON.toJSONString(paths));

                    // 绑定端口,开始接收进来的连接
                    ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
                    // 等待服务器socket关闭
                    channelFuture.channel().closeFuture().sync();

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }
    }

}
