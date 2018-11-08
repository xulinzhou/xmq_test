package com.xmq.ha.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
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
        // 缓存一个read事件中一个不完整的包，以待下次read事件到来时拼接成完整的包
        ByteBuffer cacheBuffer = ByteBuffer.allocate(100);
        private int port = 8888;
        boolean cache = false;
        int i=0;
        /*映射客户端channel */
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
                ByteBuffer byteBuffer = ByteBuffer.allocate(100);
                int bodyLen = -1;
                if (cache) {
                    cacheBuffer.flip();
                    byteBuffer.put(cacheBuffer);
                }
                channel.read(byteBuffer);//
                byteBuffer.flip();//
                while (byteBuffer.remaining() > 0) {
                    if (bodyLen == -1) {
                        if (byteBuffer.remaining() >= head_length) {// 可以读出包头，否则缓存
                            byteBuffer.mark();
                            byteBuffer.get(headByte);
                            bodyLen = byteArrayToInt(headByte);
                        } else {
                            byteBuffer.reset();
                            cache = true;
                            cacheBuffer.clear();
                            cacheBuffer.put(byteBuffer);
                            break;
                        }
                    } else {
                        if (byteBuffer.remaining() >= bodyLen) {
                            byte[] bodyByte = new byte[bodyLen];
                            byteBuffer.get(bodyByte, 0, bodyLen);
                            bodyLen = -1;
                            log.info("receive from clien content is:" + new String(bodyByte));
                        } else {
                            byteBuffer.reset();
                            cacheBuffer.clear();
                            cacheBuffer.put(byteBuffer);
                            cache = true;
                            break;
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
