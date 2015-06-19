package com.gazelle.amq.route;

import com.gazelle.amq.service.MessageLoggerService;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by msimmons on 4/16/15.
 */
@Component
public class WiretapProcessor implements Processor {

    @Autowired
    MessageLoggerService messageLogger;

    @Override
    public void process(Exchange exchange) throws Exception {
        String json = exchange.getIn().getBody(String.class);
        if (StringUtils.isEmpty(json)) return;
        Map<String,Object> headers = exchange.getIn().getHeaders();
        messageLogger.save(json, headers);
    }

}
