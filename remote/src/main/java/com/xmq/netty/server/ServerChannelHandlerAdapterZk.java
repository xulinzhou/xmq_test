package com.xmq.netty.server;

import com.alibaba.druid.support.json.JSONUtils;
import com.google.common.collect.Maps;
import com.xmq.handler.QueueHandler;
import com.xmq.message.BaseMessage;
import com.xmq.netty.Datagram;
import com.xmq.netty.RemotingHeader;
import com.xmq.netty.RequestExecutor;
import com.xmq.netty.RequestProcessor;
import com.xmq.util.IpUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.netty.server
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/16 15:04
 * @Version: 1.0
 */
@Component
@ChannelHandler.Sharable
public class ServerChannelHandlerAdapterZk implements ChannelInboundHandler {

    private QueueHandler queue;
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerChannelHandlerAdapter.class);

    public ServerChannelHandlerAdapterZk(QueueHandler queue) {
        this.queue = queue;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

    }

    /**
     * 客户端连接到服务端后进行
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        LOGGER.info("客户端连接");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws  Exception {
        String clientIp = IpUtil.getClientIp(ctx);
        List<Value> msg_new = (List<Value>)msg;
        BaseMessage baseMessage = MessagePack.unpack(MessagePack.pack(msg_new), BaseMessage.class);

        if(null!=baseMessage){
            queue.add(baseMessage);
        }
        LOGGER.info("SimpleServerHandler.channelRead");
        LOGGER.info("msgs"+msg.toString());
    }

}
