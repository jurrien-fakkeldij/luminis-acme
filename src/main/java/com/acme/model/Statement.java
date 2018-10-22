package com.acme.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * Domain Model to represent a statement which are read from files.
 */
@ToString
@XmlRootElement(name="record")
public class Statement {
    private Integer reference;

    @Getter
    @Setter
    private BigDecimal mutation;

    @XmlAttribute(name="reference")
    public Integer getReference() {
        return reference;
    }

    @Getter
    @Setter
    private String accountNumber;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private BigDecimal startBalance;

    public void setReference(final Integer reference) {
        this.reference = reference;
    }

    @Getter
    @Setter
    private BigDecimal endBalance;

    @Getter
    @Setter
    private ProcessingState state;
}
