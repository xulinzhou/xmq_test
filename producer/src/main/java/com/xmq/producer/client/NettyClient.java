package com.xmq.producer.client;

import com.google.common.util.concurrent.AbstractFuture;
import com.xmq.message.BaseMessage;
import com.xmq.netty.Datagram;
import com.xmq.netty.DecodeHandler;
import com.xmq.netty.EncodeHandler;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
           final ResultFuture future = new ResultFuture();

           final  com.xmq.producer.client.ResponseFuture responseFuture
                   =  clientHandler.process(future,f.channel(),timeout);
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



    public class ResultFuture extends AbstractFuture<Datagram> implements com.xmq.producer.client.ResponseFuture.Callback {

       /* @Override
        public void processResponse(ResponseFuture responseFuture) {
            System.out.println("1111111111111111111");
        }*/

        @Override
        public void processResponse(com.xmq.producer.client.ResponseFuture responseFuture) {
            System.out.println("tests");
        }
    }
}
