package com.xmq.exception;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.exception
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2019/1/3 15:38
 * @Version: 1.0
 */
public class TimeOutException extends Exception {

    public TimeOutException() {
    }

    public TimeOutException(String message) {
        super(message);
    }

    public TimeOutException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeOutException(Throwable cause) {
        super(cause);
    }
}
