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
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent the configuration of the applicaton.
 */
@Configuration
@EnableBatchProcessing
public class ApplicationConfiguration {
    /**
     * Factory resource to create the {@link Job} to read all the input files and process the {@link Statement}
     */
    @Resource
    public JobBuilderFactory jobBuilderFactory;

    /**
     * Factory resource to create the multiple {@link Step} objects to read all the input files and process the {@link Statement}
     */
    @Resource
    public StepBuilderFactory stepBuilderFactory;

    /**
     * Bean to create a list of {@link Statement} objects which can be used throughout the application.
     *
     * @return a {@link List} of {@link Statement}.
     */
    @Bean(name = "processedStatements")
    public List<Statement> processedStatements() {
        return new ArrayList<>();
    }

    /**
     * Bean to create a reader to read the csv file.
     *
     * @return a {@link FlatFileItemReader} to read a {@link Statement} from a csv file.
     */
    @Bean
    public FlatFileItemReader<Statement> csvReader() {
        BeanWrapperFieldSetMapper<Statement> statementBeanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper<>();
        statementBeanWrapperFieldSetMapper.setTargetType(Statement.class);

        return new FlatFileItemReaderBuilder<Statement>()
                .name("csvStatementReader")
                .resource(new ClassPathResource("records.csv"))
                .delimited()
                .names(new String[]{"Reference", "Account Number", "Description", "Start Balance", "Mutation", "End Balance"})
                .fieldSetMapper(statementBeanWrapperFieldSetMapper)
                .linesToSkip(1) // Skip header.
                .build();
    }

    /**
     * Bean to create a reader to read the xml file.
     *
     * @return a {@link StaxEventItemReader} to read a {@link Statement} from a xml file.
     */
    @Bean
    public StaxEventItemReader<Statement> xmlReader() {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(Statement.class);
        unmarshaller.setCheckForXmlRootElement(true);
        return new StaxEventItemReaderBuilder<Statement>()
                .name("xmlStatementReader")
                .resource(new ClassPathResource("records.xml"))
                .addFragmentRootElements("record")
                .unmarshaller(unmarshaller)
                .build();
    }

    /**
     * Bean to create a processor to process all the read {@link Statement} objects.
     *
     * @return a {@link StatementProcessor} to process all the read {@link Statement} objects.
     */
    @Bean
    public StatementProcessor processor() {
        return new StatementProcessor();
    }

    /**
     * Bean to create a job to start the processing of statements in two different steps.
     *
     * @param listener                    to listen to when the job is done so we can
     *                                    verify everything and output a report.
     * @param csvStatementsProcessingStep to read all the records from the csv file and process these.
     * @param xmlStatementsProcessingStep to read all the records from the xml file and process these.
     * @return a job object.
     */
    @Bean
    public Job statementsProcessingJob(final JobCompletionNotificationListener listener,
                                       final Step csvStatementsProcessingStep,
                                       final Step xmlStatementsProcessingStep) {
        return jobBuilderFactory.get("statementsProcessingJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(csvStatementsProcessingStep)
                .next(xmlStatementsProcessingStep)
                .end()
                .build();
    }

    /**
     * Bean to create the step to read from the xml file and process using the designated processor.
     *
     * @return the step to be used in the job.
     */
    @Bean
    public Step xmlStatementsProcessingStep() {
        return stepBuilderFactory.get("xmlStatementsProcessingStep")
                .<Statement, Statement>chunk(10)
                .reader(xmlReader())
                .processor(processor())
                .build();
    }

    /**
     * Bean to create the step to read from the csv file and process using the designated processor.
     *
     * @return the step to be used in the job.
     */
    @Bean
    public Step csvStatementsProcessingStep() {
        return stepBuilderFactory.get("csvStatementsProcessingStep")
                .<Statement, Statement>chunk(10)
                .reader(csvReader())
                .processor(processor())
                .build();
    }
}
