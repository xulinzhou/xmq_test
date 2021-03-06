package com.xmq.store.file;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.store.file
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/23 11:20
 * @Version: 1.0
 */
@Data
@Slf4j
public class FileLog {
    private String queueId;
    private String topic;
    private String messageContent;
    private int offeset;
    private String syn;

}
