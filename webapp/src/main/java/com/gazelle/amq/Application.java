package com.gazelle.amq;

import com.gazelle.amq.service.GroupIdConfig;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.component.quartz2.QuartzComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.*;

/**
 * Created by msimmons on 3/27/15.
 */
@Configuration
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@EnableAsync
@EnableAutoConfiguration
@ComponentScan
@ImportResource("classpath:activemq.xml")
public class Application implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Value("jdbc:mysql://${amq.db.host}/${amq.db.schema}?relaxAutoCommit=true")
    private String jdbcUrl;

    @Value("${amq.db.user}")
    private String jdbcUser;

    @Value("${amq.db.password}")
    private String jdbcPassword;

    @Value("${amq.broker.url}")
    private String brokerUrl;

    @Value("${amq.admin.name}")
    private String amqUser;

    @Value("${amq.admin.password}")
    private String amqPassword;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.addListeners(new Application());
        application.run(args);
    }

    @Bean
    public Properties quartzProperties() {
        Properties properties = new Properties();
        properties.setProperty("org.quartz.scheduler.instanceName", "GAZELLE");
        properties.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        properties.setProperty("org.quartz.scheduler.rmi.export", "false");
        properties.setProperty("org.quartz.scheduler.rmi.proxy", "false");
        //Thread pool
        properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        properties.setProperty("org.quartz.threadPool.threadCount", "3");
        properties.setProperty("org.quartz.threadPool.threadPriority", "8");

        properties.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
        properties.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        properties.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        properties.setProperty("org.quartz.jobStore.useProperties", "false");
        properties.setProperty("org.quartz.jobStore.dataSource", "QUARTZ_DS");
        properties.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ2_");
        properties.setProperty("org.quartz.jobStore.isClustered", "true");
        //The following caused deadlocks:
        //org.quartz.jobStore.txIsolationLevelSerializable = true

        properties.setProperty("org.quartz.dataSource.QUARTZ_DS.driver", "com.mysql.jdbc.Driver");
        properties.setProperty("org.quartz.dataSource.QUARTZ_DS.URL",  jdbcUrl);
        properties.setProperty("org.quartz.dataSource.QUARTZ_DS.user",  jdbcUser);
        properties.setProperty("org.quartz.dataSource.QUARTZ_DS.password", jdbcPassword);
        properties.setProperty("org.quartz.dataSource.QUARTZ_DS.maxConnections", "10");
        properties.setProperty("org.quartz.dataSource.QUARTZ_DS.validationQuery", "select 0 limit 1");
        return properties;
    }

    @Bean
    public ActiveMQComponent activemq() {
        ActiveMQComponent component = new ActiveMQComponent();
        component.setBrokerURL(brokerUrl);
        component.setUserName(amqUser);
        component.setPassword(amqPassword);
        component.setTransacted(true);
        return component;
    }

    @Bean
    public QuartzComponent quartz2() {
        QuartzComponent quartzComponent = new QuartzComponent();
        quartzComponent.setProperties(quartzProperties());
        return quartzComponent;
    }

    @Bean(name = "reverseBridgeQueues")
    public List<String> reverseBridgeQueues() {
        return Arrays.asList("erp.inventory", "sales.salesEvents");
    }

    @Bean(name="groupIdConfigMap")
    public Map<String, GroupIdConfig> groupIdConfigMap() {
        Map<String, GroupIdConfig> configMap = new HashMap<String, GroupIdConfig>();
        configMap.put("erp.item", new GroupIdConfig("erp.item", "srNumber", 10));
        return configMap;
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
    }

}
