package com.xmq.producer.client;

import com.alibaba.fastjson.JSON;
import com.xmq.message.BaseMessage;
import com.xmq.message.UserInfo;
import com.xmq.netty.Datagram;
import com.xmq.netty.RemotingHeader;
import com.xmq.resolver.ZKClient;
import com.xmq.resolver.ZkResolver;
import com.xmq.util.Constants;
import com.xmq.util.IpUtil;
import com.xmq.util.MessageTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.client
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/16 15:27
 * @Version: 1.0
 */
public class NettyClientHandler  extends SimpleChannelInboundHandler<Datagram> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientHandler.class);

    private BaseMessage message;

    public   NettyClientHandler(BaseMessage message){
        this.message =message;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Datagram datagram) throws Exception {
        LOGGER.info("====>send message "+JSON.toJSONString(datagram));
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
    // 连接成功后，向server发送消息
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        /*for (int i = 0; i < 100; i++) {
            message.setMessageId(String.valueOf(i));
            LOGGER.info("====>send message "+JSON.toJSONString(message));

            Datagram datagram = new Datagram();
            final RemotingHeader header = new RemotingHeader();
            header.setMagicCode(RemotingHeader.DEFAULT_MAGIC_CODE);
            header.setRequestCode(MessageTypeEnum.SYN_MESSAGE_CLIENT.getType());
            String messageStr = JSON.toJSONString(message);
            header.setLength(messageStr.length());
            ByteBuf buf = Unpooled.copiedBuffer(messageStr, Charset.forName("UTF-8"));//创建一个ByteBuf
            datagram.setBody(buf);
            datagram.setHeader(header);
            ctx.writeAndFlush(datagram).addListener((new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) {
                    if (future.isSuccess()) {
                        System.out.println("send success");
                        return;
                    }else{
                        LOGGER.error("send request to broker failed.", future.cause());
                    }
                }
            }));
        }*/
    }
}
