package com.xmq.cache;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.cache
 * @Description: 缓存管理
 * @Author: xulinzhou
 * @CreateDate: 2018/12/28 18:40
 * @Version: 1.0
 */
public class CacheManage {

    private static final ScheduledExecutorService SCHEDULE_POOL = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder().setNameFormat("meta-fresh-%d").build());

    public CacheManage() {
        refresh();

    }
    public void refresh(){
        SCHEDULE_POOL.scheduleAtFixedRate(()->refreshTask(), 2, 2, TimeUnit.SECONDS);
    }

    public  void refreshTask(){
        //load 数据到内存
    }
}
