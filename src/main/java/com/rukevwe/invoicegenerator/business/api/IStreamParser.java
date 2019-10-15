package com.rukevwe.invoicegenerator.business.api;

import com.rukevwe.invoicegenerator.model.CompanyResult;
import com.rukevwe.invoicegenerator.model.InvoiceItem;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface IStreamParser {
    
    public List<CompanyResult> parseCsv(MultipartFile file) throws IOException, ParseException;

    public List<String> getPdfs();
    
    public List<InvoiceItem> getCompanyInvoiceItems(String invoiceId);

    void createPdfInvoices();
}
