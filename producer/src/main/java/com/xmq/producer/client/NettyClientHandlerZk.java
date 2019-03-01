package com.xmq.producer.client;

import com.alibaba.fastjson.JSON;
import com.xmq.exception.ClientSendException;
import com.xmq.message.BaseMessage;
import com.xmq.netty.Datagram;
import com.xmq.util.MyThreadFactory;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.client
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/16 15:27
 * @Version: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyClientHandlerZk extends  SimpleChannelInboundHandler<Datagram> {

    private BaseMessage message;

    public   NettyClientHandlerZk(BaseMessage message){
        this.message =message;
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Datagram datagram) throws Exception {
        log.info("SimpleClientHandler.channelRead");
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
        for(int i=0;i<100;i++){
            log.info("====>send message "+JSON.toJSONString(message));
            ctx.write(message);
            ctx.flush();
        }

    }
}

