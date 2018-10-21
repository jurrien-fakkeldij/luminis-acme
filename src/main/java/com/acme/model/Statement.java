package com.acme.model;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@XmlRootElement(name="record")
public class Statement {
    private Integer reference;

    @XmlAttribute(name="reference")
    public Integer getReference() {
        return this.reference;
    }

    public void setReference(Integer reference) {
        this.reference = reference;
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

    @Getter
@Setter
    private BigDecimal mutation;

    @Getter
    @Setter
    private BigDecimal endBalance;

    @Getter
    @Setter
    private ProcessingState state;
}
