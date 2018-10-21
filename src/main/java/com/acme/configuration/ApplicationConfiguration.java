package com.acme.configuration;

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
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.Resource;

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

    private static final String FIELD_DELIMITER = ",";

    /**
     * Create a record for LOO limit records
     * @return the mapper
     */
    @Lazy
    @Bean(name = "looLimitRecordLineMapper")
    public LineMapper<Statement> lineMapper() {
        final DefaultLineMapper<Statement> lineMapper = new DefaultLineMapper<>();
        final BeanWrapperFieldSetMapper<Statement> beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        final DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(FIELD_DELIMITER);
        beanWrapperFieldSetMapper.setTargetType(Statement.class);
        tokenizer.setNames("Reference", "Account Number", "Description", "Start Balance", "Mutation", "End Balance");
        tokenizer.setDelimiter(FIELD_DELIMITER);
        tokenizer.setStrict(false);
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
        return lineMapper;
    }


    @Bean
    public FlatFileItemReader<Statement> csvReader() {
        return new FlatFileItemReaderBuilder<Statement>()
                .name("personItemReader")
                .resource(new ClassPathResource("records.csv"))
                .delimited()
                .names(new String[]{"Reference", "Account Number", "Description", "Start Balance", "Mutation", "End Balance"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Statement>() {{
                    setTargetType(Statement.class);
                }})
                .linesToSkip(1) //Skip header.
                .build();
    }

    ///TODO: Add xml reader.

    @Bean
    public StatementProcessor processor() {
        return new StatementProcessor();
    }


    /**
     * Bean to create a job to start the processing of statements.
     * @param listener to listen to when the job is done so we can verify everything.
     * @param statementsProcessingStep the step to execute in this job.
     * @return a job object.
     */
    @Bean
    public Job statementsProcessingJob(JobCompletionNotificationListener listener, Step statementsProcessingStep) {
        return jobBuilderFactory.get("statementsProcessingJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(statementsProcessingStep)
                .end()
                .build();
    }

    /**
     * Bean to create the processing step to be used in the job.
     * @return the step to be processed in the job.
     */
    @Bean
    public Step statementsProcessingStep() {
        return stepBuilderFactory.get("statementsProcessingStep")
                .<Statement, Statement> chunk(10)
                .reader(csvReader())
                .processor(processor())
                .build();
    }
    // end::jobstep[]
}
