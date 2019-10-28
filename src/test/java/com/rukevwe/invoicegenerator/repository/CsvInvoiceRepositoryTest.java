package com.rukevwe.invoicegenerator.repository;

import com.rukevwe.invoicegenerator.model.Company;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CsvInvoiceRepositoryTest {

    public String invoiceId0 = "8fc1a8df-235d-47a2-8c04-f5433f67b256";
    public String invoiceId1 = "7abb6029-319a-4f5a-a2e8-79c9e3053828";
    public String invoiceId2 =  "06f873a7-2341-4eec-ae86-1f7721532d20";

    @Autowired
    public CsvInvoiceRepository invoiceRepository;

    @Before
    public void setUp() {
        
    }

    @Test
    public void saveCompaniesAndFindByInvoiceId_ComanyAndInvoiceId_ReturnSavedCompanyObjectWhenSearchedByInvoiceId() {


        Company company0 = new Company();
        company0.setId(invoiceId0);
        company0.setName("Google");
        company0.setInvoiceItemList(new ArrayList<>());

        Company company1 = new Company();
        company0.setId(invoiceId1);
        company0.setName("Facebook");
        company0.setInvoiceItemList(new ArrayList<>());

        Company company2 = new Company();
        company0.setId(invoiceId2);
        company0.setName("Amazon");
        company0.setInvoiceItemList(new ArrayList<>());


        invoiceRepository.saveCompanies(invoiceId0, company0); 
        Assert.assertEquals(company0,invoiceRepository.findByInvoiceId(invoiceId0));

        invoiceRepository.saveCompanies(invoiceId1, company1);
        Assert.assertEquals(company1,invoiceRepository.findByInvoiceId(invoiceId1));

        invoiceRepository.saveCompanies(invoiceId2, company2);
        Assert.assertEquals(company2,invoiceRepository.findByInvoiceId(invoiceId2));
        

    }
}
