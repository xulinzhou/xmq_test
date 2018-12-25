package com.xmq.netty.server;

import com.alibaba.druid.support.json.JSONUtils;
import com.google.common.collect.Maps;
import com.xmq.handler.QueueHandler;
import com.xmq.message.BaseMessage;
import com.xmq.netty.Datagram;
import com.xmq.netty.RequestProcessor;
import com.xmq.util.IpUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
public class ServerChannelHandlerAdapter  extends SimpleChannelInboundHandler<Datagram> {

    private final Map<Short, RequestProcessor> commands = Maps.newHashMap();

    private QueueHandler queue;
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerChannelHandlerAdapter.class);

    public ServerChannelHandlerAdapter(QueueHandler queue) {
        this.queue = queue;
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

        LOGGER.info("客户端连接");
    }

    public void channelRead(ChannelHandlerContext ctx, Datagram msg) throws  Exception {

        ByteBuf bf = msg.getBody();

        LOGGER.info("SimpleServerHandler.channelRead"+convertByteBufToString(bf));
        LOGGER.info("msgs"+JSONUtils.toJSONString(msg));
    }

    public String convertByteBufToString(ByteBuf buf) {
        String str;
        if (buf.hasArray()) { // 处理堆缓冲区
            str = new String(buf.array(), buf.arrayOffset() + buf.readerIndex(), buf.readableBytes());
        } else { // 处理直接缓冲区以及复合缓冲区
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            str = new String(bytes, 0, buf.readableBytes());
        }
        return str;
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Datagram command) throws Exception {
        ByteBuf bf = command.getBody();
        LOGGER.info("SimpleServerHandler.channelRead"+convertByteBufToString(bf));
        command.setTime(System.currentTimeMillis());
        processMessageReceived(ctx, command);

        ctx.writeAndFlush(command);
    }
    private void processMessageReceived(ChannelHandlerContext ctx, Datagram cmd) {
        if (cmd != null) {
            final RequestProcessor executor = commands.get(cmd.getHeader().getRequestCode());

            //processRequestCommand(ctx, cmd);

        }
    }

    /*private void processRequestCommand(ChannelHandlerContext ctx, Datagram cmd) {

    }*/

}
