package com.xmq.dispatch;

import com.alibaba.fastjson.JSON;
import com.xmq.handler.QueueHandler;
import com.xmq.loadbalance.LoadBalance;
import com.xmq.loadbalance.RandomLoadBalance;
import com.xmq.message.BaseMessage;
import com.xmq.netty.MsgpackDecoder;
import com.xmq.netty.MsgpackEncoder;
import com.xmq.netty.server.NettyServer;
import com.xmq.netty.server.ServerChannelHandlerAdapter;
import com.xmq.producer.client.NettyClientHandler;
import com.xmq.resolver.ZKClient;
import com.xmq.util.Constants;
import com.xmq.util.IpUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ProjectName: xmq
 * @Package: com.xmq
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/24 20:49
 * @Version: 1.0
 */
public class DispatchThread extends Thread {
    private Logger log = LoggerFactory.getLogger(DispatchThread.class);

    @Resource
    private QueueHandler queueHandler;
    @Value("${broker.path}")
    private String path;
    private volatile boolean isRunning = true;
    private int port = 10000;
    public DispatchThread( QueueHandler queueHandler) {
        super.setDaemon(true);
        this.queueHandler = queueHandler;
    }


    public void run() {
        while (isRunning) {
            try {
                BaseMessage message = queueHandler.take();
                log.info("take message ,{}"+JSON.toJSONString(message));

                //
                dispatch(message);
                queueHandler.handleNext(message);

            } catch (Exception e) {
                log.error("Process message queue failed", e);
            }
        }
    }
    private void dispatch(final  BaseMessage message){
        log.info("netty服务启动: [port: {}]",port);
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            io.netty.bootstrap.Bootstrap bootstrap = new io.netty.bootstrap.Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1024, 0, 2,0,2));
                    ch.pipeline().addLast("msgpack decoder",new MsgpackDecoder());
                    ch.pipeline().addLast("frameEncoder",new LengthFieldPrepender(2));
                    ch.pipeline().addLast("msgpack encoder",new MsgpackEncoder());
                    ch.pipeline().addLast(new SendMessageHandler(message));
                }
            });

            ZKClient client =    new ZKClient(IpUtil.getServerIp()+":2181");
            String path  = Constants.MQ_ZK_ROOT+"/"+message.getSubject()+"/"+message.getGroupName()+"/consumer";
            List<String> paths = client.getChildren(path);
            if(null == paths  && paths.size() == 0 ){
                log.error("没有可以提供服务的broker");
            }else{
                log.info("server list"+JSON.toJSONString(paths));
                LoadBalance balance  = new RandomLoadBalance();
                String pa =  balance.select(paths);
                ChannelFuture f = bootstrap.connect(pa, port).sync();
                f.channel().closeFuture().sync();
            }

        } catch (Exception e) {
            log.error("netty服务启动异常-" + e.getMessage());
        } finally {
            group.shutdownGracefully();
        }
    }

    public void pause() {
        isRunning = false;
    }


}
