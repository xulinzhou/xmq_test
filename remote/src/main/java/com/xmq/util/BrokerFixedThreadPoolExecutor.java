package com.xmq.util;

import java.util.concurrent.*;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.util
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/10/2 10:24
 * @Version: 1.0
 */
public class BrokerFixedThreadPoolExecutor extends ThreadPoolExecutor {
    public BrokerFixedThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime,
                                         final TimeUnit unit,
                                         final BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public BrokerFixedThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime,
                                         final TimeUnit unit,
                                         final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public BrokerFixedThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime,
                                         final TimeUnit unit,
                                         final BlockingQueue<Runnable> workQueue, final RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public BrokerFixedThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime,
                                         final TimeUnit unit,
                                         final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory,
                                         final RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable, final T value) {
        return new FutureTaskExt<T>(runnable, value);
    }
}
