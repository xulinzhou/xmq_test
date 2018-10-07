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
@Component
public class DbHandler extends AbstractHandler {



    private IDbStore dbStore;

    public DbHandler(IDbStore dbStore) {
        this.dbStore = dbStore;
    }

    @Override
    public void handleNext(BaseMessage message) {
        dbStore.save(message);
        this.nextHandler.handleNext(message);
    }
}
