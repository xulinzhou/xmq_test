
package com.xmq.netty;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class EncodeHandler extends MessageToByteEncoder<Datagram> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Datagram msg, ByteBuf out) throws Exception {

        int start = out.writerIndex();
        out.writerIndex(start);
        final RemotingHeader header = msg.getHeader();
        out.writeInt(header.getMagicCode());
        out.writeInt(header.getRequestCode());
        out.writeInt(header.getLength());
        out.writeBytes(msg.getBody());
        System.out.println("2222222222222222222222222");
    }

}
