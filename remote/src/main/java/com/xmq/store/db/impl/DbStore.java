package com.xmq.store.db.impl;

import com.xmq.config.Config;
import com.xmq.message.BaseMessage;
import com.xmq.store.db.IDbStore;
import com.xmq.store.file.FileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.Timestamp;
import java.util.Date;

/**
 * @ProjectName: xmq
 * @Package: com.xmq.store
 * @Description: 数据库操作类
 * @Author: xulinzhou
 * @CreateDate: 2018/9/21 22:15
 * @Version: 1.0
 */
@Component
public class DbStore implements  IDbStore {

    private static final Logger log = LoggerFactory.getLogger(DbStore.class);


    private final static String SAVE_MESSAGE_SQL = "INSERT INTO messages(subject,content,status,created,modifyed,received_time) VALUES(?,?,?,?,?,?)";
    private JdbcTemplate jdbcTemplate;
    public   DbStore(Config config) {
        javax.sql.DataSource dataSource = DataBaseUtils.createDataSource(config.getDriver(), config.getUrl(), config.getUsername(),
                config.getPassword());
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void save(BaseMessage message) {
        jdbcTemplate.update(SAVE_MESSAGE_SQL,
                new Object[] {
                        message.getSubject(),
                        message.getGroupName(),
                        0,
                        new Date(),
                        new Date(),
                        new Date()}
        );

    }

}
