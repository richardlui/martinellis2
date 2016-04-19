package com.martinellis.rest.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

import com.martinellis.rest.api.type.Reply;
import com.martinellis.rest.utils.StringUtil;
import com.martinellis.rest.utils.config.Environment;
import com.martinellis.rest.utils.config.ServiceLocator;

public class TalentDaoImpl implements TalentDao {

    private static final String GENERATE_USER_ID = 
            "INSERT INTO user (last_updated) VALUES (:lastUpdated)";
    
    private static final String INSERT_NEW_USER = 
            "INSERT INTO user (first_name, last_name, email) VALUES (:firstName, :lastName, :email)";
    
    private static final String INSERT_NEW_TOPIC = 
            "INSERT INTO topic (by_user_id) VALUES (:userId)";
    
    private static final String INSERT_REPLY =
            "INSERT INTO reply (topic_id, comment, by_user_id) VALUES (:topicId, :comment, :userId)";
    
    private static final String FIND_REPLY_BY_THREAD_ID = 
            "SELECT * FROM reply WHERE topic_id=:topicId order by last_updated desc";
    
    private static final String DELETE_USER_SETTINGS =
            "DELETE FROM settings WHERE user_id=:userid AND namespace=:namespace";
    
    private static final String DELETE_USER_SETTINGS_BY_KEYS =
            "DELETE FROM settings WHERE user_id=:userid AND namespace=:namespace AND skey in (:listOfKeys)";
    
    static final String DELETE_ALL_KEYS_TOKEN = "__ALL__";
    
    private Logger logger = LoggerFactory.getLogger(TalentDaoImpl.class);
    
    private NamedParameterJdbcOperations template;
    
    private Environment env = ServiceLocator.get().lookup(Environment.class);
    
    public NamedParameterJdbcOperations getJdbcTemplate() {
        return template;
    }

    public void setJdbcTemplate(NamedParameterJdbcOperations template) {
        this.template = template;
    }
    
    @Override
    public Long getUserId() {
        Map<String, Object> argMap = new HashMap<String, Object>();
        argMap.put("lastUpdated", Calendar.getInstance().getTime());
        
        // insert
        template.update(GENERATE_USER_ID, argMap);
        
        
        if (env.isTestEnv()) {
            long id = template.queryForLong("call identity()", argMap);
            return id; 
        } else {
            long id = template.queryForLong("select last_insert_id()", argMap);
            return id;
        }
    }
    
    @Override
    public Long createUser(String firstName, String lastName, String email) {
        Map<String, Object> argMap = new HashMap<String, Object>();
        argMap.put("firstName", firstName);
        argMap.put("lastName", lastName);
        argMap.put("email", email);
        
        // insert
        template.update(INSERT_NEW_USER, argMap);
        if (env.isTestEnv()) {
            long id = template.queryForLong("call identity()", argMap);
            logger.info("Created a new user: {}", id);
            return id; 
        } else {
            long id = template.queryForLong("select last_insert_id()", argMap);
            logger.info("Created a new user: {}", id);
            return id;
        }
    }
    
    @Override
    public Long addTopic(Long userId) {
        Map<String, Object> argMap = new HashMap<String, Object>();
        argMap.put("userId", userId);
        
        // insert
        template.update(INSERT_NEW_TOPIC, argMap);
        if (env.isTestEnv()) {
            long id = template.queryForLong("call identity()", argMap);
            logger.info("Created a new topic: {}", id);
            return id; 
        } else {
            long id = template.queryForLong("select last_insert_id()", argMap);
            logger.info("Created a new topic: {}", id);
            return id;
        }
        
    }
    
    @Override
    public Long addReply(Long userId, Long topicId, String comment) {
        Map<String, Object> argMap = new HashMap<String, Object>();
        argMap.put("userId", userId);
        argMap.put("topicId", topicId);
        argMap.put("comment", comment);
        
        // insert
        template.update(INSERT_REPLY, argMap);
        if (env.isTestEnv()) {
            long id = template.queryForLong("call identity()", argMap);
            logger.info("Created a new reply: {}", id);
            return id; 
        } else {
            long id = template.queryForLong("select last_insert_id()", argMap);
            logger.info("Created a new reply: {}", id);
            return id;
        }
        
    }
    
    @Override
    public List<Reply> findReplyByTopicId (Long topicId) {
        Map<String, Object> argMap = new HashMap<String, Object>();
        argMap.put("topicId", topicId);
        List <Reply> list = template.query(FIND_REPLY_BY_THREAD_ID, argMap, new ReplyRowMapper());       
        return list;
    }
    
    @Override
    public int delete(String userId, String namespace, String keys) {
        
        if (keys == null || keys.isEmpty() || keys.indexOf(DELETE_ALL_KEYS_TOKEN) != -1) {
            return delete(userId, namespace);
        }
        
        List<String> list = StringUtil.splitIntoList(keys);
        
        Map<String, Object> argMap = new HashMap<String, Object>();
        argMap.put("userid", userId);
        argMap.put("namespace", namespace);
        argMap.put("listOfKeys", list);
        
        return template.update(DELETE_USER_SETTINGS_BY_KEYS, argMap);
    }
    
    private int delete(String userId, String namespace) {
        Map<String, Object> argMap = new HashMap<String, Object>();
        argMap.put("userid", userId);
        argMap.put("namespace", namespace);
        return template.update(DELETE_USER_SETTINGS, argMap);
    }
    
    private class ReplyRowMapper implements RowMapper<Reply> {

        @Override
        public Reply mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
            Reply row = new Reply();
            row.setId(rs.getLong("reply_id"));
            row.setComment(rs.getString("comment"));
            row.setUserId(rs.getLong("by_user_id"));
            row.setTime(rs.getDate("last_updated"));
            return row;
        }
    }
 
}
