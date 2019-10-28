package com.rukevwe.invoicegenerator.business.api;

import com.rukevwe.invoicegenerator.business.CsvStreamParser;
import com.rukevwe.invoicegenerator.model.CompanyResult;
import com.rukevwe.invoicegenerator.repository.CsvInvoiceRepository;
import org.apache.poi.util.IOUtils;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CsvStreamParserTest {

    @Autowired
    public CsvStreamParser csvStreamParser;

    @Autowired
    public CsvInvoiceRepository invoiceRepository;

    @Autowired
    public VelocityEngine velocityEngine;    

    @Before
    public void setUp() {
        
    }

    @Test
    public void parseCsv_ValidLocalCsvFile_ReturnParsedResult() throws IOException, ParseException {

        File file = new File("src/test/resources/sample.csv");
        FileInputStream input = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));

        Assert.assertNotNull(multipartFile);
        List<CompanyResult> companyResults =  csvStreamParser.parseCsv(multipartFile);


        Assert.assertEquals(3, companyResults.size());
        Assert.assertEquals("Google", companyResults.get(0).getName());
        Assert.assertEquals(Double.valueOf(2400), companyResults.get(0).getTotalAmount());
        Assert.assertEquals("Amazon", companyResults.get(1).getName());
        Assert.assertEquals(Double.valueOf(1600), companyResults.get(1).getTotalAmount());
        Assert.assertEquals("Facebook", companyResults.get(2).getName());
        Assert.assertEquals(Double.valueOf(1000), companyResults.get(2).getTotalAmount());

    }


}
