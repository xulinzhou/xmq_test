package com.xmq.producer.client;

import com.alibaba.fastjson.JSON;
import com.xmq.loadbalance.LoadBalance;
import com.xmq.loadbalance.RandomLoadBalance;
import com.xmq.message.BaseMessage;
import com.xmq.netty.*;

import com.xmq.resolver.ZKClient;
import com.xmq.util.Constants;
import com.xmq.util.IpUtil;
import com.xmq.util.MessageTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.List;

@Component
public class NettyClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClient.class);
    private String host;
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
            bootstrap.option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, false);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("encoder", new EncodeHandler());
                    ch.pipeline().addLast("decoder", new DecodeHandler());
                    ch.pipeline().addLast(new NettyClientHandler(message));
                }
            });

           /* ZKClient client =    new ZKClient(IpUtil.getServerIp()+":2181");
            client.addPersistentNode(Constants.MQ_ZK_ROOT);
            client.addPersistentNode(Constants.MQ_ZK_ROOT+"/"+message.getSubject());
            client.addPersistentNode(Constants.MQ_ZK_ROOT+"/"+message.getSubject()+"/"+message.getGroupName());
            client.addPersistentNode(Constants.MQ_ZK_ROOT+"/"+message.getSubject()+"/"+message.getGroupName()+"/broker");
            String path  = Constants.MQ_ZK_ROOT+"/"+message.getSubject()+"/"+message.getGroupName()+"/broker";

            List<String> paths = client.getChildren(path);
            if(null == paths  && paths.size() == 0 ){
                LOGGER.error("没有可以提供服务的broker");
            }else{
                LOGGER.info("server list"+JSON.toJSONString(paths));
                LoadBalance balance  = new RandomLoadBalance();
                String pa =  balance.select(paths);
                LOGGER.info("pa"+JSON.toJSONString(pa));
                ChannelFuture f = bootstrap.connect(pa, port).sync();
                f.channel().closeFuture().sync();


            }*/

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
