package com.gazelle.amq.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by msimmons on 6/12/15.
 * Determine the group is of the given message
 */
public interface GroupIdService {

    public int getGroupId(String destination, String message);

    @Service
    public class Impl implements GroupIdService {

        Log log = LogFactory.getLog(getClass());

        @Resource(name = "groupIdConfigMap")
        private Map<String, GroupIdConfig> configMap;

        private Pattern rootElementPattern = Pattern.compile("(<\\?.*\\?>){0,1}[\\s]*<([a-zA-Z]+)>.*", Pattern.DOTALL);

        public int getGroupId(String destination, String message) {
            Matcher matcher = rootElementPattern.matcher(message);
            if (!matcher.matches()) {
                log.debug("No root element match for "+message);
                return 0;
            }
            String rootElement = matcher.group(2);
            log.debug("Found root element " + rootElement);
            GroupIdConfig config = configMap.get(rootElement);
            if ( config == null ) config = configMap.get(destination);
            if ( config == null ) {
                log.debug("No config found for "+destination+" "+rootElement+" using "+configMap);
                return 0;
            }
            log.debug("Found config for " + config.getMapKey());
            return config.getGroupId(message);
        }
    }
}
