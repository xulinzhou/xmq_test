package com.xmq.server;

import com.xmq.netty.Datagram;
import com.xmq.netty.RemotingHeader;
import com.xmq.producer.client.NettyClient;
import com.xmq.util.MessageTypeEnum;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
public class BrokerRegister {
    private static final Logger LOG = LoggerFactory.getLogger(BrokerRegister.class);
    private final String metaServer = "";
    private  NettyClient client;
    private final ScheduledExecutorService heartbeatExecute = Executors.newSingleThreadScheduledExecutor();;

    public BrokerRegister(String ip,int port) {
        client = new NettyClient();
        client.connect();


    }
    public void  start(){
        heartbeatExecute.scheduleAtFixedRate(()->heartbeat() ,0, 1, TimeUnit.SECONDS);
        heartbeat();
    }

    public void online(){
        onlineMessage();
    }
    private void heartbeat() {
        LOG.info("send message");
        Datagram data = new Datagram();
        RemotingHeader header = new RemotingHeader();
        header.setRequestCode(MessageTypeEnum.SYN_MESSAGE_BROKER.getType());
        header.setLength(0);
        data.setHeader(header);
        ByteBuf buf = Unpooled.copiedBuffer("heartbeat", Charset.forName("UTF-8"));
        data.setBody(buf);
        client.synMessage("127.0.0.1",data);
    }


    private void onlineMessage() {
        LOG.info("online message");
        Datagram data = new Datagram();
        RemotingHeader header = new RemotingHeader();
        header.setRequestCode(MessageTypeEnum.BROKER_ONLINE.getType());
        header.setLength(0);
        data.setHeader(header);
        client.synMessage("127.0.0.1",data);
    }
}
