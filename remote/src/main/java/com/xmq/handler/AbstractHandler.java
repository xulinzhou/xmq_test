package com.xmq.handler;

import com.xmq.message.BaseMessage;
import org.springframework.stereotype.Component;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.handler
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/21 14:23
 * @Version: 1.0
 */

public abstract class AbstractHandler  implements  Handler {
    protected AbstractHandler nextHandler = null;

    public void setNextHandler(AbstractHandler nextHandler ){
        this.nextHandler  = nextHandler;
    }

   public abstract void handleNext(BaseMessage message);
}
