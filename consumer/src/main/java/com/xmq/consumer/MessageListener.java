package com.xmq.consumer;

import com.xmq.message.BaseMessage;

/**
 * @ProjectName: xmq
 * @Package: com.xmq
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/20 21:12
 * @Version: 1.0
 */
public interface MessageListener {
    void onMessage(BaseMessage msg);
}
