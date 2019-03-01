package com.xmq.consumer;

import com.google.common.util.concurrent.AbstractFuture;
import com.xmq.exception.ClientSendException;
import com.xmq.exception.TimeOutException;
import com.xmq.message.BaseMessage;
import com.xmq.netty.Datagram;
import com.xmq.netty.DecodeHandler;
import com.xmq.netty.EncodeHandler;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConsumerClient1 {
    private String host;
    @Value("${netty.port}")
    private int port;

    @Value("${zookeeper.ip}")
    private String zk;
    private BaseMessage message;

    public ConsumerClient1(){};
    /**
     * 构造函数
     */
    public ConsumerClient1(String ip) {
        clientHandler1 = new ConsumerClientHandler1();
        if(StringUtils.isEmpty(ip)){
            this.host = "127.0.0.1";
        }else{
            this.host = ip;
        }
    }
    private io.netty.bootstrap.Bootstrap bootstrap;

    private ChannelFuture f;


    private ConsumerClientHandler1 clientHandler1;

    /**
     * 连接方法
     */
    public void connect(int port) {
        if(port !=0 ){
            this.port = port;
        }
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
                    ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));//這裡的位置有問題
                    ch.pipeline().addLast(clientHandler1);
                }
            });
            log.info("=======>host:"+host+"============>port:"+port);
            f = bootstrap.connect(host, port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void close(){

    }


    /**
     * 发送消息
     * @param address
     * @param datagram
     */
    public  void synMessage(String address,Datagram datagram,long timeout){
        try {
           final ResultFuture future = new ResultFuture();

           final  com.xmq.producer.client.ResponseFuture responseFuture
                   =  clientHandler1.process(future,f.channel(),timeout);
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
