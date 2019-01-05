package com.xmq.producer.client;

import com.alibaba.fastjson.JSON;
import com.xmq.message.BaseMessage;
import com.xmq.netty.Datagram;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.client
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/16 15:27
 * @Version: 1.0
 */
@Slf4j
public class NettyClientHandler  extends SimpleChannelInboundHandler<Datagram> {


    private ConcurrentMap<Channel,ResponseFuture> channelMap = new ConcurrentHashMap(10);
    private BaseMessage message;

    public   NettyClientHandler(BaseMessage message){
        this.message =message;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Datagram datagram) throws Exception {
        log.info("====>send message "+JSON.toJSONString(datagram));
        ResponseFuture future =  channelMap.get(ctx.channel());
        future.executeCallBack();
        //
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("active");
    }
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


    public ResponseFuture process(ResponseFuture.Callback  callBack, Channel channel , long timeout){
         ResponseFuture future = new ResponseFuture(timeout,callBack);
         channelMap.putIfAbsent(channel,future);
         return future;
    }
    // 连接成功后，向server发送消息
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("active1111111111");
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
