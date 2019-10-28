package com.rukevwe.invoicegenerator.repository;

import com.rukevwe.invoicegenerator.business.NotFoundException;
import com.rukevwe.invoicegenerator.model.Company;
import com.rukevwe.invoicegenerator.model.InvoiceItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CsvInvoiceRepository implements InvoiceRepository {
    
    private Map<String, Company> cachedStore = new HashMap<>();
    
    @Override
    public void saveCompanies(String id, Company company) {
        company.setId(id);
        cachedStore.put(id, company);
    }

    @Override
    public Company findByInvoiceId(String id) {
        Company company = cachedStore.get(id);
        List<InvoiceItem> invoiceItemList = new ArrayList<>();
        if (company == null) {
            throw new NotFoundException("Invoice Id not found " +  id );
        }
        return company;
    }
    
    @Override
    public void clearCachedCompanies() {
        if (cachedStore != null || !cachedStore.isEmpty()) {
            cachedStore.clear();
        }
    }
}
