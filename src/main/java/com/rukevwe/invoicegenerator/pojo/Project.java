package com.rukevwe.invoicegenerator.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Project {
    private long employeeId;
    private double numberOfHours;
    private int unitPrice;
    private double cost;
}
