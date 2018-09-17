package com.xmq.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.test
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/17 19:40
 * @Version: 1.0
 */
public class MsgpackEncoder extends MessageToByteEncoder {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // TODO Auto-generated method stub
        MessagePack msgpack = new MessagePack();
        out.writeBytes(msgpack.write(msg));
    }

}


