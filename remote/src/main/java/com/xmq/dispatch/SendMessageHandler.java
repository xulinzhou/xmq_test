package com.xmq.dispatch;

import com.alibaba.fastjson.JSON;
import com.xmq.message.BaseMessage;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.dispatch
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/10/6 21:56
 * @Version: 1.0
 */
@Slf4j
public final class SendMessageHandler extends ChannelHandlerAdapter {

    private BaseMessage message;

    public SendMessageHandler(BaseMessage message){
        this.message =message;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("SimpleClientHandler.channelRead");
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
    // 连接成功后，向server发送消息
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("====>send message "+ JSON.toJSONString(message));
        ctx.write(message);
        ctx.flush();

    }
}
