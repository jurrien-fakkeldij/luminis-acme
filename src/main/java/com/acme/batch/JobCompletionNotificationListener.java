package com.acme.batch;

import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import com.acme.model.ProcessingState;
import com.acme.model.Statement;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

/**
 * Component in the application to listen to when the job has finished so we can generate an output on the gathered data.
 */
@Slf4j
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    @Resource
    private List<Statement> processedStatements;

    FileWriter outputWriter;

    private static String[] CSV_HEADER = { "Reference", "Description" };
    private static String DELIMITER = ",";
    private static String END_LINE = "\n";

    public JobCompletionNotificationListener() throws IOException {
        outputWriter = new FileWriter("report.csv");
        for(String header : CSV_HEADER) {
            outputWriter.write(header);
            outputWriter.write(DELIMITER);
        }
        outputWriter.write(END_LINE);
    }

    @Override
    public void afterJob(final JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            for(Statement statement : processedStatements) {
                if(statement.getState() != ProcessingState.CORRECT) {
                    try {
                        outputWriter.write(statement.getReference().toString());
                        outputWriter.write(DELIMITER);
                        outputWriter.write(statement.getDescription().toString());
                        outputWriter.write(END_LINE);
                    } catch (IOException e) {
                        log.info("Writing writer exception: ", e);
                    }
                }
                log.info(statement.toString());
            }

            try {
                outputWriter.flush();
            } catch (IOException e) {
                log.info("Flushing writer exception: ", e);
            }
        }
    }
}