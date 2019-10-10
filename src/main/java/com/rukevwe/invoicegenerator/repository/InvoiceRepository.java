package com.rukevwe.invoicegenerator.repository;

import com.rukevwe.invoicegenerator.model.Company;
import com.rukevwe.invoicegenerator.model.InvoiceItem;

import java.util.List;

public interface InvoiceRepository {


    /**
     * Persists the information retrieved from a particular file.
     * @param id String ID generated for the parse.
     * @param company Company model containing all the entries associated with a company
     */
    void saveCompanies(String id, Company company);


    /**
     * Retrieves
     * @param id String ID generated for the parse.
     * @return company Company model containing all the entries associated with a company
     */
    List<InvoiceItem> findByInvoiceId(String id);
}
