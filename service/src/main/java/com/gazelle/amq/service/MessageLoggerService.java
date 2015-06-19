package com.gazelle.amq.service;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by msimmons on 4/28/15.
 */
public interface MessageLoggerService {

    /**
     * Log a JSON message string plus the given map of properties
     * @param json
     * @param properties
     */
    public void save(String json, Map<String,Object> properties);

    @Service
    public class Impl implements MessageLoggerService {

        Log log = LogFactory.getLog(getClass());

        private static final Set<Class> SERIALIZABLE_CLASSES = new HashSet<Class>();
        static {
            SERIALIZABLE_CLASSES.add(String.class);
            SERIALIZABLE_CLASSES.add(Long.class);
            SERIALIZABLE_CLASSES.add(Integer.class);
            SERIALIZABLE_CLASSES.add(Boolean.class);
        }

        private static final String CREATE_DATE_KEY = "create_dt";
        private static final String HEADERS_KEY = "headers";


        @Autowired
        MongoTemplate mongoTemplate;

        @Override
        public void save(String json, Map<String,Object> properties) {
            try {
                BSONObject bson = (BSONObject) JSON.parse(json);

                String collection = "";
                for (String key : bson.keySet()) {
                    collection = key;
                }
                bson = (BSONObject) bson.get(collection);
                BSONObject headers = new BasicBSONObject();
                for (Map.Entry<String, Object> entry : properties.entrySet()) {
                    if (entry.getValue() == null) continue;
                    String key = entry.getKey().replace('.', '-');
                    if (SERIALIZABLE_CLASSES.contains(entry.getValue().getClass())) {
                        headers.put(key, entry.getValue());
                    } else {
                        headers.put(key, entry.getValue().toString());
                    }
                }
                Date createdAt = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
                headers.put(CREATE_DATE_KEY, createdAt);
                bson.put(HEADERS_KEY, headers);
                mongoTemplate.getDb().getCollection(collection).save(new BasicDBObject(bson.toMap()));
            }
            catch (Exception e) {
                log.warn("Unable to wiretap \n"+json, e);
            }
        }

    }
}
