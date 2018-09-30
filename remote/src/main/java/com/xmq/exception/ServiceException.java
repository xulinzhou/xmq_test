package com.xmq.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.exception
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/30 17:20
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ServiceException  extends Exception{
    private static final long serialVersionUID = 1L;

    /* 错误码 */
    protected Integer code;

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
