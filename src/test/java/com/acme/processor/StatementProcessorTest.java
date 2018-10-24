package com.acme.processor;

import com.acme.model.ProcessingState;
import com.acme.model.Statement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


@RunWith(MockitoJUnitRunner.class)
public class StatementProcessorTest {

    private List<Statement> processedStatements;

    @InjectMocks
    private StatementProcessor statementProcessor;

    @Before
    public void setup() {
        processedStatements = new ArrayList<>();

        ReflectionTestUtils.setField(statementProcessor, "processedStatements", processedStatements);
    }

    @Test
    public void processOneCorrectStatement() {
        Statement statement = new Statement();
        statement.setReference(1000);
        statement.setDescription("test");
        statement.setStartBalance(new BigDecimal(10));
        statement.setMutation(new BigDecimal(10));
        statement.setEndBalance(new BigDecimal(20));
        Statement processedStatement = statementProcessor.process(statement);

        assertFalse(processedStatements.isEmpty());
        assertTrue(processedStatements.size() == 1);
        assertTrue(processedStatement.getState() == ProcessingState.CORRECT);
    }

    @Test
    public void processBadBalanceStatement() {
        Statement statement = new Statement();
        statement.setReference(1000);
        statement.setDescription("test");
        statement.setStartBalance(new BigDecimal(10));
        statement.setMutation(new BigDecimal(10));
        statement.setEndBalance(new BigDecimal(30));
        Statement processedStatement = statementProcessor.process(statement);

        assertFalse(processedStatements.isEmpty());
        assertTrue(processedStatements.size() == 1);
        assertTrue(processedStatement.getState() == ProcessingState.FAULT_WRONG_END_BALANCE);
    }

    @Test
    public void processBadBalanceNegativeMutationStatement() {
        Statement statement = new Statement();
        statement.setReference(1000);
        statement.setDescription("test");
        statement.setStartBalance(new BigDecimal(10));
        statement.setMutation(new BigDecimal(-10));
        statement.setEndBalance(new BigDecimal(20));
        Statement processedStatement = statementProcessor.process(statement);

        assertFalse(processedStatements.isEmpty());
        assertTrue(processedStatements.size() == 1);
        assertTrue(processedStatement.getState() == ProcessingState.FAULT_WRONG_END_BALANCE);
    }

    @Test
    public void processFaultyDuplicateReferenceStatement() {
        Statement statement = new Statement();
        statement.setReference(1000);
        statement.setDescription("test");
        statement.setStartBalance(new BigDecimal(10));
        statement.setMutation(new BigDecimal(10));
        statement.setEndBalance(new BigDecimal(20));

        Statement statement2 = new Statement();
        statement2.setReference(1000);
        statement2.setDescription("test");
        statement2.setStartBalance(new BigDecimal(10));
        statement2.setMutation(new BigDecimal(10));
        statement2.setEndBalance(new BigDecimal(20));

        Statement processedStatement = statementProcessor.process(statement);
        Statement processedStatement2 = statementProcessor.process(statement2);

        assertFalse(processedStatements.isEmpty());
        assertTrue(processedStatements.size() == 2);
        assertTrue(processedStatement.getState() == ProcessingState.FAULT_DUPLICATE_REFERENCE);
        assertTrue(processedStatement2.getState() == ProcessingState.FAULT_DUPLICATE_REFERENCE);
    }

    @Test
    public void processTwoCorrectStatements() {
        Statement statement = new Statement();
        statement.setReference(1000);
        statement.setDescription("test");
        statement.setStartBalance(new BigDecimal(10));
        statement.setMutation(new BigDecimal(10));
        statement.setEndBalance(new BigDecimal(20));

        Statement statement2 = new Statement();
        statement2.setReference(1001);
        statement2.setDescription("test");
        statement2.setStartBalance(new BigDecimal(10));
        statement2.setMutation(new BigDecimal(10));
        statement2.setEndBalance(new BigDecimal(20));

        Statement processedStatement = statementProcessor.process(statement);
        Statement processedStatement2 = statementProcessor.process(statement2);


        assertFalse(processedStatements.isEmpty());
        assertTrue(processedStatements.size() == 2);
        assertTrue(processedStatement.getState() == ProcessingState.CORRECT);
        assertTrue(processedStatement2.getState() == ProcessingState.CORRECT);
    }
}
