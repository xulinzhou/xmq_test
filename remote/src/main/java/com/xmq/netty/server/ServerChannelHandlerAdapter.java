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
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws  Exception {
        ZKClient client =    new ZKClient( IpUtil.getServerIp()+":2181");
        System.out.println(msg.toString());

        List<Value> msg_new = (List<Value>)msg;
        UserInfo t = MessagePack.unpack(MessagePack.pack(msg_new), UserInfo.class);

        System.out.println(t.getUsername()+"========"+t.getAge());
        LOGGER.info("SimpleServerHandler.channelRead");
        /*List<UserInfo> msgs = (List<UserInfo>) msg;
        for(UserInfo baseMes : msgs){

            //client.addEphemeralNode(Constants.BROKER_ROOT+baseMes.getSubject()+"/"+baseMes.getGroupName()+ IpUtil.getServerIp());
        }*/

        LOGGER.info("msgs"+msg.toString());
    }

}
