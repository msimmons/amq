package com.gazelle.amq.message;

/**
 * Created by msimmons on 4/17/15.
 */
public class ProcessingInfo {

    private String busId;
    private String partitionKey;
    private String lastException;

    protected ProcessingInfo() {}

    public String getBusId() {
        return busId;
    }

    public String getPartitionKey() {
        return partitionKey;
    }

    public String getLastException() {
        return lastException;
    }
}
