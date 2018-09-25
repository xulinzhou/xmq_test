package com.xmq.store.file;

import com.xmq.message.BaseMessage;
import com.xmq.netty.server.ServerChannelHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
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

    protected final static  AtomicInteger wrotePosition = new AtomicInteger(0);

    private String fileName;
    private File file;
    private int fileSize;
    private Long fileFromOffset;
    private static  MappedByteBuffer mappedByteBuffer;
    private volatile long storeTimestamp = 0;
    private boolean firstCreateInQueue = false;


    protected FileChannel fileChannel;
    private void init(final String fileName, final int fileSize) throws IOException {
        this.fileName = "D:\\test1\\"+fileName+".txt";
        this.fileSize = fileSize;
        if(!new File(this.fileName).exists()){
            new File(this.fileName).createNewFile();
        }
        this.file = new File(this.fileName);
        this.fileFromOffset =  this.file.length();

        try {
            this.fileChannel = new RandomAccessFile(this.file, "rw").getChannel();

            this.mappedByteBuffer = this.fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);

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
        init(message.getSubject(),1024*1024);
    }
    /**
     * 写
     * @param str
     */
    public void write(String str)
    {
        int currentPos = this.wrotePosition.get();
        int position = mappedByteBuffer.limit() - str.length();
        mappedByteBuffer.position(position);
        mappedByteBuffer.put(str.getBytes());
        mappedByteBuffer.force();
        mappedByteBuffer.flip();
        this.wrotePosition.addAndGet(str.length());

        log.info("positin"+wrotePosition.toString());
    }
    /**
     * 读文件
     */
    public void getAll()
    {
        System.out.println("capacity:"+mappedByteBuffer.capacity());
        for(int i=0;i<mappedByteBuffer.capacity();i++)
        {
            char c=(char)mappedByteBuffer.get(i);
            if(c!=' '&&c!=0)
                System.out.print(c);
        }
        System.out.println();
    }

}
