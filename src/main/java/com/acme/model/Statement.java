package com.acme.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Domain model class for the statements which need to be parsed.
 */
@Getter
@Setter
public class Statement {
    private Integer reference;
    private String accountNumber;
    private String description;
    private BigDecimal startBalance;
    private BigDecimal mutation;
    private BigDecimal endBalance;
}
