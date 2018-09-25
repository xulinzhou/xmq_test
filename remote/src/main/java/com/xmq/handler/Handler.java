package com.xmq.handler;

import com.sun.xml.internal.rngom.parse.host.Base;
import com.xmq.message.BaseMessage;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.handler
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/24 19:54
 * @Version: 1.0
 */
public interface Handler {
    void handleNext(BaseMessage message);
}
