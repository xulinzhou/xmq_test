package com.xmq.producer.client;

import com.alibaba.fastjson.JSON;
import com.xmq.message.BaseMessage;
import com.xmq.message.UserInfo;
import com.xmq.resolver.ZKClient;
import com.xmq.resolver.ZkResolver;
import com.xmq.util.Constants;
import com.xmq.util.IpUtil;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.client
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/16 15:27
 * @Version: 1.0
 */
public class NettyClientHandler  extends ChannelHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientHandler.class);

    private BaseMessage message;

    public   NettyClientHandler(BaseMessage message){
        this.message =message;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("SimpleClientHandler.channelRead");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
    // 连接成功后，向server发送消息
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //ZKClient client =    new ZKClient(IpUtil.getServerIp()+":2181");
        /*client.addPersistentNode(Constants.MQ_ZK_ROOT);
        client.addPersistentNode(Constants.MQ_ZK_ROOT+"/"+message.getSubject());
        client.addEphemeralNode(Constants.MQ_ZK_ROOT+"/"+message.getSubject()+"/"+message.getGroupName());*/
        //BaseMessage user = new BaseMessage("111","333","222");
       // List<BaseMessage> msgs = new ArrayList<>();
        UserInfo user = new UserInfo("1111","33333");
       // msgs.add(user);
        LOGGER.info("====>send message "+JSON.toJSONString(user));
        ctx.write(user);
        ctx.flush();
    }
}
