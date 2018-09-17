package com.xmq.message;

import java.util.List;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.client
 * @Description: 发送消息类
 * @Author: xulinzhou
 * @CreateDate: 2018/9/17 9:41
 * @Version: 1.0
 */
public interface BrokerMessageService {
     void send(List<BaseMessage> messages);
}
