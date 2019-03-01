package com.xmq.processor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xmq.config.Config;
import com.xmq.message.BaseMessage;
import com.xmq.message.Broker;
import com.xmq.netty.Datagram;
import com.xmq.netty.RemotingHeader;
import com.xmq.netty.RequestProcessor;
import com.xmq.store.db.IDbStore;
import com.xmq.store.db.IDbStoreNew;
import com.xmq.store.db.impl.DbStore;
import com.xmq.store.db.impl.DbStoreNew;
import com.xmq.util.BufferCovert;
import com.xmq.util.IpUtil;
import com.xmq.util.MessageTypeEnum;
import com.xmq.util.MyThreadFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.processor
 * @Description: broker 注册
 * @Author: xulinzhou
 * @CreateDate: 2018/12/24 20:26
 * @Version: 1.0
 */
@Slf4j
public class BrokerRegisterProcessor implements RequestProcessor {

    public static final IDbStoreNew storeNew = new DbStoreNew();
    Timeout timeout = null;

    Timer timer = null;

    private final ConcurrentMap<String, Timeout> timeouts  = new ConcurrentHashMap<>();

    private static ConcurrentMap<String, ChannelHandlerContext> brokers  = new ConcurrentHashMap<>();
    @Override
    public CompletableFuture<Datagram> processRequest(ChannelHandlerContext ctx, Datagram request) {
        brokers.putIfAbsent(IpUtil.getServerIp(),ctx);
        //broker 入库
        if(request.getHeader().getRequestCode() == MessageTypeEnum.SYN_MESSAGE_BROKER.getType()){
            return CompletableFuture.completedFuture(handlerHeat(request));
        }else if(request.getHeader().getRequestCode() == MessageTypeEnum.BROKER_ONLINE.getType()){
            handOnline(request);
        }else if(request.getHeader().getRequestCode() == MessageTypeEnum.SYN_DATA.getType()){
            handProducerMessage(request);
        }else if(request.getHeader().getRequestCode() == MessageTypeEnum.CONSUMER_DATA.getType()){
            handConsumerMessage(request,ctx);
        }else if(request.getHeader().getRequestCode() == MessageTypeEnum.CONSUMER_DATA_INFO.getType()){
            handConsumerSynMessage(request,ctx);
        }
        return null;
    }
    private Datagram handlerHeat(Datagram request){
         //心跳数据入库
        log.info("heartbeat from broker");

        Broker broker = new Broker();
        broker.setTopic("test1");
        broker.setIp(IpUtil.getServerIp());
        broker.setServerPort("7777");
        storeNew.saveBroker(broker);


        String str = BufferCovert.convertByteBufToString(request.getBody());
        log.info("heat beat from broker "+ JSON.toJSONString(str));
        if(timeout !=null){
            timeout.cancel();
        }
        timeout = new HeartBeat().connect(str);
        //timeouts.get("topic").cancel();
        return new Datagram();
    }

    private void handOnline(Datagram request){
         //插入数据库，broker入库
        log.info("heatbeat from broker");

        Broker broker = new Broker();
        broker.setTopic("test1");
        broker.setIp(IpUtil.getServerIp());
        broker.setServerPort("7777");
        storeNew.saveBroker(broker);
    }
    private void handProducerMessage(Datagram request){
        //收到producer 数据

        String str = BufferCovert.convertByteBufToString(request.getBody());

        log.info("receive client str "+ JSON.toJSONString(str));

        JSONObject obj = JSONObject.parseObject(str);
        log.info("receive client message "+ JSON.toJSONString(obj));
        BaseMessage message = new BaseMessage();
        message.setSubject(obj.get("subject").toString());
        message.setGroupName(obj.get("groupName").toString());
        message.setMessageId(obj.get("messageId").toString());
        message.setContent(str);
        storeNew.saveMessage(message);


    }


    private void handConsumerMessage(Datagram request,ChannelHandlerContext ctx){
        //收到producer 数据

        String str = BufferCovert.convertByteBufToString(request.getBody());


        JSONObject obj = JSONObject.parseObject(str);
        log.info("receive consumer message "+ JSON.toJSONString(obj));
        String topic= obj.get("subject").toString();
        String groupName= obj.get("groupName").toString();
        //获取producer列表
        List<Broker> brokerList = storeNew.getBroker(topic);

        //选取一个服务器连接创建socket连接
        String ip = brokerList.get(0).getIp();
        //获取message 列表
        List<BaseMessage> messageList = storeNew.getMessages(topic,groupName);
        log.info("broker ip"+ip);

        Datagram datagram  = new Datagram();
        BaseMessage message = new BaseMessage();
        message.setContent(ip);
        String dataStr = JSON.toJSONString(message);
        ByteBuf buf = Unpooled.copiedBuffer(dataStr, Charset.forName("UTF-8"));
        datagram.setBody(buf);
        RemotingHeader header = new RemotingHeader();
        header.setRequestCode(MessageTypeEnum.CONSUMER_DATA.getType());
        header.setLength(dataStr.length());
        header.setMagicCode(RemotingHeader.DEFAULT_MAGIC_CODE);
        datagram.setHeader(header);

        ctx.channel().writeAndFlush(datagram).addListener((new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    log.info("send broker   success");
                    return;
                }else{
                    log.error("send  failed.", future.cause());
                }
            }
        }));;;
        ctx.channel().close();
        ctx.close();
        //ChannelHandlerContext ctx =  brokers.get(ip);
        //ctx.writeAndFlush(JSON.toJSONString(messageList));
        //发送socket数据
        log.info("process consumer message");
    }



    private void handConsumerSynMessage(Datagram request,ChannelHandlerContext ctx){
        //收到producer 数据

        String str = BufferCovert.convertByteBufToString(request.getBody());

        JSONObject obj = JSONObject.parseObject(str);
        log.info("receive consumer message "+ JSON.toJSONString(obj));
        String topic= obj.get("subject").toString();
        String groupName= obj.get("groupName").toString();
        //获取message 列表
        List<BaseMessage> messageList = storeNew.getMessages(topic,groupName);

        Datagram datagram  = new Datagram();
        BaseMessage message = new BaseMessage();
        message.setContent(JSON.toJSONString(messageList));
        String dataStr = JSON.toJSONString(message);
        ByteBuf buf = Unpooled.copiedBuffer(dataStr, Charset.forName("UTF-8"));
        datagram.setBody(buf);
        RemotingHeader header = new RemotingHeader();
        header.setRequestCode(MessageTypeEnum.CONSUMER_DATA_INFO.getType());
        header.setLength(dataStr.length());
        header.setMagicCode(RemotingHeader.DEFAULT_MAGIC_CODE);
        datagram.setHeader(header);

        ctx.channel().writeAndFlush(datagram).addListener((new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                if (future.isSuccess()) {
                    log.info("syn consumer data   success");
                    storeNew.deleteMessage(topic,groupName);
                    return;
                }else{
                    log.error("send consumer failed.", future.cause());
                }
            }
        }));;;
        //ChannelHandlerContext ctx =  brokers.get(ip);
        //ctx.writeAndFlush(JSON.toJSONString(messageList));
        //发送socket数据
        log.info("send data to  consumer message");
    }
    private void handOffLine(Datagram request){
        //数据异常
    }



    public class HeartBeat {

        public Timeout connect(String ip){
            log.info("heart beat ");
            timer = new HashedWheelTimer(new MyThreadFactory("heartbeat"));
            timeout =  timer.newTimeout(new TimerTask() {
                public void run(Timeout timeout1) throws Exception {
                    if(!timeout.isCancelled()){
                        log.info("没收borker连接,断开"+ip);
                        log.info("receive consumer message "+ ip);
                        storeNew.deleteBroker(ip);
                        //取消连接业务
                        //删除连接
                    }else{
                    }
                }
            }, 5, TimeUnit.SECONDS);

            //timeouts.putIfAbsent(topic,timeout);
            return timeout;
        }

    }


    public static void main(String[] args) {
        final Timer timer = new HashedWheelTimer();
        timer.newTimeout(new TimerTask() {
            public void run(Timeout timeout) throws Exception {
                log.info("timeout 5");
            }
        }, 5, TimeUnit.SECONDS);
    }
}

