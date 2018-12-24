package com.xmq.future;

import com.google.common.util.concurrent.AbstractFuture;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.future
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/12/23 21:20
 * @Version: 1.0
 */
public class AbstractFutureImpl<T> extends AbstractFuture<T> {
    public boolean set(T value) {
        return super.set(value);
    }

    // 创建线程池
    final static ExecutorService executors = Executors.newCachedThreadPool();

    public static void main(String[] args) throws InterruptedException {
        final AbstractFutureImpl<String> future = new AbstractFutureImpl();
        //监听future设置了值
        future.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("进入回调函数");
                    TimeUnit.SECONDS.sleep(4);
                    System.out.println("收到set的值: " + future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, executors);
        TimeUnit.SECONDS.sleep(5);
        future.set("hh");
        System.out.println("set完值");
    }

}
