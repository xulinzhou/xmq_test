package com.xmq.netty.server;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.mysql.cj.x.json.JsonArray;
import com.xmq.handler.QueueHandler;
import com.xmq.message.BaseMessage;
import com.xmq.netty.Datagram;
import com.xmq.netty.RemotingHeader;
import com.xmq.netty.RequestExecutor;
import com.xmq.netty.RequestProcessor;
import com.xmq.util.BufferCovert;
import com.xmq.util.IpUtil;
import com.xmq.util.MessageTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.netty.server
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/16 15:04
 * @Version: 1.0
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class ServerChannelHandlerAdapter  extends SimpleChannelInboundHandler<Datagram> {

    private final Map<Integer, RequestExecutor> commands = Maps.newHashMap();

    private QueueHandler queue;
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerChannelHandlerAdapter.class);

    public ServerChannelHandlerAdapter() {
    }

    public ServerChannelHandlerAdapter(QueueHandler queue) {
        this.queue = queue;
    }

    public void registerProcessor(Integer requestCode, RequestProcessor processor, ExecutorService executor) {
        this.commands.put(requestCode, new RequestExecutor(requestCode, processor, executor));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 客户端连接到服务端后进行
     */
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("客户端连接"+ctx.channel());
    }

    public void channelRead(ChannelHandlerContext ctx, Datagram msg) throws  Exception {

        ByteBuf bf = msg.getBody();

        LOGGER.info("simpleserverhandler channelread"+BufferCovert.convertByteBufToString(bf));
        LOGGER.info("msgs"+JSONUtils.toJSONString(msg));
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Datagram command) throws Exception {
        ByteBuf bf = command.getBody();
        String message =  BufferCovert.convertByteBufToString(bf);
        LOGGER.info("SimpleServerHandler.channelRead byte : "+message);
        LOGGER.info("SimpleServerHandler.type byte : "+command.getHeader().getRequestCode());
       /* RemotingHeader header1 = new RemotingHeader();
        header1.setMagicCode(header1.DEFAULT_MAGIC_CODE);
        ByteBuf buf = Unpooled.copiedBuffer("heartbeat", Charset.forName("UTF-8"));
        header1.setLength("heartbeat".length());
        header1.setRequestCode(100);
        Datagram datagram1 = new Datagram();
        datagram1.setHeader(header1);
        datagram1.setBody(buf);
        ctx.channel().writeAndFlush(datagram1);
        LOGGER.info("error"+ctx.channel());

        command.setTime(System.currentTimeMillis());*/
        if(command.getHeader().getRequestCode() == MessageTypeEnum.CONSUMER_DATA_INFO.getType()){
            System.out.println("1111111111111");
        }


        processMessageReceived(ctx, command);





        //ctx.writeAndFlush(command);
    }
    private void processMessageReceived(ChannelHandlerContext ctx, Datagram cmd) {
        if (cmd != null) {
            final RequestExecutor executor = commands.get(cmd.getHeader().getRequestCode());

            log.info(JSON.toJSONString(commands));
            log.info("===============>"+cmd.getHeader().getRequestCode());

            log.info("executor"+executor);

            executor.execute(ctx,cmd);
        }
    }

}
