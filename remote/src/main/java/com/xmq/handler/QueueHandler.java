package com.xmq.handler;

import com.xmq.message.BaseMessage;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.handler
 * @Description: 数据放入队列
 * @Author: xulinzhou
 * @CreateDate: 2018/9/21 14:24
 * @Version: 1.0
 */
@Component
public class QueueHandler implements Handler {

    private BlockingQueue<BaseMessage> queue;
    @Resource
    protected AbstractHandler nextHandler ;
    public QueueHandler() {
        queue = new LinkedBlockingQueue<BaseMessage>(10000);
    }

    public void add(BaseMessage message) {
        queue.offer(message);
    }

    //多线程统一处理数据

    public BaseMessage take() {
        try {
            BaseMessage item = this.queue.take();
            return item;
        } catch (InterruptedException e) {
            return null;
        }
    }
    public void setNextHandler(AbstractHandler nextHandler ){
        this.nextHandler  = nextHandler;
    }

    @Override
    public void handleNext(BaseMessage message) {
        nextHandler.handleNext(message);
    }
}
