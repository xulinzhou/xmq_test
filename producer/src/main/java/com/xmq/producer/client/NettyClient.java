package com.xmq.producer.client;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.AbstractFuture;
import com.xmq.loadbalance.LoadBalance;
import com.xmq.loadbalance.RandomLoadBalance;
import com.xmq.message.BaseMessage;
import com.xmq.netty.*;

import com.xmq.resolver.ZKClient;
import com.xmq.util.Constants;
import com.xmq.util.IpUtil;
import com.xmq.util.MessageTypeEnum;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import qunar.tc.qmq.netty.client.ResponseFuture;

import java.nio.charset.Charset;
import java.util.List;

@Component
@Slf4j
public class NettyClient {
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
    private io.netty.bootstrap.Bootstrap bootstrap;

    private ChannelFuture f;


    private NettyClientHandler clientHandler;

    /**
     * 连接方法
     */
    public void connect() {
        log.info("port:"+port);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            bootstrap = new  io.netty.bootstrap.Bootstrap();
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

            f = bootstrap.connect("127.0.0.1", 7777);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(BaseMessage message){
        this.message = message;
        connect();
    }


   /* public  void synMessage(String address,Datagram datagram){
        try {
            port = 7777;
            LOGGER.info("port:"+port);

            ChannelFuture f = bootstrap.connect(address, port).sync();
            *//*f.channel().writeAndFlush(datagram).addListener((new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        System.out.println("send message  success");
                        return;
                    }else{
                        LOGGER.error("send  failed.", future.cause());
                    }
                }
            }));
            f.channel().closeFuture().sync();*//*
            f.channel().writeAndFlush(null);
            //f.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();

            LOGGER.error("eeeeeeeeeeeeeeeee");
        }
    }*/
    /**
     * 发送消息
     * @param address
     * @param datagram
     */
    public  void synMessage(String address,Datagram datagram,long timeout){
        try {
            //异步方法，放到多线程里面处理，处理完后
            ResultFuture future = new ResultFuture();
           final  com.xmq.producer.client.ResponseFuture responseFuture =  clientHandler.process(future,f.channel(),timeout);
            log.info("send message  chanel================"+f);
            f.channel().writeAndFlush(datagram).addListener((new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        responseFuture.setOk(true);
                        log.info("send message  success");
                        return;
                    }else{
                        log.error("send  failed.", future.cause());
                    }
                }
            }));;
        }catch (Exception e){
            log.info(e.getMessage());
            e.printStackTrace();
        }
    }



    public class ResultFuture extends AbstractFuture<Datagram> implements ResponseFuture.Callback {

        @Override
        public void processResponse(ResponseFuture responseFuture) {
            System.out.println("1111111111111111111");
        }
    }
}
