package com.xmq.store.db;

import com.xmq.message.BaseMessage;
import com.xmq.message.Broker;

import java.util.List;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.store
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/21 22:14
 * @Version: 1.0
 */
public interface IDbStoreNew {
    void saveMessage(BaseMessage message);
    void saveBroker(Broker broker);
    List<Broker> getBroker(String topic);
    List<BaseMessage> getMessages(String topic,String groupName);
    void deleteMessage(String topic,String groupName);
    void deleteBroker(String ip);
}
