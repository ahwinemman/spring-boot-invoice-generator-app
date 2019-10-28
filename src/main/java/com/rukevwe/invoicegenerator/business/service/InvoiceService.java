package com.rukevwe.invoicegenerator.business.service;


import com.rukevwe.invoicegenerator.business.IStreamParser;
import com.rukevwe.invoicegenerator.model.Company;
import com.rukevwe.invoicegenerator.model.CompanyResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Service
public class InvoiceService {

    private IStreamParser iStreamParser;
    

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    @Autowired
    public InvoiceService(IStreamParser iStreamParser) {
        this.iStreamParser = iStreamParser;
    }

    public List<CompanyResult> parseCsv(MultipartFile csvFile) throws IOException, ParseException {
        return iStreamParser.parseCsv(csvFile);
    }
    
    public Company getCompanyInvoiceItems(String invoiceId) {
        return iStreamParser.getCompany(invoiceId);
    }
    
    public List<String> getPdfs() {
        return iStreamParser.getPdfs();
    }

    
}
