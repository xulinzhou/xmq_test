package com.xmq.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.util
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2019/1/5 11:28
 * @Version: 1.0
 */
@Slf4j

public class MyThreadFactory  implements ThreadFactory{
    final ThreadGroup group;

    private String prefix ;
    final AtomicInteger threadNumber = new AtomicInteger(1);

    public MyThreadFactory(String prefix) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null)? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();

        this.prefix = prefix+"-"+threadNumber.getAndDecrement()+"-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        log.info("new thread");
        Thread t=new Thread(group,r,prefix,0);
        t.setDaemon(true);
        return t;
    }
}
