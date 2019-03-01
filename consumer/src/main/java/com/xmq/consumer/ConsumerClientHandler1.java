package com.xmq.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xmq.exception.ClientSendException;
import com.xmq.message.BaseMessage;
import com.xmq.netty.Datagram;
import com.xmq.producer.client.ResponseFuture;
import com.xmq.util.BufferCovert;
import com.xmq.util.MessageTypeEnum;
import com.xmq.util.MyThreadFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

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
public class ConsumerClientHandler1 extends SimpleChannelInboundHandler<Datagram> {


    private ConcurrentMap<Channel,ResponseFuture> channelMap = new ConcurrentHashMap(10);


    private BaseMessage message;
    private ScheduledExecutorService timer ;
    public ConsumerClientHandler1(){
        timer  =  Executors.newSingleThreadScheduledExecutor(new MyThreadFactory("client-timer"));
        timer.scheduleAtFixedRate(()->time(),
        3000,
        1000,
        TimeUnit.MILLISECONDS);
    }
    public ConsumerClientHandler1(BaseMessage message){
        this.message =message;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Datagram datagram) throws Exception {
        log.info("====>receive message "+JSON.toJSONString(datagram));

        String str = BufferCovert.convertByteBufToString(datagram.getBody());
        log.info("====>message"+ str);
        if( datagram.getHeader().getRequestCode() == MessageTypeEnum.CONSUMER_DATA_INFO.getType()){
            log.info("====>message"+ str);
        }
        if(!str.equalsIgnoreCase("heartbeat")
                ){
            JSONObject obj = JSONObject.parseObject(str);
            ProcessHandler processHandler = new ConsumerHandlerNew();
            processHandler.getBroker(obj.get("content").toString());
            ctx.close();
            ctx.channel();
        }

        if( datagram.getHeader().getRequestCode() == MessageTypeEnum.CONSUMER_DATA_INFO.getType()){

        }

        //ResponseFuture future =  channelMap.get(ctx.channel());
        //log.info("====>future========="+future);
        //if(null==future) return ;
        //future.executeCallBack();
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


    public ResponseFuture process(ResponseFuture.Callback  callBack, Channel channel , long timeout) throws  ClientSendException{
         ResponseFuture future = new ResponseFuture(timeout,callBack);
         channelMap.putIfAbsent(channel,future);
         //channelMap.putIfAbsent(channel,future)!=null){
           //  throw new ClientSendException("发送消息异常");
         //};
         return future;
    }
    // 连接成功后，向server发送消息
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("channelActivechannelActivechannelActivechannelActive");
    }


    public void time(){

        channelMap.forEach((k,v)->{
                ResponseFuture future = v;
                if(isTimeout(future)){
                    future.completeByTimeoutClean();
                }
                log.info("channel : " + k + " value : " + v);
              });
    }

    private boolean isTimeout(ResponseFuture future) {
        return future.getTimeout() >= 0 && (future.getBegintime() + future.getTimeout()) <= System.currentTimeMillis();
    }

}
