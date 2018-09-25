package com.xmq.store.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.store.file
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/24 18:02
 * @Version: 1.0
 */
public class IndexLog {

    private static final Logger log = LoggerFactory.getLogger(FileStore.class);


    private String fileName;
    private File file;
    private int fileSize;
    private Long fileFromOffset;
    private MappedByteBuffer mappedByteBuffer;
    private volatile long storeTimestamp = 0;
    private boolean firstCreateInQueue = false;


    protected FileChannel fileChannel;
    private void init(final String fileName, final int fileSize) throws IOException {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.file = new File(fileName);
        this.fileFromOffset = Long.parseLong(this.file.getName());

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
    public IndexLog(final String fileName, final int fileSize) throws IOException {
        init(fileName, fileSize);
    }
    /**
     * 写
     * @param str
     */
    public void write(String str)
    {
        mappedByteBuffer.put(str.getBytes());
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
