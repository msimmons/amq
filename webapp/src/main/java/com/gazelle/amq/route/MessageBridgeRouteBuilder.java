package com.gazelle.amq.route;

import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.List;

/**
 * Process messages from database and send to specified JMS queue
 */
public class MessageBridgeRouteBuilder extends SpringRouteBuilder {

    @Autowired
    MessageBridgeProcessor processor;

    @Resource(name = "reverseBridgeQueues")
    List<String> reverseQueues;

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).bean(processor, "handleException");

        from("timer://MessageBridge?fixedRate=true&period=10s")
            .routeId(getClass().getSimpleName())
            .bean(processor, "getMessageIds")
            .split(body())
            .bean(processor, "processMessage")
            .routingSlip(header(MessageBridgeProcessor.ROUTING_SLIP_HEADER))
            .bean(processor, "deleteMessage");

        for ( String queueName : reverseQueues ) {
            from("activemq:"+queueName+"?destination.consumer.exclusive=true")
                .routeId("ReverseBridge."+queueName)
                .bean(processor, "saveMessage");
        }

    }

}
