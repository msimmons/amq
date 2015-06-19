package com.gazelle.amq.model;

import org.apache.activemq.broker.region.Destination;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Created by msimmons on 5/5/15.
 */
public class QueueSummary implements Comparable<QueueSummary> {

    public String name;
    public Long count;
    public Long enqueues;
    public Long dispatched;
    public Long dequeues;
    public Integer consumers;

    public QueueSummary(Destination destination) {
        this.name = destination.getName();
        this.count = destination.getDestinationStatistics().getMessages().getCount();
        this.consumers = destination.getConsumers().size();
        this.dequeues = destination.getDestinationStatistics().getDequeues().getCount();
        this.enqueues = destination.getDestinationStatistics().getEnqueues().getCount();
        this.dispatched = destination.getDestinationStatistics().getDispatched().getCount();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if ( o==null || !getClass().isAssignableFrom(o.getClass())) return false;
        return ((QueueSummary)o).name.equals(name);
    }

    @Override
    public int compareTo(QueueSummary o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
