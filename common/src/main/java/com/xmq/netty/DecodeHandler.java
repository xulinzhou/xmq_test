
package com.xmq.netty;

import com.xmq.util.BufferCovert;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

import static com.xmq.netty.RemotingHeader.DEFAULT_MAGIC_CODE;

public class DecodeHandler extends ByteToMessageDecoder {
private static int i = 0;
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        int redinx= in.readableBytes();
        if (redinx < RemotingHeader.LENGTH_FIELD ) return;
        System.out.println("===========================+readableBytes"+redinx);
        System.out.println("-------------------------------------"+i++);

        int ind = in.readerIndex();



        System.out.println("===========================+readerIndex"+ind);

        int magicCode = in.getInt(ind);

        System.out.println("===========================+magicCode"+magicCode);

        System.out.println("Integer.toHexString(magicCode)"+Integer.toHexString(magicCode));
        if (DEFAULT_MAGIC_CODE != magicCode) {
            throw new IOException("Illegal Data, MagicCode=" + Integer.toHexString(magicCode));
        }

        in.markReaderIndex();
        int readableBytes = in.readableBytes();


        System.out.println("===========================+readableBytes"+readableBytes);
        int total = in.readInt();
        int code = in.readInt();
        int bodyLength = in.readInt();
        if (readableBytes <= RemotingHeader.LENGTH_FIELD) {
            in.resetReaderIndex();
            return;
        }

        System.out.println("===========================+bodyLength"+bodyLength);

        ByteBuf bodyData = in.readSlice(bodyLength);
        bodyData.retain();

        RemotingHeader remotingHeader = new RemotingHeader();
        remotingHeader.setMagicCode(total);
        remotingHeader.setRequestCode(code);
        remotingHeader.setLength(bodyLength);
        Datagram remotingCommand = new Datagram();
        remotingCommand.setBody(bodyData);

        System.out.println("daa"+ BufferCovert.convertByteBufToString(bodyData));

        remotingCommand.setHeader(remotingHeader);
        list.add(remotingCommand);
        System.out.println("readerIndex"+in.readerIndex());
    }

}
