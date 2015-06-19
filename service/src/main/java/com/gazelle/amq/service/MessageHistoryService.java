package com.gazelle.amq.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by msimmons on 6/17/15.
 */
public interface MessageHistoryService {

    public List<MessageHistoryResult> find(String searchString);

    @Service
    public class Impl implements MessageHistoryService {

        @Autowired
        QueryExecutor queryExecutor;

        @Override
        public List<MessageHistoryResult> find(String searchString) {
            DBObject query = new BasicDBObject();
            DBObject sort = new BasicDBObject();
            query.put("srNumber", searchString);
            Future<List<MessageHistoryResult>> future1 = queryExecutor.executeQuery("itemAccepted", query);
            Future<List<MessageHistoryResult>> future2 = queryExecutor.executeQuery("itemPacked", query);
            List<MessageHistoryResult> result = new ArrayList<MessageHistoryResult>();
            try {
                result.addAll(future1.get(2, TimeUnit.MINUTES));
                result.addAll(future2.get(2, TimeUnit.MINUTES));
                return result;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            return Collections.emptyList();
        }
    }

    @Service
    public class QueryExecutor {

        @Autowired
        MongoTemplate mongoTemplate;

        @Async
        public Future<List<MessageHistoryResult>> executeQuery(String collection, DBObject query) {
            List<MessageHistoryResult> results = new LinkedList<MessageHistoryResult>();
            for ( DBObject dbo : mongoTemplate.getCollection(collection).find(query) ) {
                if ( dbo instanceof BasicDBObject ) {
                    BasicDBObject bdbo = (BasicDBObject) dbo;
                    Date createdAt = ((BasicDBObject)bdbo.get("headers")).getDate("create_dt");
                    String summary = bdbo.getString("srNumber");
                    ObjectId oid = bdbo.getObjectId("_id");
                    results.add(new MessageHistoryResult(collection, oid, createdAt, summary, dbo.toString()));
                }
            }
            return new AsyncResult<List<MessageHistoryResult>>(results);
        }

    }
}
