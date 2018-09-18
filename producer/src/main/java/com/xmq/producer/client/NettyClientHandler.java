package com.xmq.producer.client;

import com.alibaba.fastjson.JSON;
import com.xmq.message.BaseMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.client
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/16 15:27
 * @Version: 1.0
 */
public class NettyClientHandler  extends ChannelHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientHandler.class);

    private BaseMessage message;

    public   NettyClientHandler(BaseMessage message){
        this.message =message;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("SimpleClientHandler.channelRead");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
    // 连接成功后，向server发送消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("====>send message "+JSON.toJSONString(message));
        ctx.write(message);
        ctx.flush();
    }
}
