package com.xmq.util;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.util
 * @Description: java类作用描述
 * @Author: xulinzhou
 * @CreateDate: 2018/9/18 15:48
 * @Version: 1.0
 */
public class Constants {

    public static final String MQ_ZK_ROOT = "/mq";

    public static final String ZK_PATH_SEP = "/";

    /*
    consumer订阅消息subject时在zookeeper上的路径
     */
    public static final String CONSUMER_SUBJECT_ROOT = MQ_ZK_ROOT + "/consumer/subject";

    /*
         放置broker的配置信息的zookeeper路径
     */
    public static final String BROKER_ROOT = MQ_ZK_ROOT + "/broker";

    public static final String PROTOCOL = "mq";

    public static final String SELECTOR = "selector";

}
