package com.gazelle.amq.route;

import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by msimmons on 5/5/15.
 */
public class QueueMonitorRouteBuilder extends SpringRouteBuilder {

    @Autowired
    QueueMonitorProcessor processor;

    @Override
    public void configure() throws Exception {

        from("timer://QueueMonitor?period=5m")
            .routeId(getClass().getSimpleName())
            .process(processor);

    }
}
