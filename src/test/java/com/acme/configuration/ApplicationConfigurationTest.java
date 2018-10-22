package com.acme.configuration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationConfigurationTest {

    @InjectMocks
    ApplicationConfiguration applicationConfiguration;

    @Test
    public void testProcessorAvailable() {
        assertNotNull(applicationConfiguration.processor());
    }

}
