package com.xmq.message;

import java.io.Serializable;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.message
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/17 19:14
 * @Version: 1.0
 */
public class Send implements Serializable {

    /**
     * serialVersionUID:TODO（用一句话描述这个变量表示什么）
     *
     * @since 1.0.0
     */

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String message;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Send [id=" + id + ", name=" + name + ", message=" + message + "]";
    }

}