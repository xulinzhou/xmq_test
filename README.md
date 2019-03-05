# xmq_test


Broker 端:
    Broker支持集群部署, 集群节点之间地位平等, 集群部署情况下可大大提高系统的消息吞吐量。
    
    Broker通过自定义注册中心实现集群功能, 各节点在启动时会自动注册到注册中心, Producer或Consumer在生产消息或者消费消息时,将会通过内置注册中心自动感知到在线的Broker节点。
    Broker每隔4s发送心跳，给meta服务，如果Broker服务断开，从数据库中删除对应的服务列表
    
Producer 设计：
Producer，通过netty接口发送mq消息，通过定时任务发送心跳，保持连接
Consumer设计：
向注册中心发送请求，获取broker列表，随机选择一台broker机器返回给consumer端，consumer端连接到broker端，获取数据

注册中心：
1. Zookeeper 注册中心，producer,consumer,启动后，自动注册到Zookeeper，zk保存对应topic对应的服务器列表
2. 自定义注册中心meta，topic，服务器信息，数据保存在mysql中，meta 和 broker通过心跳，保持连接，如果5S内没有心跳，则自动将broker从数据库删除，consumer无法获取对应的broker
