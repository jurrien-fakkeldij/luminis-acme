package com.acme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Class to run the statement processor application.
 */
@SpringBootApplication
public class Application {

    /**
     * Entry point for the application to start.
     * @param args arguments which can be given with the starting of the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}