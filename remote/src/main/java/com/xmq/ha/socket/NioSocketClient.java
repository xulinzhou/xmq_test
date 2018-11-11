package com.xmq.ha.socket;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.xmq.ha.HaMessageSend;
import com.xmq.message.HaMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
    private static int port = 8888;
    private static  String PATH = "D:\\test1\\test.txt";
    private static Map<String, SocketChannel> clientsMap = new ConcurrentHashMap<String, SocketChannel>();



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
                log.error("服务端启动异常！");
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
            log.error("连接异常");
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
            e.printStackTrace();
        }
    }

    /**
     * int到byte[]
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
    public void sendMessage(String message) {

        SocketChannel channel = (SocketChannel)clientsMap.get(String.valueOf(port));;
        HaMessage haMessage = new HaMessage();
        haMessage.setPosition(100);
        haMessage.setMessage(message);
        log.info("syn channel info : "+channel);
        String jsonString = JSON.toJSONString(haMessage);
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

    public void File(){

    }
    public static void main(String args[]) throws IOException {
        NioSocketClient client = new NioSocketClient();
        client.initClient();
        client.start();
        try{
            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }

        Charset charset = Charset.defaultCharset();
        CharsetDecoder charsetDecoder = charset.newDecoder();

        File file =new File(PATH);
        FileChannel fc = new RandomAccessFile(file, "rw").getChannel();
        int start= 0;
        MappedByteBuffer mb = fc.map(FileChannel.MapMode.READ_WRITE,start,file.length());

        mb.position(100);
        mb.flip();
        String sendMessage = charsetDecoder.decode(mb).toString();
        log.info("sendMessage=================="+sendMessage);
        HaMessage message = new HaMessage();
        String msg = JSON.toJSONString(sendMessage);
        log.info("msg.length==================="+msg.length());
        message.setPosition(msg.length());
        message.setMessage(msg);
        client.sendMessage(sendMessage);

        /*HaMessage message = new HaMessage();
        message.setPosition(100);
        message.setMessage("message info new");
        client.sendMessage(message);*/
    }
}
