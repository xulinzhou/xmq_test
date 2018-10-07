package com.xmq.consumer;

import com.xmq.message.BaseMessage;
import com.xmq.util.IpUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandlerInvoker;
import io.netty.util.concurrent.EventExecutorGroup;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.consumer
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/10/7 10:11
 * @Version: 1.0
 */
@Slf4j
public class ConsumerMessageHandler extends ChannelHandlerAdapter {

    private MessageListener listener;

    public ConsumerMessageHandler(MessageListener listener) {
        this.listener = listener;
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

    /**
     * 客户端连接到服务端后进行
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        log.info("客户端连接");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws  Exception {
        String clientIp = IpUtil.getClientIp(ctx);
        List<Value> msg_new = (List<Value>)msg;
        BaseMessage baseMessage = MessagePack.unpack(MessagePack.pack(msg_new), BaseMessage.class);

        listener.onMessage(baseMessage);
    }

}