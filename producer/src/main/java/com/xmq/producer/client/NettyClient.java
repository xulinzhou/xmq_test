package com.xmq.producer.client;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.AbstractFuture;
import com.xmq.exception.ClientSendException;
import com.xmq.exception.TimeOutException;
import com.xmq.message.BaseMessage;
import com.xmq.netty.Datagram;
import com.xmq.netty.DecodeHandler;
import com.xmq.netty.EncodeHandler;
import com.xmq.netty.RemotingHeader;
import com.xmq.util.MessageTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

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
        clientHandler = new NettyClientHandler();
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
                    ch.pipeline().addLast(clientHandler);
                }
            });

            f = bootstrap.connect("127.0.0.1", 7777);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     *心跳
     */
    public void heatBeat() {
        connect();
        Datagram datagram  = new Datagram();
        String dataStr = JSON.toJSONString(message);
        ByteBuf buf = Unpooled.copiedBuffer(dataStr, Charset.forName("UTF-8"));
        datagram.setBody(buf);
        RemotingHeader header = new RemotingHeader();
        header.setRequestCode(MessageTypeEnum.SYN_MESSAGE_BROKER.getType());
        header.setLength(0);
        header.setMagicCode(RemotingHeader.DEFAULT_MAGIC_CODE);
        datagram.setHeader(header);

    }

    public void sendMessage(BaseMessage message){
        this.message = message;
        for(int i = 0;i<1000;i++){
            System.out.println("=============="+i);
        Datagram datagram  = new Datagram();
        String dataStr = JSON.toJSONString(message);
        ByteBuf buf = Unpooled.copiedBuffer(dataStr, Charset.forName("UTF-8"));
        datagram.setBody(buf);
        RemotingHeader header = new RemotingHeader();
        header.setRequestCode(MessageTypeEnum.SYN_DATA.getType());
        header.setLength(dataStr.length());
        header.setMagicCode(RemotingHeader.DEFAULT_MAGIC_CODE);
        datagram.setHeader(header);
        System.out.println("========="+JSON.toJSONString(datagram));
        try {
           // final ResultFuture future = new ResultFuture();

                f.channel().writeAndFlush(datagram);

            /*final  com.xmq.producer.client.ResponseFuture responseFuture
                    =  clientHandler.process(future,f.channel(),1000);
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
            }));;*/
        }catch (Exception e){
            log.info(e.getMessage());
            e.printStackTrace();
        }

        }
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

        @Override
        public void processResponse(com.xmq.producer.client.ResponseFuture responseFuture) {

            if (!responseFuture.isOk()) {
                setException(new ClientSendException("发送异常"));
                return;
            }
            if (responseFuture.isIstimeout()) {
                setException(new TimeOutException("超时"));
                return;
            }
            log.info("send success");
        }
    }
}
