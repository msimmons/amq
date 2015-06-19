package com.gazelle.amq.route;

import com.gazelle.amq.model.DBMessage;
import com.gazelle.amq.service.MessageBridgeService;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by msimmons on 4/29/15.
 */
@Component
public class MessageBridgeProcessor {

    public static final String MESSAGE_ID_HEADER="GZMessageBridgeId";
    public static final String ROUTING_SLIP_HEADER="GZMessageBridgeDestination";

    @Autowired
    MessageBridgeService messageBridgeService;

    /**
     * Set the exchange body to a list of message ids to be processed
     * @param exchange
     */
    public void getMessageIds(Exchange exchange) {
        List<Long> messageIds = messageBridgeService.getMessageIds();
        exchange.getIn().setBody(messageIds);
    }

    /**
     * Process a single message; get it from the database, set the body
     * to the message and a routing slip header to the destination
     */
    public void processMessage(Exchange exchange) {
        Long messageId = exchange.getIn().getBody(Long.class);
        DBMessage message = messageBridgeService.getMessage(messageId);
        if ( message == null )
            throw new IllegalStateException("Message is null for id "+messageId);
        exchange.getIn().setHeader(MESSAGE_ID_HEADER, messageId);
        exchange.getIn().setHeader(ROUTING_SLIP_HEADER, message.getDestination());
        exchange.getIn().setBody(message.getMessage());
    }

    /**
     * Delete the message id for this exchange
     */
    public void deleteMessage(Exchange exchange) {
        Long messageId = exchange.getIn().getHeader(MESSAGE_ID_HEADER, Long.class);
        messageBridgeService.deleteMessage(messageId);
    }

    /**
     * Save a reverse message to table
     */
    public void saveMessage(Exchange exchange) {
        String destination = exchange.getIn().getHeader("JMSDestination", String.class);
        String message = exchange.getIn().getBody(String.class);
        DBMessage dbMessage = new DBMessage(destination, message);
        messageBridgeService.saveMessage(dbMessage);
    }

    /**
     * Handle any exception conditions
     */
    public void handleException(Exchange exchange) {
        Exception e = (Exception) exchange.getProperty(Exchange.EXCEPTION_CAUGHT);
        Long messageId = exchange.getIn().getHeader(MESSAGE_ID_HEADER, Long.class);
        messageBridgeService.handleException(messageId, e);
    }
}
