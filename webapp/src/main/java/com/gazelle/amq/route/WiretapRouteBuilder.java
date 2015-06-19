package com.gazelle.amq.route;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.dataformat.xmljson.XmlJsonDataFormat;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by msimmons on 4/1/15.
 * VM route to perform wiretapping.  Called from interceptor plugin
 */
public class WiretapRouteBuilder extends SpringRouteBuilder {

    Logger logger = Logger.getLogger(getClass());

    private static final String JMS_DESTINATION = "JMSDestination";

    @Autowired
    private WiretapProcessor wiretapProcessor;

    public static final String WIRETAP_ENDPOINT = "vm:wireTapLogger";

    @Override
    public void configure() throws Exception {
        XmlJsonDataFormat xmlJsonDataFormat = new XmlJsonDataFormat();
        xmlJsonDataFormat.setEncoding("UTF-8");
        xmlJsonDataFormat.setForceTopLevelObject(true);
        xmlJsonDataFormat.setTrimSpaces(true);
        xmlJsonDataFormat.setSkipNamespaces(true);
        xmlJsonDataFormat.setRemoveNamespacePrefixes(true);

        onException().handled(true).convertBodyTo(String.class).to("activemq:broker.error");

        from(WIRETAP_ENDPOINT+"?concurrentConsumers=20")
            .routeId(WIRETAP_ENDPOINT)
            .choice().when(new ShouldWiretap())
            .choice()
            .when(new IsXml())
            .marshal(xmlJsonDataFormat)
            .end()
            .process(wiretapProcessor)
            .end();
    }

    class IsXml implements Predicate {

        @Override
        public boolean matches(Exchange exchange) {
            String body = exchange.getIn().getBody(String.class);
            if (StringUtils.isEmpty(body)) return false;
            if (!body.startsWith("<")) return false;
            int start = body.indexOf('<');
            int end = body.indexOf('>');
            String rootElement = body.substring(start + 1, end);
            // Remove processing instructions if any
            body = body.replaceAll("\\<\\?.*\\?\\>", "");
            // Set the root element as a _type element
            body = body.replace("<" + rootElement + ">", "<" + rootElement + "><_type>" + rootElement + "</_type>");
            exchange.getIn().setBody(body);
            return true;
        }
    }

    class ShouldWiretap implements Predicate {

        @Override
        public boolean matches(Exchange exchange) {
            String destination = exchange.getIn().getHeader(JMS_DESTINATION).toString();
            if (destination.endsWith(".error") || destination.endsWith(".invalid") || destination.endsWith(".trigger")) {
                logger.info("Skip wiretap to " + destination);
                return false;
            }
            return true;
        }
    }

}
