package com.gazelle.amq.route;

import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by msimmons on 5/5/15.
 */
public class NotificationRouteBuilder extends SpringRouteBuilder {

    @Autowired
    NotificationProcessor processor;

    private String fromEndpoint="activemq:common.notification";

    @Override
    public void configure() throws Exception {
        from(fromEndpoint)
            .routeId(getClass().getSimpleName())
            .process(processor);
    }
}
