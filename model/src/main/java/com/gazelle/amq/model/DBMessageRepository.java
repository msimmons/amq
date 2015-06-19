package com.gazelle.amq.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by msimmons on 4/30/15.
 */
public interface DBMessageRepository {

    /**
     * Return a list of message ids
     */
    public List<Long> getMessageIds();

    /**
     * Get a message by id
     */
    public DBMessage findOne(Long messageId);

    /**
     * Delete the given message id
     */
    public void delete(Long messageId);

    /**
     * Save the given message
     */
    public void save(DBMessage message);

    @Repository
    public class Impl implements DBMessageRepository {

        @Autowired
        JdbcTemplate jdbcTemplate;

        @Override
        @Transactional
        public List<Long> getMessageIds() {
            return jdbcTemplate.queryForList("select id from msg_bridge_to_bus order by id", Long.class);
        }

        @Override
        @Transactional
        public DBMessage findOne(Long messageId) {
            return jdbcTemplate.queryForObject("select * from msg_bridge_to_bus where id=?", new DBMessage(), messageId);
        }

        @Override
        @Transactional
        public void delete(Long messageId) {
            jdbcTemplate.update("delete from msg_bridge_to_bus where id=?", messageId);
        }

        @Override
        @Transactional
        public void save(DBMessage message) {
            jdbcTemplate.update("insert into msg_bridge_from_bus (message, destination, created_at) values (?, ?, now())",
                message.getMessage(), message.getDestination());
        }
    }

}
