package com.xmq.store.db;

import com.xmq.message.BaseMessage;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.store
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/21 22:14
 * @Version: 1.0
 */
public interface IDbStore {
    void save(BaseMessage message);
}
