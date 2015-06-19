package com.gazelle.amq.route;

import com.gazelle.amq.model.QueueSummary;
import com.gazelle.amq.service.QueueService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by msimmons on 5/5/15.
 */
@Component
public class QueueMonitorProcessor implements Processor {

    private Log log = LogFactory.getLog(getClass());

    @Autowired
    private QueueService queueService;

    private Map<String,QueueSummary> previousData = new HashMap<String,QueueSummary>();

    @Override
    public void process(Exchange exchange) throws Exception {
        StringBuilder body = new StringBuilder();
        for ( QueueSummary queueSummary : queueService.findAll() ) {
            QueueSummary previous = previousData.remove(queueSummary.name);
            if ( queueSummary.count == 0 ) continue;
            previousData.put(queueSummary.name, queueSummary);
            if ( previous == null ) continue;
            // Nothing dequeued since last check
            if ( previous.dequeues == queueSummary.dequeues ) {
                body.append(queueSummary +"\n");
            }
        }
        if ( body.length() > 0 ) {
            body.insert(0, "The following queues are not being consumed\n\n");
            exchange.getIn().setBody(body.toString());
            log.warn(body);
        }
    }
}
