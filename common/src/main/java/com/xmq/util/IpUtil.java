package com.xmq.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
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
}
