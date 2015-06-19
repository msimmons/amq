package com.gazelle.amq.controller;

import org.apache.activemq.broker.BrokerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by msimmons on 5/11/15.
 */
@Controller
public class IndexController implements ApplicationContextAware {

    Log log = LogFactory.getLog(getClass());

    private static String MINIFIED_FILE_NAME="js/amq-min.js";

    private static String JS_ANT_MATCHER="classpath*:**/*.js";

    @Value("${amq.minified}")
    private Boolean isMinified;

    @Autowired
    private BrokerService brokerService;

    private ApplicationContext context;

    @RequestMapping({"/", "/index", "/index.html"})
    public String index(Model model) throws IOException {
        Set<String> jsFiles = null;
        if (isMinified) {
            log.info("Using minfied app");
            jsFiles = new LinkedHashSet<String>(Arrays.asList(new String[]{MINIFIED_FILE_NAME}));
        }
        else {
            jsFiles = getJsFiles(JS_ANT_MATCHER);
        }
        log.info("Using javascript files "+jsFiles);
        model.addAttribute("jsFiles", jsFiles);
        model.addAttribute("brokerName", brokerService.getBrokerName());
        return "index";
    }

    private Set<String> getJsFiles(String resourceString) throws IOException {
        Set<String> jsFiles = new LinkedHashSet<String>();
        jsFiles.add("js/app.js");
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        for ( Resource resource : resolver.getResources(resourceString)) {
            String name = getJsFileName(resource.getURL());
            if ( name != null ) jsFiles.add(name);
        }
        return jsFiles;
    }

    private String getJsFileName(URL uri) {
        if ( !uri.getPath().contains("static/js") ) return null;
        if ( uri.getPath().contains(MINIFIED_FILE_NAME) ) return null;
        int begin = uri.getPath().indexOf("js/");
        return uri.getPath().substring(begin);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}