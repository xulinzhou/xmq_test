package com.xmq.ha.socket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xmq.message.HaMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.ha.socket
 * @Description: 数据接收，包含粘包处理
 * @Author: xulinzhou
 * @CreateDate: 2018/11/5 14:36
 * @Version: 1.0
 */
@Slf4j
public class NioSocketServer extends Thread {
        ServerSocketChannel serverSocketChannel = null;
        Selector selector = null;
        SelectionKey selectionKey = null;
        private int port = 8888;
        boolean cache = false;
        //备份数据包
        private static String PATH = "D:\\test1\\test_bak.txt";
        int i=0;
        private static Map<String, SocketChannel> clientsMap = new ConcurrentHashMap<String, SocketChannel>();
        public void initServer() throws IOException {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            log.info("start server port:"+port);
            selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        }

        public void run() {
            while (true) {
                try {
                    int selectKey = selector.select();
                    if (selectKey > 0) {
                        Set<SelectionKey> keySet = selector.selectedKeys();
                        Iterator<SelectionKey> iter = keySet.iterator();
                        while (iter.hasNext()) {
                            SelectionKey selectionKey = iter.next();
                            iter.remove();
                            if (selectionKey.isAcceptable()) {
                                accept(selectionKey);
                            }
                            if (selectionKey.isReadable()) {
                                read(selectionKey);
                            }
                            if (selectionKey.isWritable()) {
                                // write(selectionKey);
                                System.out.println();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        serverSocketChannel.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        }

        public void accept(SelectionKey key) {
            try {
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                SocketChannel socketChannel = serverSocketChannel.accept();
                log.info("socket is acceptable");
                socketChannel.configureBlocking(false);
                clientsMap.put(socketChannel.getLocalAddress().toString().substring(1)+ i++,socketChannel);
                socketChannel.register(selector, SelectionKey.OP_READ);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void read(SelectionKey selectionKey) {
            log.info("read事件");
            int head_length = 4;
            byte[] headByte = new byte[4];

            try {
                SocketChannel channel = (SocketChannel) selectionKey.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1000);
                int bodyLen = -1;

                channel.read(byteBuffer);//
                byteBuffer.flip();//
                while (byteBuffer.remaining() > 0) {
                    if (bodyLen == -1) {
                        if (byteBuffer.remaining() >= head_length) {
                            byteBuffer.mark();
                            byteBuffer.get(headByte);
                            bodyLen = byteArrayToInt(headByte);
                        }
                    } else {
                        if (byteBuffer.remaining() >= bodyLen) {
                            File file =new File(PATH);
                            FileChannel fc = new RandomAccessFile(file, "rw").getChannel();
                            int start= 0;
                            MappedByteBuffer mb = fc.map(FileChannel.MapMode.READ_WRITE,start,bodyLen);
                            byte[] bodyByte = new byte[bodyLen];
                            byteBuffer.get(bodyByte, 0, bodyLen);
                            bodyLen = -1;
                            String str = new String(bodyByte);
                            HaMessage ha  = JSON.parseObject(str,HaMessage.class);
                            mb.put(ha.getMessage().getBytes());
                            log.info("receive from clien content is:" + ha.getMessage());
                        }
                    }
                }

                selectionKey.interestOps(SelectionKey.OP_READ);
            } catch (IOException e) {
                try {
                    serverSocketChannel.close();
                    selectionKey.cancel();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }

        }

        public void write(SelectionKey selectionKey) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            String httpResponse = "success syn";
            log.info("response from server to client");
            try {
                ByteBuffer byteBuffer = ByteBuffer.wrap(httpResponse.getBytes());
                while (byteBuffer.hasRemaining()) {
                    socketChannel.write(byteBuffer);
                }
                selectionKey.cancel();
            } catch (IOException e) {
                try {
                    selectionKey.cancel();
                    serverSocketChannel.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }

        /**
         * byte[]转int
         *
         * @param bytes
         * @return
         */
        public static int byteArrayToInt(byte[] bytes) {
            int value = 0;
            // 由高位到低位
            for (int i = 0; i < 4; i++) {
                int shift = (4 - 1 - i) * 8;
                value += (bytes[i] & 0x000000FF) << shift;// 往高位游
            }
            return value;
        }

        /**
         * 启动服务端
         * @throws Exception
         */
        public void startServer() throws Exception {
            NioSocketServer server = new NioSocketServer();
            server.initServer();
            server.start();
        }

        public static void main(String args[]) throws IOException {
            NioSocketServer server = new NioSocketServer();
            server.initServer();
            server.start();
        }

}
