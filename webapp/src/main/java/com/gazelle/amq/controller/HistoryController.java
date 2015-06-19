package com.gazelle.amq.controller;

import com.gazelle.amq.service.MessageHistoryResult;
import com.gazelle.amq.service.MessageHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by msimmons on 4/15/15.
 */
@RestController
public class HistoryController {

    @Autowired
    private MessageHistoryService historyService;

    @RequestMapping(value="/api/history/{searchString:.+}", method = RequestMethod.GET)
    public List<MessageHistoryResult> getHistory(@PathVariable("searchString") String searchString) {
        return historyService.find(searchString);
    }

}
