package com.xmq.dispatch;

import com.alibaba.fastjson.JSON;
import com.xmq.handler.QueueHandler;
import com.xmq.message.BaseMessage;
import com.xmq.netty.server.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ProjectName: xmq
 * @Package: com.xmq
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/24 20:49
 * @Version: 1.0
 */
public class DispatchThread extends Thread {
    private Logger log = LoggerFactory.getLogger(DispatchThread.class);

    private QueueHandler queueHandler;

    private volatile boolean isRunning = true;

    public DispatchThread( QueueHandler queueHandler) {
        super.setDaemon(true);
        this.queueHandler = queueHandler;
    }


    public void run() {
        while (isRunning) {
            try {
                BaseMessage message = queueHandler.take();
                log.info("take message ,{}"+JSON.toJSONString(message));
                queueHandler.handleNext(message);

            } catch (Exception e) {
                log.error("Process message queue failed", e);
            }
        }
    }

    public void pause() {
        isRunning = false;
    }


}
