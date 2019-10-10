package com.rukevwe.invoicegenerator.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceItem {
    private String name;
    private String employeeId;
    private double numberOfHours;
    private int unitPrice;
    private double cost;
}
