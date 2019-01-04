package com.xmq.exception;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.exception
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2019/1/3 15:30
 * @Version: 1.0
 */
public class RemoteException extends Exception {
    /**
     * 错误编码
    */
    private String errorCode;

    public RemoteException() {
    }

    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteException(Throwable cause) {
        super(cause);
    }
}
