package com.gazelle.amq.service;

import com.gazelle.amq.model.DBMessage;
import com.gazelle.amq.model.DBMessageRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by msimmons on 4/29/15.
 */
public interface MessageBridgeService {

    /**
     * Get a list of unprocessed message ids
     * @return
     */
    public List<Long> getMessageIds();

    /**
     * Get the pending message for the given id
     */
    public DBMessage getMessage(Long messageId);

    /**
     * Delete the given message id
     */
    public void deleteMessage(Long messageId);

    /**
     * Handle an exception in message processing
     */
    public void handleException(Long messageId, Exception e);

    /**
     * Save a message in the reverse bridge
     */
    public void saveMessage(DBMessage message);

    @Service
    public class Impl implements MessageBridgeService {

        Log log = LogFactory.getLog(getClass());

        @Autowired
        DBMessageRepository dbMessageRepository;

        @Override
        public List<Long> getMessageIds() {
            return dbMessageRepository.getMessageIds();
        }

        @Override
        @Transactional
        public DBMessage getMessage(Long messageId) {
            return dbMessageRepository.findOne(messageId);
        }

        @Override
        @Transactional
        public void deleteMessage(Long messageId) {
            dbMessageRepository.delete(messageId);
        }

        @Override
        @Transactional
        public void handleException(Long messageId, Exception e) {
            log.info("An exception occurred on "+messageId, e);
        }

        @Override
        @Transactional
        public void saveMessage(DBMessage message) {
            dbMessageRepository.save(message);
        }
    }
}
