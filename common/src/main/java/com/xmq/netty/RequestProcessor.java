package com.xmq.netty;

import io.netty.channel.ChannelHandlerContext;
import java.util.concurrent.CompletableFuture;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.netty
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/12/24 20:21
 * @Version: 1.0
 */
public interface RequestProcessor {

    CompletableFuture<Datagram> processRequest(ChannelHandlerContext ctx, Datagram request);

    boolean rejectRequest();
}
