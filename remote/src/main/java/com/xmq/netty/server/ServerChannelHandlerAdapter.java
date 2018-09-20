package com.xmq.netty.server;

import com.xmq.message.BaseMessage;
import com.xmq.message.UserInfo;
import com.xmq.resolver.ZKClient;
import com.xmq.resolver.ZkResolver;
import com.xmq.util.Constants;
import com.xmq.util.IpUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.msgpack.MessagePack;
import org.msgpack.type.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.List;

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
public class ServerChannelHandlerAdapter extends ChannelHandlerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerChannelHandlerAdapter.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    /**
     * 客户端连接到服务端后进行
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("客户端连接");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws  Exception {

        LOGGER.info(msg.toString());

        List<Value> msg_new = (List<Value>)msg;
        BaseMessage baseMessage = MessagePack.unpack(MessagePack.pack(msg_new), BaseMessage.class);

        if(null!=baseMessage){

        }
        LOGGER.info("SimpleServerHandler.channelRead");
        LOGGER.info("msgs"+msg.toString());
    }

}
