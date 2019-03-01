package com.xmq.producer.client;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.AbstractFuture;
import com.xmq.exception.ClientSendException;
import com.xmq.exception.TimeOutException;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.List;

@Component
@Slf4j
public class NettyClientZk {
    private String host;
    @Value("${netty.port}")
    private int port = 1000;

    @Value("${zookeeper.ip}")
    private String zk = "192.168.253.128:2181";
    private BaseMessage message;
    /**
     * 构造函数
     */
    public NettyClientZk() {
        this.host = "127.0.0.1";
    }


    /**
     * 连接方法
     */
    public void connect() {
        log.info("port:"+port);
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
                    ch.pipeline().addLast(new NettyClientHandlerZk(message));
                }
            });

            ZKClient client =    new ZKClient(IpUtil.getServerIp()+":2181");
            client.addPersistentNode(Constants.MQ_ZK_ROOT);
            client.addPersistentNode(Constants.MQ_ZK_ROOT+"/"+message.getSubject());
            client.addPersistentNode(Constants.MQ_ZK_ROOT+"/"+message.getSubject()+"/"+message.getGroupName());
            String path  = Constants.MQ_ZK_ROOT+"/"+message.getSubject()+"/"+message.getGroupName()+"/broker";


            List<String> paths = client.getChildren(path);
            if(null == paths  && paths.size() == 0 ){
                log.error("没有可以提供服务的broker");
            }else{
                log.info("server list"+JSON.toJSONString(paths));
                LoadBalance balance  = new RandomLoadBalance();
                String pa =  balance.select(paths);
                ChannelFuture f = bootstrap.connect(pa, port).sync();
                f.channel().closeFuture().sync();
            }

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
