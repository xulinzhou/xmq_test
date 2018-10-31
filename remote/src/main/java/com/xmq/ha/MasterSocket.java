package com.xmq.ha;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.ha
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/10/6 16:51
 * @Version: 1.0
 */
@Slf4j
public class MasterSocket {
    private int port = 8888;

    //解码buffer
    private Charset cs = Charset.forName("utf-8");

    /*接受数据缓冲区*/
    private static ByteBuffer sBuffer = ByteBuffer.allocate(1024);

    /*发送数据缓冲区*/
    private static ByteBuffer rBuffer = ByteBuffer.allocate(1024);

    /*映射客户端channel */
    private static Map<String, SocketChannel> clientsMap = new ConcurrentHashMap<String, SocketChannel>();

    private MappedByteBuffer mappedByteBuffer;

    private FileChannel fileChannel;

    int i=0;
    private static Selector selector;

    public MasterSocket(int port) {
        this.port = port;
        try {
            init();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(port));
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        log.info("server start on port:" + port);
    }

    /**
     * 服务器端轮询监听，select方法会一直阻塞直到有相关事件发生或超时
     */
    private void listen() {
        while (true) {
            try {
                selector.select();//返回值为本次触发的事件数
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey key : selectionKeys) {
                    handle(key);
                }
                selectionKeys.clear();//清除处理过的事件
            }
            catch (Exception e) {
                e.printStackTrace();
                break;
            }

        }
    }

    /**
     * 处理不同的事件
     */
    private void handle(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel server = null;
        SocketChannel client = null;
        String receiveText = null;
        int count = 0;
        if (selectionKey.isAcceptable()) {
            /*
             * 客户端请求连接事件
             * serversocket为该客户端建立socket连接，将此socket注册READ事件，监听客户端输入
             * READ事件：当客户端发来数据，并已被服务器控制线程正确读取时，触发该事件
             */
            server = (ServerSocketChannel)selectionKey.channel();
            client = server.accept();
            client.configureBlocking(false);
            clientsMap.put(client.getLocalAddress().toString().substring(1)+ i++,client);
            client.register(selector, SelectionKey.OP_READ);
        }
        else if (selectionKey.isReadable()) {
            /*
             * READ事件，收到客户端发送数据，读取数据后继续注册监听客户端
             */
            client = (SocketChannel)selectionKey.channel();
            rBuffer.clear();
            count = client.read(rBuffer);
            if (count > 0) {
                rBuffer.flip();
                receiveText = String.valueOf(cs.decode(rBuffer).array());
                log.info(client.toString() + ":" + receiveText);
                //dispatch(client, receiveText);

                //client = (SocketChannel)selectionKey.channel();
            }
        }
    }

    /**
     * 把当前客户端信息 推送到其他客户端
     */
    private void dispatch(SocketChannel client, String info) throws IOException {
        Socket s = client.socket();
        String name =
                "[" + s.getInetAddress().toString().substring(1) + ":" + info + "]";
        log.info(name);
        log.info(JSONObject.toJSONString(clientsMap));
        if (!clientsMap.isEmpty()) {
            for (Map.Entry<String, SocketChannel> entry : clientsMap.entrySet()) {
                SocketChannel temp = entry.getValue();
                if (!client.equals(temp)) {


                    Long position = Long.parseLong(info);
                    File file = new File("D:\\test1\\test.txt");
                    this.fileChannel = new RandomAccessFile(file, "rw").getChannel();
                    this.mappedByteBuffer = this.fileChannel.map(FileChannel.MapMode.READ_WRITE,position.intValue(), 1024);
                    int intPosition = mappedByteBuffer.getInt(0);
                    //写入position
                    byte[] dst = new byte[intPosition];// 每次读出3M的内容
                    for (int i = 0; i < intPosition; i++) {
                        dst[i] = mappedByteBuffer.get(i);
                    }
                    sBuffer.clear();
                    sBuffer.put(dst);
                    sBuffer.flip();
                    //输出到通道
                    temp.write(sBuffer);
                }
            }
        }
        clientsMap.put(name, client);
    }

    public static void main(String[] args) throws IOException {
        MasterSocket server = new MasterSocket(7777);
        server.listen();
    }

}
