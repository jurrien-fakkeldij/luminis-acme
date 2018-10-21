package com.acme.processor;

import java.util.List;

import javax.annotation.Resource;

import com.acme.model.ProcessingState;
import com.acme.model.Statement;

import org.springframework.batch.item.ItemProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * Class to process incoming statement and return the output statement.
 */
@Slf4j
public class StatementProcessor implements ItemProcessor<Statement, Statement> {

    @Resource
    private List<Statement> processedStatements;

    @Override
    public Statement process(final Statement statement) {

        log.info("Processing statements.");
        statement.setState(ProcessingState.CORRECT); //Start with assuming there is no issue.

        for(Statement processedStatement : processedStatements) {
            if(processedStatement.getReference().compareTo(statement.getReference()) == 0) {
                //If the references are the same, set both to be at fault.
                statement.setState(ProcessingState.FAULT_DUPLICATE_REFERENCE);
                processedStatement.setState(ProcessingState.FAULT_DUPLICATE_REFERENCE);
            }
        }

        if(statement.getStartBalance().add(statement.getMutation()).compareTo(statement.getEndBalance()) != 0) {
            //if the end balance is not correct set to the correct error state.
            statement.setState(ProcessingState.FAULT_WRONG_END_BALANCE);
        }

        processedStatements.add(statement);

        return statement;
    }
}
