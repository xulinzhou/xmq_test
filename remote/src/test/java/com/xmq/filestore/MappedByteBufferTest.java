package com.xmq.filestore;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.filestore
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/10/5 17:37
 * @Version: 1.0
 */
public class MappedByteBufferTest {
    private static int count = 2024; // 10 MB


    public static void main(String[] args) throws Exception {


        RandomAccessFile memoryMappedFile = new RandomAccessFile("D:\\test1\\largeFile.txt", "rw");


        // Mapping a file into memory
        MappedByteBuffer out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, count);

        out.putInt(2342);
        System.out.println("+++++" + out.getInt(0));


        // Writing into Memory Mapped File
        for (int i = 0; i < count-1024; i++) {
            out.put((byte) 'A');
        }
        System.out.println("+++++" + out.getInt(0));
        System.out.println("Writing to Memory Mapped File is completed");

        out.force();

        // reading from memory file in Java

        /*for (int i = 0; i < 8; i++) {
            System.out.print( (long)out.get(i));
        }
        System.out.println("Reading from Memory Mapped File is completed");*/


        memoryMappedFile.close();
    }
}
