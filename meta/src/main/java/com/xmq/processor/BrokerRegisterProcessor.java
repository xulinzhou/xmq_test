package com.xmq.processor;

import com.alibaba.druid.support.json.JSONUtils;
import com.xmq.netty.Datagram;
import com.xmq.netty.RequestProcessor;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.processor
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/12/24 20:26
 * @Version: 1.0
 */
@Slf4j
public class BrokerRegisterProcessor implements RequestProcessor {
    @Override
    public CompletableFuture<Datagram> processRequest(ChannelHandlerContext ctx, Datagram request) {
        log.info("request data"+ JSONUtils.toJSONString(request));
        //borkder 入库

        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}

