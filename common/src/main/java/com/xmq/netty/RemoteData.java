package com.xmq.netty;

import org.msgpack.annotation.Message;

import java.io.Serializable;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.netty
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/16 16:28
 * @Version: 1.0
 */
@Message
public class RemoteData  implements Serializable {
    /**
     * 接口
     */
    private Class<?> interfaceClass;
    /**
     * 方法名
     */
    private String methodName;


    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
