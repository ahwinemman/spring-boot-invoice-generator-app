package com.rukevwe.invoicegenerator.repository;

import com.rukevwe.invoicegenerator.model.Company;

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
    Company findByInvoiceId(String id);


    /**
     * Clears the cached list of companies
     */
    void clearCachedCompanies();
}
