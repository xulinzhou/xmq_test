package com.xmq.server;

import com.xmq.netty.Datagram;
import com.xmq.netty.RemotingHeader;
import com.xmq.producer.client.NettyClient;
import com.xmq.util.IpUtil;
import com.xmq.util.MessageTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.server
 * @Description: 连接meta 服务器，发送 heat beat 消息
 * @Author: xulinzhou
 * @CreateDate: 2018/12/25 14:45
 * @Version: 1.0
 */
@Slf4j
public class BrokerRegister {
    private long timeout = 1000;
    private final String metaServer = "";
    private  NettyClient client;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public BrokerRegister(String ip,int port) {
        client = new NettyClient();
        client.connect();
    }
    public void  start(){
        executor.scheduleAtFixedRate(this::heartbeat ,1, 4, TimeUnit.SECONDS);
    }

    private void heartbeat() {
        log.info("send broker heatbeat ");
        Datagram data = new Datagram();
        RemotingHeader header = new RemotingHeader();
        header.setRequestCode(MessageTypeEnum.SYN_MESSAGE_BROKER.getType());
        String ip = IpUtil.getServerIp();
        ByteBuf buf = Unpooled.copiedBuffer(ip, Charset.forName("UTF-8"));

        header.setLength(ip.length());
        data.setHeader(header);
        data.setBody(buf);
        log.info("send message"+data);
        client.synMessage("127.0.0.1",data,timeout);
    }


    public void online(){
        onlineMessage();
    }


    private void onlineMessage() {
        log.info("online message");
        Datagram data = new Datagram();
        RemotingHeader header = new RemotingHeader();
        header.setRequestCode(MessageTypeEnum.BROKER_ONLINE.getType());
        header.setLength(0);
        data.setHeader(header);
        client.synMessage("127.0.0.1",data,timeout);
    }
}
