package com.xmq.util;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.netty
 * @Description: 命令类型
 * @Author: xulinzhou
 * @CreateDate: 2018/12/20 19:18
 * @Version: 1.0
 */
public enum MessageTypeEnum {

    NULL(-1, "空命令"),
    SYN_MESSAGE(10, "同步消息"),
    WAIT_CANCELED_5(105, "等待取消"),

    SYN_ERROR(400, "同步消息异常");


    MessageTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private int type;
    private String desc;
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public static String getOrderStatusMsg (int type) {
        for (MessageTypeEnum orderStatus : MessageTypeEnum.values()) {
            if (type == orderStatus.getType()) {
                return orderStatus.getDesc();
            }
        }
        return null;
    }
}
