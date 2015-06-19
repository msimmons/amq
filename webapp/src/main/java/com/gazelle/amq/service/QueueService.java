package com.gazelle.amq.service;

import com.gazelle.amq.model.QueueData;
import com.gazelle.amq.model.QueueSummary;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.broker.region.Destination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.ObjectName;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by msimmons on 5/5/15.
 */
public interface QueueService {

    public Set<QueueSummary> findAll();

    public void purge(String queueName);

    public QueueData findOne(String queueName);

    public void send(String queueName, String message);

    @Service
    public class Impl implements QueueService {

        private final static String AMQ_JMX_DOMAIN="org.apache.activemq";

        Log log = LogFactory.getLog(getClass());

        @Autowired
        BrokerService broker;

        @Autowired
        CamelContext camelContext;

        @Autowired
        ProducerTemplate producerTemplate;

        @Override
        public Set<QueueSummary> findAll() {
            Set<QueueSummary> queues = new TreeSet<QueueSummary>();
            try {
                for (ObjectName name : broker.getAdminView().getQueues() ) {
                    String destinationName = name.getKeyProperty("destinationName");
                    Destination destination = getDestination(destinationName);
                    QueueSummary queueSummary = new QueueSummary(destination);
                    queues.add(queueSummary);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error getting queue data",e);
            }
            return queues;
        }

        @Override
        public void purge(String queueName) {
            // The parameters for an ObjectName
            Hashtable<String, String> params = new Hashtable<String, String>();
            params.put("type", "Broker");
            params.put("destinationType", "Queue");
            params.put("brokerName", broker.getBrokerName());
            params.put("destinationName", queueName);

            try {
                // Create an ObjectName
                ObjectName queueObjectName = ObjectName.getInstance(AMQ_JMX_DOMAIN, params);

                log.info("JMX Domain: " + broker.getManagementContext().getJmxDomainName());
                //log.info("names: "+broker.getManagementContext().queryNames(null, null));
                // Create a proxy to the QueueViewMBean
                QueueViewMBean queueProxy = (QueueViewMBean) broker.getManagementContext().newProxyInstance(queueObjectName, QueueViewMBean.class, true);

                // Purge the queue
                queueProxy.purge();

            }
            catch (Exception e) {
                throw new RuntimeException("Error purging queue "+queueName,e);
            }
        }

        @Override
        public QueueData findOne(String queueName) {
            try {
                Destination destination = getDestination(queueName);
                return new QueueData(destination);
            }
            catch (Exception e) {
                throw new RuntimeException("Error getting queue data "+queueName,e);
            }
        }

        @Override
        public void send(String queueName, String message) {
            producerTemplate.sendBody("activemq:"+queueName, message);
        }

        private Destination getDestination(String name) throws Exception {
            ActiveMQQueue queue = new ActiveMQQueue(name);
            return broker.getDestination(queue);
        }

    }
}
