
package com.xmq.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

import static com.xmq.netty.RemotingHeader.DEFAULT_MAGIC_CODE;

public class DecodeHandler extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        int redinx= in.readableBytes();
        if (redinx < RemotingHeader.LENGTH_FIELD ) return;
        int ind = in.readerIndex();
        int magicCode = in.getInt(ind);
        System.out.println("Integer.toHexString(magicCode)"+Integer.toHexString(magicCode));
        if (DEFAULT_MAGIC_CODE != magicCode) {
            throw new IOException("Illegal Data, MagicCode=" + Integer.toHexString(magicCode));
        }

        in.markReaderIndex();
        int readableBytes = in.readableBytes();
        int total = in.readInt();
        int code = in.readInt();
        int bodyLength = in.readInt();
        if (readableBytes < bodyLength) {
            in.resetReaderIndex();
            return;
        }
        RemotingHeader remotingHeader = new RemotingHeader();
        remotingHeader.setMagicCode(total);
        remotingHeader.setRequestCode(code);
        remotingHeader.setLength(bodyLength);
        Datagram remotingCommand = new Datagram();
        //ByteBuf bodyData = in.readSlice(bodyLength);
        //bodyData.retain();
        //remotingCommand.setBody(bodyData);

        ByteBuf bodyData = Unpooled.buffer(bodyLength, bodyLength);
        in.readBytes(bodyData, bodyLength);
        remotingCommand.setBody(bodyData);

        remotingCommand.setHeader(remotingHeader);
        list.add(remotingCommand);
    }

}
