package com.gazelle.amq.service;

import com.gazelle.amq.route.WiretapRouteBuilder;
import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;
import org.apache.activemq.broker.BrokerPlugin;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.Message;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsBinding;
import org.apache.camel.component.jms.JmsMessage;
import org.apache.camel.impl.DefaultExchange;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by msimmons on 6/4/15.
 */
public class InterceptorPlugin implements BrokerPlugin {

    private Log log = LogFactory.getLog(getClass());

    @Autowired
    ProducerTemplate producerTemplate;

    @Autowired
    GroupIdService groupIdService;

    public Broker installPlugin(Broker broker) throws Exception {
        log.info("Installing plugin "+getClass());
        return new Interceptor(broker, producerTemplate, groupIdService);
    }

    public class Interceptor extends BrokerFilter {

        private Log log = LogFactory.getLog(getClass());

        private ProducerTemplate producerTemplate;

        private GroupIdService groupIdService;

        private JmsBinding jmsBinding = new JmsBinding();

        public Interceptor(Broker next, ProducerTemplate producerTemplate, GroupIdService groupIdService) {
            super(next);
            this.producerTemplate = producerTemplate;
            this.groupIdService = groupIdService;
        }

/*
        @Override
        public void messageDelivered(ConnectionContext context, MessageReference reference) {
            log.info("Interceptor messageDelivered()");
            super.messageDelivered(context, reference);
        }

        @Override
        public void messageConsumed(ConnectionContext context, MessageReference reference) {
            log.info("Interceptor messageConsumed()");
            super.messageConsumed(context, reference);
        }

        @Override
        public void preProcessDispatch(MessageDispatch dispatch) {
            log.info("Interceptor preProcessDispatch()");
            super.preProcessDispatch(dispatch);
        }

        @Override
        public void postProcessDispatch(MessageDispatch messageDispatch) {
            log.info("Interceptor postProcessDispatch()");
            super.postProcessDispatch(messageDispatch);
        }

        @Override
        public void processDispatchNotification(MessageDispatchNotification messageDispatchNotification) throws Exception {
            log.info("Interceptor processDispatchNotification()");
            super.processDispatchNotification(messageDispatchNotification);
        }

        @Override
        public void beginTransaction(ConnectionContext connectionContext, TransactionId transactionId) throws Exception {
            log.info("Interceptor beginTransaction()");
            super.beginTransaction(connectionContext, transactionId);
        }

        @Override
        public void commitTransaction(ConnectionContext connectionContext, TransactionId transactionId, boolean onePhase) throws Exception {
            log.info("Interceptor commitTransaction()");
            super.commitTransaction(connectionContext, transactionId, onePhase);
        }

        @Override
        public Response messagePull(ConnectionContext context, MessagePull pull) throws Exception {
            log.info("Interceptor messagePull()");
            return super.messagePull(context, pull);
        }

        @Override
        public void acknowledge(ConsumerBrokerExchange consumerExchange, MessageAck ack) throws Exception {
            log.info("Interceptor acknowleged()");
            super.acknowledge(consumerExchange, ack);
        }
*/

        @Override
        public void send(ProducerBrokerExchange exchange, Message message) throws Exception {
            if ( message == null || !(message instanceof ActiveMQTextMessage) ) {
                super.send(exchange, message);
                return;
            }
            ActiveMQTextMessage amqMessage = (ActiveMQTextMessage) message;
            // Set the group id if necessary
            String text = amqMessage.getText();
            amqMessage.setGroupID(String.valueOf(groupIdService.getGroupId(message.getDestination().getPhysicalName(), text)));
            // Send to wiretap route
            wiretap(amqMessage);
            super.send(exchange, message);
        }

        private void wiretap(ActiveMQTextMessage message) {
            try {
                Exchange camelExchange = new DefaultExchange(producerTemplate.getCamelContext());
                camelExchange.setIn(new JmsMessage(message, jmsBinding));
                producerTemplate.send(WiretapRouteBuilder.WIRETAP_ENDPOINT, camelExchange);
            }
            catch (Exception e) {
                log.warn("Error trying to wiretap "+message, e);
            }
        }
    }
}
