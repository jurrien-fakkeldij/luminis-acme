package com.acme.model;

/**
 * Enumeration of possible states for a {@link Statement}
 */
public enum ProcessingState {
    /**
     * Representing a state where there are multiple references.
     */
    FAULT_DUPLICATE_REFERENCE,

    /**
     * Representing a state where the end balance doesn't match the outcome of the start balance + the mutation.
     */
    FAULT_WRONG_END_BALANCE,

    /**
     * Representing a state where the validations are all met and the {@link Statement} is validated.
     */
    CORRECT
}