package com.xmq.processor;

import com.xmq.netty.Datagram;
import com.xmq.netty.RequestProcessor;
import com.xmq.util.MessageTypeEnum;
import com.xmq.util.MyThreadFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;


import java.util.concurrent.CompletableFuture;
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
    @Override
    public CompletableFuture<Datagram> processRequest(ChannelHandlerContext ctx, Datagram request) {
        //broker 入库
        if(request.getHeader().getRequestCode() == MessageTypeEnum.SYN_MESSAGE_BROKER.getType()){
            return CompletableFuture.completedFuture(handlerHeat(request));
        }else if(request.getHeader().getRequestCode() == MessageTypeEnum.BROKER_ONLINE.getType()){
            handOnline(request);
        }
        return null;
    }
    private Datagram handlerHeat(Datagram request){
         //心跳数据入库
        log.info("heartbeat from broker");
        Timeout timeout = new HeartBeat().connect();
        //timeout.cancel();
        return new Datagram();
    }

    private void handOnline(Datagram request){
         //插入数据库，broker入库

    }

    private void handOffLine(Datagram request){
        //数据异常
    }



    public class HeartBeat {

        public Timeout connect(){
            Timer timer = new HashedWheelTimer(new MyThreadFactory("heartbeat"));
            Timeout timeout =  timer.newTimeout(new TimerTask() {
                public void run(Timeout timeout) throws Exception {
                    log.info("==========================");
                    if(!timeout.isCancelled()){
                        log.error("没收到连接，断开");
                        //取消连接业务
                    }
                }
            }, 1, TimeUnit.SECONDS);
            return timeout;
        }

    }

}

