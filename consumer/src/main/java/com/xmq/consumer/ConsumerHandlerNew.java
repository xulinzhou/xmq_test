package com.xmq.consumer;

import com.alibaba.fastjson.JSON;
import com.xmq.message.BaseMessage;
import com.xmq.netty.*;
import com.xmq.netty.server.NettyServer;
import com.xmq.producer.client.NettyClient;
import com.xmq.resolver.ZKClient;
import com.xmq.util.Constants;
import com.xmq.util.IpUtil;
import com.xmq.util.MessageTypeEnum;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.consumer
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/20 21:14
 * @Version: 1.0
 */

@Component("consumerHandlerNew")
@Slf4j
public class ConsumerHandlerNew implements MessageConsumerNew,ProcessHandler{

    private int port = 7777;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerHandlerNew.class);
    private io.netty.bootstrap.Bootstrap bootstrap;

    private ChannelFuture f;
    private ConsumerMessageNewHandler clientHandler;
    @Override
    public void addListener(String subjectPrefix, String group, final MessageListener listener) {
        LOGGER.info("consumer start ");
        sendMessage(subjectPrefix,group,f,listener);
    }
    public void sendMessage(String subjectPrefix, String group, ChannelFuture f,MessageListener listener){
        Datagram datagram  = new Datagram();
        ConsumerClient  client = new ConsumerClient("");
        client.connect(7777);
        BaseMessage message = new BaseMessage();
        message.setSubject(subjectPrefix);
        message.setGroupName(group);
        message.setMessageId("222222222222222222");
        message.setContent("tewt");
        String dataStr = JSON.toJSONString(message);
        ByteBuf buf = Unpooled.copiedBuffer(dataStr, Charset.forName("UTF-8"));
        datagram.setBody(buf);
        RemotingHeader header = new RemotingHeader();
        header.setRequestCode(MessageTypeEnum.CONSUMER_DATA.getType());
        header.setLength(dataStr.length());
        header.setMagicCode(RemotingHeader.DEFAULT_MAGIC_CODE);
        datagram.setHeader(header);
        client.synMessage("127.0.0.1",datagram,1000);
    }


    @Override
    public void getBroker(String ip) {

        ConsumerClient1  client = new ConsumerClient1("127.0.0.1");
        client.connect(12345);
        Datagram datagram  = new Datagram();
        BaseMessage message = new BaseMessage();
        message.setSubject("test1");
        String dataStr = JSON.toJSONString(message);
        ByteBuf buf = Unpooled.copiedBuffer(dataStr, Charset.forName("UTF-8"));
        datagram.setBody(buf);
        RemotingHeader header = new RemotingHeader();
        header.setRequestCode(MessageTypeEnum.CONSUMER_DATA_INFO.getType());
        header.setLength(dataStr.length());
        header.setMagicCode(RemotingHeader.DEFAULT_MAGIC_CODE);
        datagram.setHeader(header);
        client.synMessage(ip,datagram,1000);
    }
}
