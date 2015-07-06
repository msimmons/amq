package com.gazelle.amq.service;

/**
 * Created by msimmons on 7/1/15.
 */
public class MessageHistoryQuery {

    private String collection;

    private String searchAttribute;

    private String[] summaryAttributes;

    public MessageHistoryQuery(String collection, String searchAttribute, String... summaryAttributes) {
        this.collection = collection;
        this.searchAttribute = searchAttribute;
        this.summaryAttributes = summaryAttributes;
    }

    public String getCollection() {
        return collection;
    }

    public String getSearchAttribute() {
        return searchAttribute;
    }

    public String[] getSummaryAttributes() {
        return summaryAttributes;
    }
}
