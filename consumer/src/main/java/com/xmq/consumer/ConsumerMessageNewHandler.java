package com.xmq.consumer;

import com.alibaba.fastjson.JSON;
import com.xmq.netty.Datagram;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.consumer
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/10/7 10:11
 * @Version: 1.0
 */
@Slf4j
public class ConsumerMessageNewHandler extends SimpleChannelInboundHandler<Datagram> {

    private MessageListener listener;

    public ConsumerMessageNewHandler(MessageListener listener) {
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


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Datagram msg) throws Exception {
        log.info("broker ================返回消息"+ JSON.toJSONString(msg));
    }
}