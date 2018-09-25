package com.xmq.util;

import io.netty.channel.ChannelHandlerContext;

import java.net.*;
import java.util.Enumeration;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.util
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/19 10:38
 * @Version: 1.0
 */
public class IpUtil {

    public static String  getServerIp(){
        String SERVER_IP = null;
        try {
            String hostName = InetAddress.getLocalHost().getHostName();
            SERVER_IP = InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return SERVER_IP;
    }

    public static String  getClientIp(ChannelHandlerContext ctx) throws  Exception{
        String CLIENT_IP = null;
        try {
            InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
             CLIENT_IP = insocket.getAddress().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CLIENT_IP;
    }
}
