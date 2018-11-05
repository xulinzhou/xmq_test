package com.xmq.ha;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.ha
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/10/6 16:53
 * @Version: 1.0
 */
@Slf4j
public class SlaveSocket {
    /*发送数据缓冲区*/
    private static ByteBuffer sBuffer = ByteBuffer.allocate(1024);

    /*接受数据缓冲区*/
    private static ByteBuffer rBuffer = ByteBuffer.allocate(1024);

    /*服务器端地址*/
    private InetSocketAddress SERVER;

    private static Selector selector;

    private static SocketChannel client;

    private static String receiveText;

    private static String sendText;

    private static int count = 0;

    private MappedByteBuffer mappedByteBuffer;

    private FileChannel fileChannel;

    private String path = "D:\\test1\\test_bak.txt";
    public SlaveSocket(int port) {
        SERVER = new InetSocketAddress("localhost", port);
        init();
    }

    public void init() {
        try {

            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            socketChannel.connect(SERVER);

            while (true) {
                selector.select();
                Set<SelectionKey> keySet = selector.selectedKeys();
                for (final SelectionKey key : keySet) {
                    handle(key);
                };
                keySet.clear();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void handle(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isConnectable()) {

            client = (SocketChannel)selectionKey.channel();
            if (client.isConnectionPending()) {
                client.finishConnect();
                log.info("connect success !");
                sBuffer.clear();
                sBuffer.put((new Date() + " connected!").getBytes());
                sBuffer.flip();
                client.write(sBuffer);//发送信息至服务器

                new Thread() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                sBuffer.clear();
                                long position  = file();
                                log.info("sendText position " +position);
                                SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss", java.util.Locale.US);
                                sBuffer.put((String.valueOf(position)).getBytes("UTF-8"));
                                sBuffer.flip();
                                client.write(sBuffer);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                break;
                            }
                        }
                    };
                }.start();
            }
            //注册读事件
            client.register(selector, SelectionKey.OP_READ);
        }
        else if (selectionKey.isReadable()) {

            client = (SocketChannel)selectionKey.channel();
            rBuffer.clear();
            count = client.read(rBuffer);
            if (count > 0) {
                receiveText = new String(rBuffer.array(), 0, count);
                log.info("receiveText:"+receiveText);
                File file = new File(path);
                fileChannel = new RandomAccessFile(file, "rw").getChannel();
                mappedByteBuffer =  fileChannel.map(FileChannel.MapMode.READ_WRITE,0, 1024);
                mappedByteBuffer.put(rBuffer);
                client = (SocketChannel)selectionKey.channel();
                client.register(selector, SelectionKey.OP_READ);
            }
        }
    }
    public long file() throws  Exception{
        File file = new File(path);
        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        long position = fileChannel.position();
        return position;
    }
    public static void main(String[] args) throws Exception {
        SlaveSocket client = new SlaveSocket(7777);
        client.file();

    }
}
