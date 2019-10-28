package com.rukevwe.invoicegenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResult {
    
    public String id;
    public String name;
    public Double totalAmount;
    
    @Override
    public boolean equals (Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
 
        return ((o instanceof CompanyResult) &&
                ((CompanyResult) o).getName().equals(this.getName()) &&
                ((CompanyResult) o).getTotalAmount().equals(this.getTotalAmount()));
        
    }
    
    @Override
    public int hashCode() {
        return Integer.valueOf(this.id.substring(0, 5));
    }
    
}
