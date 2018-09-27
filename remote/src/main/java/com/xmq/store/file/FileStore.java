package com.xmq.store.file;

import com.alibaba.fastjson.JSON;
import com.xmq.message.BaseMessage;
import com.xmq.netty.server.ServerChannelHandlerAdapter;
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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.store.file
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/21 22:15
 * @Version: 1.0
 */
public class FileStore {
    private static final Logger log = LoggerFactory.getLogger(FileStore.class);

    protected  static  AtomicInteger wrotePosition = new AtomicInteger(0);

    private static String fileName;
    private static File file;
    private static int fileSize = 1024;
    private Long fileFromOffset;
    private static  MappedByteBuffer mappedByteBuffer;
    private volatile long storeTimestamp = 0;
    private boolean firstCreateInQueue = false;

    protected FileChannel fileChannel;

    private void init(final String fileName, final int messgeSize) throws IOException {
        this.fileSize = fileSize;

        if(StringUtils.isEmpty(this.fileName)){
            this.fileName = "D:\\test1\\"+fileName+".txt";
            if(!new File(this.fileName).exists()){
                new File(this.fileName).createNewFile();
            }
        }

        if(wrotePosition.intValue() - 1024 > -messgeSize){
            this.fileName =  "D:\\test1\\"+fileName+"_"+new Date().getTime()+".txt";
            if(!new File(this.fileName).exists()){
                new File(this.fileName).createNewFile();
            }

            this.wrotePosition = new AtomicInteger(0);
        }
        this.file = new File(this.fileName);

        try {
            this.fileChannel = new RandomAccessFile(this.file, "rw").getChannel();

            this.mappedByteBuffer = this.fileChannel.map(FileChannel.MapMode.READ_WRITE,wrotePosition.intValue(), fileSize);

        } catch (FileNotFoundException e) {
            log.error("create file channel " + this.fileName + " Failed. ", e);
            throw e;
        } catch (IOException e) {
            log.error("map file " + this.fileName + " Failed. ", e);
            throw e;
        } finally {
            if ( this.fileChannel != null) {
                this.fileChannel.close();
            }
        }
    }
    public FileStore(BaseMessage message) throws IOException {
        init(message.getSubject(), JSON.toJSON(message).toString().length());
    }
    /**
     * 写文件
     * @param str
     */
    public void write(String str)
    {
        log.info("str:{},size:{}",str,str.length());
        mappedByteBuffer.put(str.getBytes());
        this.wrotePosition.addAndGet(str.length());
        log.info("positin"+wrotePosition.toString());
    }


}
