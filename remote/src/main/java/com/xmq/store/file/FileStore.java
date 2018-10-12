package com.xmq.store.file;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.mysql.cj.x.json.JsonArray;
import com.xmq.config.Config;
import com.xmq.ha.SynFileService;
import com.xmq.message.BaseMessage;
import com.xmq.netty.server.ServerChannelHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import lombok.extern.slf4j.Slf4j;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.store.file
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/21 22:15
 * @Version: 1.0
 */
@Slf4j
public class FileStore {

    protected  AtomicInteger wrotePosition = new AtomicInteger(0);
    private  String fileName;
    private  File file;
    private  int fileSize = 1024;
    private  Long fileFromOffset;
    private  MappedByteBuffer mappedByteBuffer;
    private  Config config;
    private  FileChannel fileChannel;
    private  String  MAGIC = "XMQ";
    private ReentrantLock putMessagelLock = new ReentrantLock(); // 文件锁，只让一个程序写问题

    private void init(final String fileName) throws IOException {
        this.fileSize = fileSize;
        putMessagelLock.lock();
        this.fileName = config.getFilePath()+File.separator+fileName+".txt";
        new File(this.fileName).deleteOnExit();
        if(!new File(this.fileName).exists()){
            new File(this.fileName).createNewFile();
        }
        this.file = new File(this.fileName);

        try {
            log.info("file position:"+wrotePosition.intValue());
            this.fileChannel = new RandomAccessFile(this.file, "rw").getChannel();
            this.mappedByteBuffer = this.fileChannel.map(FileChannel.MapMode.READ_WRITE,wrotePosition.intValue(), fileSize);
        } catch (FileNotFoundException e) {
            log.error("create file channel " + this.fileName + " Failed. ", e);
            throw e;
        } catch (IOException e) {
            log.error("map file " + this.fileName + " Failed. ", e);
            throw e;
        } finally {
            putMessagelLock.unlock();
            mappedByteBuffer.force();
            //new SynFileService(this.file,wrotePosition.intValue());
            if ( this.fileChannel != null) {
                this.fileChannel.close();
            }
        }
    }

    public FileStore(Config config) throws IOException {
        this.config = config;
    }
    /**
     * 写文件
     * @param message
     */
    public void write(BaseMessage message)
    {
        try{
            String str = JSON.toJSONString(message);
            init(message.getSubject());
            log.info("str:{},size:{}",str,str.length());
            int length = str.length();
            mappedByteBuffer.put(MAGIC.getBytes());
            mappedByteBuffer.put(String.valueOf(length).getBytes());
            mappedByteBuffer.put(str.getBytes());
            this.wrotePosition.getAndAdd(str.length()+String.valueOf(length).length()+MAGIC.length());
        }catch (Exception e){
            log.error("写人文件失败",e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 写文件
     * @param messages
     */
    public void write(List<BaseMessage> messages)
    {
        try{

            for(BaseMessage  bm : messages){
                String str = JSON.toJSONString(bm);
                init(bm.getSubject());
                log.info("str:{},size:{}",str,str.length());
                int length = str.length();
                mappedByteBuffer.put(MAGIC.getBytes());
                mappedByteBuffer.put(String.valueOf(length).getBytes());
                mappedByteBuffer.put(str.getBytes());
                this.wrotePosition.getAndAdd(str.length()+String.valueOf(length).length()+MAGIC.length());
                log.info("position"+wrotePosition.toString());
            }

        }catch (Exception e){
            log.error("写人文件失败",e.getMessage());
            e.printStackTrace();
        }

    }
}
