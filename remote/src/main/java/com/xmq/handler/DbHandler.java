package com.xmq.handler;

import com.xmq.message.BaseMessage;
import com.xmq.store.db.IDbStore;
import com.xmq.store.db.impl.DbStore;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.handler
 * @Description: 保存数据
 * @Author: xulinzhou
 * @CreateDate: 2018/9/21 14:23
 * @Version: 1.0
 */
public class DbHandler extends AbstractHandler {



    @Override
    public void handleNext(BaseMessage message) {
        DbStore store = new DbStore();
        //store.save(message);
        this.nextHandler.handleNext(message);
    }
}
