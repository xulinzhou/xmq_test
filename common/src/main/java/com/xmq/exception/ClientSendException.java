package com.xmq.exception;

/**
 * @ProjectName: xmq
 * @Package: com.xmq
 * @Description: 客户端发送异常
 * @Author: xulinzhou
 * @CreateDate: 2019/1/3 15:27
 * @Version: 1.0
 */
public class ClientSendException extends Exception {

    public ClientSendException() {
    }

    public ClientSendException(String message) {
        super(message);
    }

    public ClientSendException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClientSendException(Throwable cause) {
        super(cause);
    }

}
