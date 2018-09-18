package com.xmq.message;

import org.msgpack.annotation.Message;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.message
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/18 11:15
 * @Version: 1.0
 */
@Message
public class UserInfo {
    private String username;
    private String age;
    public String getUsername() {
        return username;
    }
    public String getAge() {
        return age;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setAge(String age) {
        this.age = age;
    }
    public UserInfo(String username, String age) {
        super();
        this.username = username;
        this.age = age;
    }
    public UserInfo(){

    }
}
