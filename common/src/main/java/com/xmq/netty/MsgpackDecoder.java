package com.xmq.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.test
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/17 19:41
 * @Version: 1.0
 */
public class MsgpackDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        // TODO Auto-generated method stub
        System.out.println("msg=======================2222222222222222222222222");
        final int length = msg.readableBytes();
        byte[] b = new byte[length];
        msg.getBytes(msg.readerIndex(), b,0,length);
        MessagePack msgpack = new MessagePack();
        out.add(msgpack.read(b));
    }

}

