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
 * @Description: 客户端连接
 * @Author: xulinzhou
 * @CreateDate: 2018/12/24 20:24
 * @Version: 1.0
 */
@Slf4j
public class ClientRegisterProcessor implements RequestProcessor {
    @Override
    public CompletableFuture<Datagram> processRequest(ChannelHandlerContext ctx, Datagram request) {
        log.info("client register data"+ JSONUtils.toJSONString(request));
        return null;
    }


}
