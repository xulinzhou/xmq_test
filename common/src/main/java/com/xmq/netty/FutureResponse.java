package com.xmq.netty;

import com.google.common.util.concurrent.AbstractFuture;
import org.springframework.remoting.RemoteTimeoutException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.netty
 * @Description: 异步回调
 * @Author: xulinzhou
 * @CreateDate: 2018/12/24 11:17
 * @Version: 1.0
 */
public class FutureResponse extends AbstractFuture<Datagram> {
    final static ExecutorService executors = Executors.newCachedThreadPool();

}
