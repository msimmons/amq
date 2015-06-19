package com.gazelle.amq.controller;

import com.gazelle.amq.model.QueueData;
import com.gazelle.amq.model.QueueSummary;
import com.gazelle.amq.service.QueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Created by msimmons on 4/15/15.
 */
@RestController
public class QueueController {

    @Autowired
    private QueueService queueService;

    @RequestMapping(value="/api/queue", method = RequestMethod.GET)
    public Set<QueueSummary> getQueueSummaries() {
        return queueService.findAll();
    }

    @RequestMapping(value="/api/queue/{queueName:.+}", method = RequestMethod.GET)
    public QueueData getQueueData(@PathVariable("queueName") String queueName) {
        return queueService.findOne(queueName);
    }

    @RequestMapping(value="/api/queue/{queueName:.+}", method = RequestMethod.DELETE)
    public void purgeQueue(@PathVariable("queueName") String queueName) {
        queueService.purge(queueName);
    }

    @RequestMapping(value="/api/queue/{queueName:.+}", method = RequestMethod.POST)
    public void sendMessage(@PathVariable("queueName") String queueName, @RequestBody String message) {
        queueService.send(queueName, message);
    }

}
