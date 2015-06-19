package com.gazelle.amq.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by msimmons on 6/12/15.
 * Configuration for how to determine group id for a given type of message.  Currently geared towards XML messages
 * but could be generalized
 */
public class GroupIdConfig {

    private String destination;

    private String rootElement;

    private String partitionKey;

    private int partitions;

    private Pattern partitionKeyPattern;

    public GroupIdConfig(String destination, String partitionKey, int partitions) {
        this.destination = destination;
        this.partitionKey = partitionKey;
        this.partitions = partitions;
        this.partitionKeyPattern = Pattern.compile(".*<"+partitionKey+">(.*)</"+partitionKey+">.*", Pattern.DOTALL);
    }

    public GroupIdConfig(String destination, String rootElement, String partitionKey, int partitions) {
        this(destination, partitionKey, partitions);
        this.rootElement = rootElement;
    }

    public String getMapKey() {
        return rootElement == null ? destination : rootElement;
    }

    public int getGroupId(String message) {
        Matcher matcher = partitionKeyPattern.matcher(message);
        if ( matcher.matches() ) {
            String key = matcher.group(1);
            return Math.abs(key.hashCode() % partitions)+1;
        }
        else {
            return 0;
        }
    }
}
