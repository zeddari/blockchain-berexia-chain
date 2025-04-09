package com.berexia.orchestrator.config;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration class for Flowable 6.8.0 specifics.
 */
@Configuration
@Slf4j
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    @Value("classpath*:/processes/*.bpmn20.xml")
    private Resource[] processResources;

    private RepositoryService repositoryService;

    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        engineConfiguration.setDatabaseSchemaUpdate("true");
        engineConfiguration.setActivityFontName("Arial");
        engineConfiguration.setLabelFontName("Arial");
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(
            DataSource dataSource,
            PlatformTransactionManager transactionManager) {
        SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
        configuration.setDataSource(dataSource);
        configuration.setTransactionManager(transactionManager);
        configuration.setDatabaseSchemaUpdate("true");
        configuration.setActivityFontName("Arial");
        configuration.setLabelFontName("Arial");
        return configuration;
    }

    @Bean
    public ProcessEngine processEngine(SpringProcessEngineConfiguration configuration) {
        return configuration.buildProcessEngine();
    }

    @Bean
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        this.repositoryService = processEngine.getRepositoryService();
        deployProcesses();
        return this.repositoryService;
    }

    @Bean
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    @Bean
    public TaskService taskService(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    private void deployProcesses() {
        try {
            for (Resource resource : processResources) {
                String fileName = resource.getFilename();
                log.info("Deploying process: {}", fileName);
                repositoryService.createDeployment()
                    .addInputStream(fileName, resource.getInputStream())
                    .deploy();
            }
            log.info("Successfully deployed all BPMN processes");
        } catch (Exception e) {
            log.error("Error deploying BPMN processes", e);
            throw new RuntimeException("Failed to deploy BPMN processes", e);
        }
    }
} 