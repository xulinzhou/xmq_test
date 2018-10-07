package com.xmq.util;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.util
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/10/2 10:25
 * @Version: 1.0
 */
public class FutureTaskExt<V> extends FutureTask<V> {
    private final Runnable runnable;

    public FutureTaskExt(final Callable<V> callable) {
        super(callable);
        this.runnable = null;
    }

    public FutureTaskExt(final Runnable runnable, final V result) {
        super(runnable, result);
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
