package com.xmq.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.client
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/16 15:27
 * @Version: 1.0
 */
public class NettyClientHandler  extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("SimpleClientHandler.channelRead");
       /* ByteBuf result = (ByteBuf) msg;
        byte[] result1 = new byte[result.readableBytes()];
        result.readBytes(result1);
        System.out.println("Server said:" + new String(result1));
        result.release();*/

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
        /*RemoteData data = new RemoteData();
        Class cls = com.xmq.message.BrokerMessageService.class;
        data.setInterfaceClass(cls);
        data.setMethodName("sendMe");*/
        UserInfo user = new UserInfo("1","22");
        System.out.println("222222222-==========================");
        ctx.write(user);
        ctx.flush();
    }
}
