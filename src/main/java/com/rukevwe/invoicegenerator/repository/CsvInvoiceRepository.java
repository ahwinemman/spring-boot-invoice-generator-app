package com.rukevwe.invoicegenerator.repository;

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
        if (cachedStore != null || !cachedStore.isEmpty()) {
            cachedStore.clear();
        }
        cachedStore.put(id, company);
    }

    @Override
    public List<InvoiceItem> findByInvoiceId(String id) {
        Company company = cachedStore.get(id);
        List<InvoiceItem> invoiceItemList = new ArrayList<>();
        if (company != null) {
            company.getInvoiceItemList().forEach(invoiceItem -> {
                invoiceItemList.add(invoiceItem);
            });
        }
        return invoiceItemList;
    }
}
