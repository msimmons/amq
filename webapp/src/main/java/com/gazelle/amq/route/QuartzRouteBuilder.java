package com.gazelle.amq.route;

import org.apache.camel.spring.SpringRouteBuilder;

/**
 * Created by msimmons on 4/1/15.
 */
public class QuartzRouteBuilder extends SpringRouteBuilder {

    @Override
    public void configure() throws Exception {
        //from("quartz2://AMQ/MYTIMER?cron=0+0/5+*+*+*+?")
        //    .routeId("MYTIMER")
        //    .to("activemq:erp.foo");
    }

}
