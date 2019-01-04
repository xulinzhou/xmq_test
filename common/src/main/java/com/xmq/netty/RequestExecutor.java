package com.xmq.netty;

import com.xmq.util.MessageTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.netty
 * @Description: 处理器
 * @Author: xulinzhou
 * @CreateDate: 2018/12/25 10:03
 * @Version: 1.0
 */
public class RequestExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(RequestExecutor.class);

    private final RequestProcessor processor;
    private final ExecutorService executor;

    public RequestExecutor(final Integer requestCode, final RequestProcessor processor, final ExecutorService executor) {
        this.processor = processor;
        this.executor = executor;
    }
    public  void execute(ChannelHandlerContext ctx, Datagram cmd){
        if(null== executor){
           executeRequest(ctx, cmd);
        }
    }

    private void executeRequest(final ChannelHandlerContext ctx, Datagram cmd) {
        try {
            final CompletableFuture<Datagram> future = processor.processRequest(ctx, cmd);


             RemotingHeader header1 = new RemotingHeader();
            header1.setMagicCode(header1.DEFAULT_MAGIC_CODE);
            header1.setLength(0);
            header1.setRequestCode(100);
            Datagram datagram1 = new Datagram();
            datagram1.setHeader(header1);
            ctx.writeAndFlush(datagram1);


            if (future != null) {
                future.exceptionally(ex -> errorResp(MessageTypeEnum.SYN_ERROR.getType(), cmd))
                        .thenAccept((datagram -> {
                            final RemotingHeader header = datagram.getHeader();
                            header.setMagicCode(header.DEFAULT_MAGIC_CODE);
                            header.setLength(0);
                            header.setRequestCode(100);
                            datagram.setHeader(header);
                            ctx.writeAndFlush(datagram);
                        }));
            }

        } catch (Throwable e) {
            LOG.error("excute error channle {},cmd:{}", ctx.channel(),cmd,e.fillInStackTrace());
            ctx.writeAndFlush(errorResp(MessageTypeEnum.SYN_ERROR.getType(), cmd));
        }
    }
    private Datagram errorResp(final int code, final Datagram command) {
        final Datagram datagram = new Datagram();
        final RemotingHeader header = datagram.getHeader();
        header.setRequestCode(code);
        return datagram;
    }
}
