package com.rukevwe.invoicegenerator.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Company {
    
    private String id;
    private String name;
    private List<InvoiceItem> invoiceItemList = new ArrayList<>();
    
    public Company(String name) {
        this.name = name;
    }
    
    public Double getTotalAmount() {
        Double totalAmount = 0.0;
        if (invoiceItemList != null && !invoiceItemList.isEmpty()) {
            for (InvoiceItem invoiceItem: invoiceItemList) {
                totalAmount += invoiceItem.getCost();
            }
        }
        return totalAmount;
    }
}
