package com.xmq.message;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.message
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2019/2/26 16:54
 * @Version: 1.0
 */
public class Broker {
    private String topic;
    private String ip;
    private String serverPort;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }
}
