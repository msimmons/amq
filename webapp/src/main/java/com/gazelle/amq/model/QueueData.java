package com.gazelle.amq.model;

import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.Message;

import java.util.*;

/**
 * Created by msimmons on 5/11/15.
 */
public class QueueData {

    private QueueSummary summary;
    private List<MessageData> messages = new ArrayList<MessageData>();
    private List<ConsumerData> consumers = new ArrayList<ConsumerData>();

    public QueueData(Destination destination) {
        this.summary = new QueueSummary(destination);
        addMessages(destination);
        addConsumers(destination);
    }

    public QueueSummary getSummary() {
        return summary;
    }

    public List<MessageData> getMessages() {
        return messages;
    }

    public List<ConsumerData> getConsumers() {
        return consumers;
    }

    private void addMessages(Destination destination) {
        for (Message message : destination.browse()) {
            messages.add(new MessageData(message));
        }
    }

    private void addConsumers(Destination destination) {
        for ( Subscription subscription : destination.getConsumers() ) {
            consumers.add(new ConsumerData(subscription));
        }
    }

    public class MessageData {

        private String text;
        private String type;
        private String messageId;
        private String correlationId;
        private String groupId;
        private String producer;
        private String destination;
        private Integer groupSequence;
        private Date timestamp;
        private Map<String, String> properties;

        public MessageData(Message message) {
            try {
                if (message instanceof ActiveMQTextMessage) {
                    this.text = ((ActiveMQTextMessage) message).getText();
                }
                else if ( message.getContent() != null && message.getContent().getData()!=null ) {
                    this.text = new String(message.getContent().getData());
                }
                else {
                    this.text = "";
                }
                this.type = message.getClass().getSimpleName();
                this.messageId = message.getMessageId().toString();
                this.correlationId = message.getCorrelationId();
                this.groupId = message.getGroupID();
                this.groupSequence = message.getGroupSequence();
                this.destination = message.getDestination().getPhysicalName();
                this.producer = message.getProducerId().getConnectionId();
                this.timestamp = new Date(message.getTimestamp());
                this.properties = new HashMap<String, String>();
                for ( Map.Entry<String,Object> property : message.getProperties().entrySet() ) {
                    this.properties.put(property.getKey(), property.getValue().toString());
                }
            } catch (Exception e) {
                this.text = e.getMessage();
            }
        }

        public String getText() {
            return text;
        }

        public String getType() {
            return type;
        }

        public String getMessageId() {
            return messageId;
        }

        public String getCorrelationId() {
            return correlationId;
        }

        public String getGroupId() {
            return groupId;
        }

        public Integer getGroupSequence() {
            return groupSequence;
        }

        public Date getTimestamp() {
            return timestamp;
        }

        public String getProducer() {
            return producer;
        }

        public String getDestination() {
            return destination;
        }

        public Map<String, String> getProperties() {
            return properties;
        }

    }

    public class ConsumerData {

        private String clientId;
        private String connectionId;
        private Long sessionId;
        private Date lastAck;
        private Long enqueued;
        private Long dequeued;
        private Long dispatched;

        public ConsumerData(Subscription subscription) {
            this.lastAck = new Date(subscription.getTimeOfLastMessageAck());
            this.enqueued = subscription.getEnqueueCounter();
            this.dequeued = subscription.getDequeueCounter();
            this.dispatched = subscription.getDispatchedCounter();
            this.clientId = subscription.getConsumerInfo().getClientId();
            this.sessionId = subscription.getConsumerInfo().getConsumerId().getSessionId();
            this.connectionId = subscription.getConsumerInfo().getConsumerId().getConnectionId();
        }

        public String getClientId() {
            return clientId;
        }

        public String getConnectionId() {
            return connectionId;
        }

        public Long getSessionId() {
            return sessionId;
        }

        public Date getLastAck() {
            return lastAck;
        }

        public Long getEnqueued() {
            return enqueued;
        }

        public Long getDequeued() {
            return dequeued;
        }

        public Long getDispatched() {
            return dispatched;
        }
    }
}
