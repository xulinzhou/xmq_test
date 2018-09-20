package com.xmq.consumer;

import com.alibaba.fastjson.JSON;
import com.xmq.netty.MsgpackDecoder;
import com.xmq.netty.MsgpackEncoder;
import com.xmq.resolver.ZKClient;
import com.xmq.util.Constants;
import com.xmq.util.IpUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.consumer
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/20 21:14
 * @Version: 1.0
 */
public class ConsumerHandler implements MessageConsumer{

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerHandler.class);

    @Override
    public void addListener(String subjectPrefix, String group, MessageListener listener) {
        LOGGER.info("account info");
        {
            LOGGER.info("port:"+10000);
            EventLoopGroup eventGroup = new NioEventLoopGroup();
            try {
                io.netty.bootstrap.Bootstrap bootstrap = new io.netty.bootstrap.Bootstrap();
                bootstrap.group(eventGroup).channel(NioSocketChannel.class);
                bootstrap.option(ChannelOption.TCP_NODELAY, true);
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        //  ch.pipeline().addLast("msgpack decoder",new MsgpackDecoder());
                        //这里设置通过增加包头表示报文长度来避免粘包
                        ch.pipeline().addLast("frameDecoder",new LengthFieldBasedFrameDecoder(1024, 0, 2,0,2));
                        //增加解码器
                        ch.pipeline().addLast("msgpack decoder",new MsgpackDecoder());
                        //这里设置读取报文的包头长度来避免粘包
                        ch.pipeline().addLast("frameEncoder",new LengthFieldPrepender(2));
                        //增加编码器
                        ch.pipeline().addLast("msgpack encoder",new MsgpackEncoder());
                        //ch.pipeline().addLast(new NettyClientHandler(message));
                    }
                });

                ZKClient client =    new ZKClient(IpUtil.getServerIp()+":2181");
               // client.addPersistentNode(Constants.MQ_ZK_ROOT);
                //client.addPersistentNode(Constants.MQ_ZK_ROOT+"/"+message.getSubject());
               // client.addPersistentNode(Constants.MQ_ZK_ROOT+"/"+message.getSubject()+"/"+message.getGroupName());
                String path  = Constants.MQ_ZK_ROOT+"/"+subjectPrefix+"/"+group;


                List<String> paths = client.getChildren(path);
                if(null == paths  && paths.size() == 0 ){
                    LOGGER.error("没有可以提供服务的broker");
                }else{
                    LOGGER.info("server list"+JSON.toJSONString(paths));
                   /* LoadBalance balance  = new RandomLoadBalance();
                    String pa =  balance.select(paths);*/
                    //ChannelFuture f = bootstrap.connect(pa, port).sync();
                   // f.channel().closeFuture().sync();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                eventGroup.shutdownGracefully();
            }
        }
    }
}
