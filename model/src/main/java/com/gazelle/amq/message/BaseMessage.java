package com.gazelle.amq.message;

/**
 * Created by msimmons on 4/17/15.
 */
public class BaseMessage {

    private ProcessingInfo processingInfo;

    protected BaseMessage() {}

    public ProcessingInfo getProcessingInfo() {
        return processingInfo;
    }
}
