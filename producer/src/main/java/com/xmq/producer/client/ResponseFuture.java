package com.xmq.producer.client;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.producer.client
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2019/1/4 9:49
 * @Version: 1.0
 */
@Data
@Slf4j
public class ResponseFuture {


    public boolean ok;

    private long timeout;

    private Callback callBack;

    private final AtomicBoolean executeCallbackOnce = new AtomicBoolean(false);


    public ResponseFuture(long timeout, Callback callBack) {
        this.timeout = timeout;
        this.callBack = callBack;
    }
    public  void executeCallBack(){
        if(callBack == null) return ;
         if(executeCallbackOnce.compareAndSet(false,true)){
             callBack.processResponse(this);
         }
    }
    public interface Callback {
        void processResponse(ResponseFuture responseFuture);
    }

}
