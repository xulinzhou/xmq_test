package com.xmq.ha;

import com.xmq.message.BaseMessage;
import com.xmq.message.HaMessage;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.ha
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/11/5 15:18
 * @Version: 1.0
 */
public interface HaMessageSend {

    public  void sendMessage(HaMessage message);
}
