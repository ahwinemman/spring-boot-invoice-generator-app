package com.rukevwe.invoicegenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CompanyResult {
    
    public String id;
    public String name;
    public Double totalAmount;
    
}
