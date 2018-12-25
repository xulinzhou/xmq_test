package com.xmq.processor;

import com.xmq.netty.Datagram;
import com.xmq.netty.RequestProcessor;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.CompletableFuture;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.processor
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/12/24 20:26
 * @Version: 1.0
 */
public class BrokerProcessor implements RequestProcessor {
    @Override
    public CompletableFuture<Datagram> processRequest(ChannelHandlerContext ctx, Datagram request) {

        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}

