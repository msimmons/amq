package com.gazelle.amq.service;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by msimmons on 6/17/15.
 */
public interface MessageHistoryService {

    public Set<MessageHistoryResult> find(String searchString);

    @Service
    public class Impl implements MessageHistoryService {

        Log logger = LogFactory.getLog(getClass());

        @Autowired
        QueryExecutor queryExecutor;

        private static List<MessageHistoryQuery> ITEM_QUERIES = new ArrayList<MessageHistoryQuery>();
        static {
            ITEM_QUERIES.add(new MessageHistoryQuery("itemReceived", "srNumber", "estimatedTiv", "headers.JMSDestination", "headers.destination"));
            ITEM_QUERIES.add(new MessageHistoryQuery("itemAccepted", "srNumber", "baseCost", "headers.JMSDestination", "headers.destination"));
            ITEM_QUERIES.add(new MessageHistoryQuery("itemPacked", "srNumber", "disposition", "headers.JMSDestination", "headers.destination"));
            ITEM_QUERIES.add(new MessageHistoryQuery("itemUnpacked", "srNumber", "headers.JMSDestination"));
            ITEM_QUERIES.add(new MessageHistoryQuery("itemLost", "srNumber", "headers.JMSDestination"));
            ITEM_QUERIES.add(new MessageHistoryQuery("itemDeclined", "srNumber", "headers.JMSDestination"));
            ITEM_QUERIES.add(new MessageHistoryQuery("itemChanged", "srNumber", "headers.JMSDestination"));
            ITEM_QUERIES.add(new MessageHistoryQuery("itemInspected", "srNumber", "sku"));
            ITEM_QUERIES.add(new MessageHistoryQuery("addItem", "line.srNumber", "source", "sourceId"));
            ITEM_QUERIES.add(new MessageHistoryQuery("replaceItem", "oldSrNumber", "orderSource", "orderSourceId"));
            ITEM_QUERIES.add(new MessageHistoryQuery("replaceItem", "newSrNumber", "orderSource", "orderSourceId"));
            ITEM_QUERIES.add(new MessageHistoryQuery("createOrder", "orderLine.srNumber", "source", "sourceId"));
            ITEM_QUERIES.add(new MessageHistoryQuery("orderShipped", "lineItem.srNumber", "orderNumber"));
            ITEM_QUERIES.add(new MessageHistoryQuery("rmaReceived", "srNumber", "rmaNumber"));
            ITEM_QUERIES.add(new MessageHistoryQuery("rmaReceived", "rmaNumber", "srNumber"));
            ITEM_QUERIES.add(new MessageHistoryQuery("rmaAccepted", "srNumber", "rmaNumber"));
            ITEM_QUERIES.add(new MessageHistoryQuery("rmaAccepted", "rmaNumber", "srNumber"));
            ITEM_QUERIES.add(new MessageHistoryQuery("inventoryChanges", "inventoryChange.srNumber"));
        }

        @Override
        public Set<MessageHistoryResult> find(String searchString) {
            List<Future<List<MessageHistoryResult>>> futures = new ArrayList<Future<List<MessageHistoryResult>>>();
            for ( MessageHistoryQuery query : ITEM_QUERIES) {
                futures.add(queryExecutor.executeQuery(query, searchString));
            }
            Set<MessageHistoryResult> result = new TreeSet<MessageHistoryResult>();
            for ( Future<List<MessageHistoryResult>> future : futures ) {
                try {
                    result.addAll(future.get(2, TimeUnit.MINUTES));
                } catch (Exception e) {
                    logger.error(e);
                    result.add(new MessageHistoryResult(e));
                }
            }
            return result;
        }
    }

    @Service
    public class QueryExecutor {

        @Autowired
        MongoTemplate mongoTemplate;

        @Async
        public Future<List<MessageHistoryResult>> executeQuery(MessageHistoryQuery queryDef, String searchString) {
            DBObject query = new BasicDBObject();
            query.put(queryDef.getSearchAttribute(), searchString);
            List<MessageHistoryResult> results = new LinkedList<MessageHistoryResult>();
            for ( DBObject result : mongoTemplate.getCollection(queryDef.getCollection()).find(query) ) {
                if ( result instanceof BasicDBList ) {
                    results.addAll(getResults(queryDef, (BasicDBList)result));
                }
                else if ( result instanceof BasicDBObject ) {
                    results.add(getResult(queryDef, (BasicDBObject) result));
                }
                else {
                }
            }
            return new AsyncResult<List<MessageHistoryResult>>(results);
        }

        private MessageHistoryResult getResult(MessageHistoryQuery queryDef, BasicDBObject dbObject) {
            StringBuilder summary = new StringBuilder();
            for ( String summaryAttribute : queryDef.getSummaryAttributes() ) {
                summary.append(getValue(dbObject, summaryAttribute, String.class)).append(",");
            }
            ObjectId oid = dbObject.getObjectId("_id");
            Date createdAt = oid.getDate();
            return new MessageHistoryResult(queryDef.getCollection(), oid, createdAt, summary.toString(), dbObject.toString());
        }

        private List<MessageHistoryResult> getResults(MessageHistoryQuery queryDef, BasicDBList dbList) {
            for ( Object o : dbList ) {
                if ( o instanceof BasicDBList ) return getResults(queryDef, (BasicDBList)o);
                else if ( o instanceof BasicDBObject ) return Arrays.asList(getResult(queryDef, (BasicDBObject)o));
            }
            return Collections.emptyList();
        }

        /**
         * Get attributes for the given key ( using dot notation )
         * @param keyString
         * @param valueClass
         * @param <T>
         * @return
         */
        private <T> T getValue(DBObject dbObject, String keyString, Class<T> valueClass) {
            String[] keyParts = keyString.split("\\.");
            if ( keyParts.length==0 ) keyParts = new String[] {keyString};
            DBObject current = dbObject;
            int i=0;
            for ( ; i<keyParts.length-1; i++) {
                current = (DBObject)current.get(keyParts[i]);
            }
            Object value = current.get(keyParts[i]);
            if ( value == null ) return null;
            else if ( valueClass.isAssignableFrom(value.getClass()) ) return (T)value;
            else if ( valueClass.isAssignableFrom(String.class)) return (T)value.toString();
            else return null;
        }

    }
}
