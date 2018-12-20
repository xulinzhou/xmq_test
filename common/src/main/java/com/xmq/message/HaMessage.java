package com.xmq.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.message
 * @Description:
 * @Author: xulinzhou
 * @CreateDate: 2018/11/5 15:19
 * @Version: 1.0
 */
@Data
public class HaMessage implements Serializable{
    private int position ;
    private String message;

}
