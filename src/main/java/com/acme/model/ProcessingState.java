package com.acme.model;

/**
 * Enumeration of possible states for a {@link Statement}
 */
public enum ProcessingState {
    /**
     * Representing a state where there are multiple references.
     */
    FAULT_DUPLICATE_REFERENCE ("Duplicate reference"),

    /**
     * Representing a state where the end balance doesn't match the outcome of the start balance + the mutation.
     */
    FAULT_WRONG_END_BALANCE ("End balance not correct"),

    /**
     * Representing a state where the validations are all met and the {@link Statement} is validated.
     */
    CORRECT ("Correct statement");

    private final String name;       

    /**
     * Constructor to add name for string representation.
     * @param name the string representation for the enum.
     */
    private ProcessingState(String name) {
        this.name = name;
    }

    /**
     * Function to return the string representation of the state.
     * @return returns the string representation of the {@link State}
     */
    public String toString() {
        return this.name;
     }
}