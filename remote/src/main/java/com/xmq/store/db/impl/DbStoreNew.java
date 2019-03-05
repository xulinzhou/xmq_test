package com.xmq.store.db.impl;

import com.xmq.config.Config;
import com.xmq.message.BaseMessage;
import com.xmq.message.Broker;
import com.xmq.store.db.IDbStore;
import com.xmq.store.db.IDbStoreNew;
import com.xmq.util.FileConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.store
 * @Description: 数据库操作类
 * @Author: xulinzhou
 * @CreateDate: 2018/9/21 22:15
 * @Version: 1.0
 */
@Component
public class DbStoreNew implements IDbStoreNew {

    private static final Logger log = LoggerFactory.getLogger(DbStoreNew.class);


    private final static String SAVE_MESSAGE_SQL = "INSERT INTO messages(topic,content,status,created,modifyed,received_time) VALUES(?,?,?,?,?,?)";


    private final static String SAVE_BROKER_SQL = "INSERT INTO broker_register(topic,ip,serve_port,create_time,update_time) VALUES(?,?,?,?,?)";

    private final static String GET_MESSAGE_LIST_SQL = "select * from  messages where topic = ?";


    private final static String GET_BROKER_LIST_SQL = "select * from  broker_register where topic = ?";

    private final static String DELETE_BROKER_LIST_SQL = "delete  from  broker_register where ip = ?";

    private final static String SELECT_BROKER_LIST_SQL = "select *  from  broker_register where ip = ?";

    private final static String DELETE_MESSAGE_LIST_SQL = "delete  from  messages where topic = ? and group=?";
    private static com.xmq.util.Config config = new FileConfig("/application.properties");;

    private JdbcTemplate jdbcTemplate;
    public DbStoreNew() {
        String driver = config.getString("store.jdbc.driver");
        String url = config.getString("store.jdbc.url");
        String username = config.getString("store.jdbc.username");
        String password = config.getString("store.jdbc.password");

        javax.sql.DataSource dataSource = DataBaseUtils.createDataSource(driver, url, username,
                password);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void saveMessage(BaseMessage message) {
        jdbcTemplate.update(SAVE_MESSAGE_SQL,
                new Object[] {
                        message.getSubject(),
                        message.getGroupName(),
                        message.getContent(),
                        new Date(),
                        new Date(),
                        new Date()}
        );

    }
    public void saveBroker(Broker broker){
        try{

            List<Broker> brokerList =  jdbcTemplate.query(SELECT_BROKER_LIST_SQL,BROKER_ROW_MAPPER,new  Object[] {broker.getIp()});
            if(brokerList == null || brokerList.size()==0){
                jdbcTemplate.update(SAVE_BROKER_SQL,
                        new Object[] {
                                broker.getTopic(),
                                broker.getIp(),
                                broker.getServerPort(),
                                new Date(),
                                new Date()}
                );
            }

        }catch (Exception e){
            e.printStackTrace();
            log.error("broker 数据库异常");
        }
    }
    private static final RowMapper<Broker> BROKER_ROW_MAPPER = (rs, rowNum) -> {
        final String topic = rs.getString("topic");
        final String ip = rs.getString("ip");
        final String port = rs.getString("serve_port");

        final Broker broker = new Broker();
        broker.setTopic(topic);
        broker.setIp(ip);
        broker.setServerPort(port);
        return broker;
    };
    @Override
    public List<Broker> getBroker(String topic) {
        try{
            return jdbcTemplate.query(GET_BROKER_LIST_SQL,BROKER_ROW_MAPPER,new  Object[] {topic});

        }catch (Exception e){
            e.printStackTrace();
            log.error(" 数据库异常");
            return Collections.emptyList();
        }
    }
    private static final RowMapper<BaseMessage> SUBJECT_MESSAGE_ROW_MAPPER = (rs, rowNum) -> {
        final String messageId = rs.getString("id");
        final String topic = rs.getString("topic");
        final String content = rs.getString("content");

        final BaseMessage baseMessage = new BaseMessage();
        baseMessage.setSubject(topic);
        baseMessage.setContent(content);
        baseMessage.setMessageId(messageId);
        return baseMessage;
    };
    @Override
    public List<BaseMessage> getMessages(String topic,String groupNmame) {
        try{
            return jdbcTemplate.query(GET_MESSAGE_LIST_SQL,SUBJECT_MESSAGE_ROW_MAPPER,new  Object[] {topic});

        }catch (Exception e){
            e.printStackTrace();
            log.error(" 数据库异常");
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteMessage(String topic ,String groupName) {
        try{
             jdbcTemplate.update(DELETE_MESSAGE_LIST_SQL, new  Object[] {topic});

        }catch (Exception e){
            e.printStackTrace();
            log.error(" 数据库异常");
        }
    }

    @Override
    public void deleteBroker(String ip){
        try{
            jdbcTemplate.update(DELETE_BROKER_LIST_SQL,
                    new Object[] {
                             ip}
            );
        }catch (Exception e){
            e.printStackTrace();
            log.error("broker 下线数据库异常");
        }
    }
}
