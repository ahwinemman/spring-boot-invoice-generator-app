package com.rukevwe.invoicegenerator.business;

import com.rukevwe.invoicegenerator.model.Company;
import com.rukevwe.invoicegenerator.model.CompanyResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface IStreamParser {
    
    public List<CompanyResult> parseCsv(MultipartFile file) throws IOException, ParseException;

    public List<String> getPdfs();
    
    public Company getCompany(String invoiceId);

    void createPdfInvoices();
}
