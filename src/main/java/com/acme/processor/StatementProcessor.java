package com.acme.processor;

import com.acme.model.Statement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to process incoming statement and return the output statement.
 */
@Slf4j
public class StatementProcessor implements ItemProcessor<Statement, Statement> {

    private List<Statement> processedStatements;
    private List<Statement> errorStatements;

    /**
     * Constructor to initialize all the local variables.
     */
    public StatementProcessor() {
        processedStatements =  new ArrayList<>();
        errorStatements = new ArrayList<>();
    }

    @Override
    public Statement process(final Statement statement) {

        log.info("Processing statements.");

        

        return new Statement();
    }
}
