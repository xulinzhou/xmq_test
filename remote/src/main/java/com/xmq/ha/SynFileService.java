package com.xmq.ha;

import com.alibaba.fastjson.JSON;
import com.xmq.netty.MsgpackDecoder;
import com.xmq.netty.MsgpackEncoder;
import com.xmq.resolver.ZKClient;
import com.xmq.util.Constants;
import com.xmq.util.IpUtil;
import com.xmq.util.ServiceThread;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.ha
 * @Description: 文件同步
 * @Author: xulinzhou
 * @CreateDate: 2018/10/2 13:40
 * @Version: 1.0
 */
@Slf4j
public class SynFileService {
    private  String fileName;
    private  File file;
    private  int file_offset;
    private  int port  = 1001;
    private MappedByteBuffer mappedByteBuffer;
    private ReentrantLock putMessagelLock = new ReentrantLock(); // 文件锁，只让一个程序写问题

    public SynFileService(File file, int file_offset) {
        this.file = file;
        this.file_offset = file_offset;
        new HAClient(file,file_offset).start();
    }
    class HAClient extends ServiceThread {
        private  File file;
        private  int file_offset;
        private FileChannel file_bak;
        public HAClient( File file,int file_offset) {
            this.file = file;
            this.file_offset = file_offset;
        }

        @Override
        public String getServiceName() {
            return null;
        }

        @Override
        public void run() {
            log.info("syn file start");
            putMessagelLock.lock();
            try {
                FileChannel   fileChannel = new RandomAccessFile(this.file, "rw").getChannel();
                MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY,file_offset, 1024);
                int count = mappedByteBuffer.getInt();
                byte bytes =  mappedByteBuffer.get(file_offset+count);
                send(bytes); //发送文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                putMessagelLock.unlock();
            }
        }

        public void send(Byte bytes){
            {
                log.info("port:"+port);
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }
            }
        }
    }
}
