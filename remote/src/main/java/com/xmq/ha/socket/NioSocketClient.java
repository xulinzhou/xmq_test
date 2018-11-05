package com.xmq.ha.socket;

import com.alibaba.fastjson.JSON;
import com.xmq.ha.HaMessageSend;
import com.xmq.message.HaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.ha.socket
 * @Description: 数据客户端，包含粘包处理
 * @Author: xulinzhou
 * @CreateDate: 2018/11/5 14:35
 * @Version: 1.0
 */
@Slf4j
public class NioSocketClient extends Thread implements HaMessageSend{
    private SocketChannel socketChannel;
    private Selector selector = null;
    private int clientId;
    private int port = 8888;
    private static Map<String, SocketChannel> clientsMap = new ConcurrentHashMap<String, SocketChannel>();

    public static void main(String args[]) throws IOException {
        NioSocketClient client = new NioSocketClient();
        client.initClient();
        client.start();
        try{
              Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }
        HaMessage message = new HaMessage();
        message.setLength(100);
        message.setMessage("111111111111111111111111111");
        client.sendMessage(message);
    }

    public NioSocketClient() {
    }

    public NioSocketClient(int clientId) {
        this.clientId = clientId;
    }

    public void initClient() throws IOException {
        log.info("start server port:"+port);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
        selector = Selector.open();
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(inetSocketAddress);
        synchronized (selector) {
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
    }

    public void run() {
        while (true) {
            try {
                int key = selector.select();
                if (key > 0) {
                    Set<SelectionKey> keySet = selector.selectedKeys();
                    Iterator<SelectionKey> iter = keySet.iterator();
                    while (iter.hasNext()) {
                        SelectionKey selectionKey = null;
                        synchronized (iter) {
                            selectionKey = iter.next();
                            iter.remove();
                        }
                        if (selectionKey.isConnectable()) {
                            finishConnect(selectionKey);
                        }
                        if (selectionKey.isWritable()) {
                            send(selectionKey);
                        }
                        if (selectionKey.isReadable()) {
                            read(selectionKey);
                        }
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void finishConnect(SelectionKey key) {
        log.info("client finish connect!");
        SocketChannel socketChannel = (SocketChannel) key.channel();
        clientsMap .put(String.valueOf(port),socketChannel);
        try {
            socketChannel.finishConnect();
            synchronized (selector) {
                socketChannel.register(selector, SelectionKey.OP_WRITE);
                key.interestOps(SelectionKey.OP_WRITE);

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        int len = channel.read(byteBuffer);
        if (len > 0) {
            byteBuffer.flip();
            byte[] byteArray = new byte[byteBuffer.limit()];
            byteBuffer.get(byteArray);
            log.info("client[" + clientId + "]" + "receive from server:");
            log.info(new String(byteArray));
            len = channel.read(byteBuffer);
            byteBuffer.clear();

        }
        key.interestOps(SelectionKey.OP_READ);
    }

    public void send(SelectionKey key) throws IOException{
        SocketChannel channel = (SocketChannel) key.channel();
        log.info("channel"+channel.toString());

        /*for (int i = 0; i < 10; i++) {
            String ss = i + "Server ,how are you? this is package message from NioSocketClient!";
            int head = (ss).getBytes().length;
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 + head);
            byteBuffer.put(intToBytes(head));
            byteBuffer.put(ss.getBytes());
            byteBuffer.flip();
            log.info("[client] send:" + i + "-- " + head + ss);
            while (byteBuffer.hasRemaining()) {
                try {

                    channel.write(byteBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/

        try {
            synchronized (selector) {
                channel.register(selector, SelectionKey.OP_READ);
            }
        } catch (ClosedChannelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * int到byte[]
     *
     * @param
     * @return
     */
    public static byte[] intToBytes(int value) {
        byte[] result = new byte[4];
        // 由高位到低位
        result[0] = (byte) ((value >> 24) & 0xFF);
        result[1] = (byte) ((value >> 16) & 0xFF);
        result[2] = (byte) ((value >> 8) & 0xFF);
        result[3] = (byte) (value & 0xFF);
        return result;
    }

    @Override
    public void sendMessage(HaMessage message) {

        SocketChannel channel = (SocketChannel)clientsMap.get(String.valueOf(port));;

        log.info("channel============="+channel);
        String jsonString = JSON.toJSONString(message);
        int head = (jsonString).getBytes().length;
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + head);
        byteBuffer.put(intToBytes(head));
        byteBuffer.put(jsonString.getBytes());
        byteBuffer.flip();
        log.info("[client] send message : "+ jsonString);
        while (byteBuffer.hasRemaining()) {
            try {
                channel.write(byteBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
