package com.acme.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.acme.batch.JobCompletionNotificationListener;
import com.acme.model.Statement;
import com.acme.processor.StatementProcessor;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

/**
 * Class to represent the configuration of the applicaton.
 */
@Configuration
@EnableBatchProcessing
public class ApplicationConfiguration {
    @Resource
    public JobBuilderFactory jobBuilderFactory;

    @Resource
    public StepBuilderFactory stepBuilderFactory;

    @Bean(name = "processedStatements")
    public List<Statement> processedStatements() {
        return new ArrayList<Statement>();
    }

    @Bean
    public FlatFileItemReader<Statement> csvReader() {
        return new FlatFileItemReaderBuilder<Statement>().name("csvStatementReader")
                .resource(new ClassPathResource("records.csv")).delimited().names(new String[] { "Reference",
                        "Account Number", "Description", "Start Balance", "Mutation", "End Balance" })
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Statement>() {
                    {
                        setTargetType(Statement.class);
                    }
                }).linesToSkip(1) // Skip header.
                .build();
    }

    @Bean
    public StaxEventItemReader<Statement> xmlReader() {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(Statement.class);
        unmarshaller.setCheckForXmlRootElement(true);
        return new StaxEventItemReaderBuilder<Statement>().name("xmlStatementReader")
                .resource(new ClassPathResource("records.xml")).addFragmentRootElements("record")
                .unmarshaller(unmarshaller).build();
    }

    @Bean
    public StatementProcessor processor() {
        return new StatementProcessor();
    }

    /**
     * Bean to create a job to start the processing of statements.
     * 
     * @param listener                 to listen to when the job is done so we can
     *                                 verify everything.
     * @param statementsProcessingStep the step to execute in this job.
     * @return a job object.
     */
    @Bean
    public Job statementsProcessingJob(JobCompletionNotificationListener listener, Step csvStatementsProcessingStep, Step xmlStatementsProcessingStep) {
        return jobBuilderFactory.get("statementsProcessingJob").incrementer(new RunIdIncrementer()).listener(listener)
                .flow(csvStatementsProcessingStep).next(xmlStatementsProcessingStep).end().build();
    }

    /**
     * Bean to create the processing step to be used in the job.
     * 
     * @return the step to be processed in the job.
     */
    @Bean
    public Step xmlStatementsProcessingStep() {
        return stepBuilderFactory.get("xmlStatementsProcessingStep").<Statement, Statement>chunk(10).reader(xmlReader())
                .processor(processor()).build();
    }

    @Bean
    public Step csvStatementsProcessingStep() {
        return stepBuilderFactory.get("csvStatementsProcessingStep").<Statement, Statement>chunk(10).reader(csvReader())
                .processor(processor()).build();
    }
}
