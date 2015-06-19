package com.gazelle.amq.route;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.jms.JmsConstants;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by msimmons on 4/1/15.
 */
public class ConsumerRouteBuilder extends SpringRouteBuilder {

    Logger logger = Logger.getLogger(getClass());

    @Override
    public void configure() throws Exception {
        from("activemq:erp.foo?maxConcurrentConsumers=10")
            .routeId("theConsumer1")
            .process(new ConsumerProcessor("consumer1"));

        from("activemq:erp.foo?maxConcurrentConsumers=10")
            .routeId("theConsumer2")
            .process(new ConsumerProcessor("consumer2"));

        from("activemq:erp.foo?maxConcurrentConsumers=10")
            .routeId("theConsumer3")
            .process(new ConsumerProcessor("consumer3"));
    }

    public class ConsumerProcessor implements Processor {

        private Set<String> groups = new HashSet<String>();
        private String id;

        ConsumerProcessor(String id) {
            this.id = id;
        }

        @Override
        public void process(Exchange exchange) throws Exception {
            String group = (String)exchange.getIn().getHeader(JmsConstants.JMS_X_GROUP_ID);
            groups.add(group);
            logger.info("*** Processing: "+exchange.getIn().getBody());
            Thread.sleep(5000);
            logger.info("*** Finish processing "+id+"-"+group+"("+groups+")");
        }
    }

}
