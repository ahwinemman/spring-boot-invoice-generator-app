package com.rukevwe.invoicegenerator.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rukevwe.invoicegenerator.business.service.InvoiceService;
import com.rukevwe.invoicegenerator.model.CompanyResult;
import org.apache.poi.util.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(InvoiceController.class)
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    InvoiceService invoiceServiceMock;

    @Autowired
    ObjectMapper mapper = new ObjectMapper();
    
    public String invoiceId0 = "8fc1a8df-235d-47a2-8c04-f5433f67b256";
    public String invoiceId1 = "7abb6029-319a-4f5a-a2e8-79c9e3053828";
    public String invoiceId2 =  "06f873a7-2341-4eec-ae86-1f7721532d20";
    public String name0 = "Google";
    public String name1 = "Amazon";
    public String name2 = "Facebook";
    public double totalAmount0 = 2400;
    public double totalAmount1 = 1600;
    public double totalAmount2 = 1000;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void parseCsv_ValidCsvFile_ReturnListOfResults_ControllerTest() throws Exception {
        

        CompanyResult companyResult0 = new CompanyResult(invoiceId0, name0, totalAmount0);
        CompanyResult companyResult1 = new CompanyResult(invoiceId1, name1, totalAmount1);
        CompanyResult companyResult2 = new CompanyResult(invoiceId2, name2, totalAmount2);

        List<CompanyResult> companyResults = Arrays.asList(companyResult0, companyResult1, companyResult2);


        Mockito.when(invoiceServiceMock.parseCsv(ArgumentMatchers.any(MultipartFile.class))).thenReturn(companyResults);

        MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/invoice/parse-csv")
                                        .file(getCsvAsMultipartFile())
                                        .contentType(MediaType.MULTIPART_FORM_DATA))
                                        .andExpect(status().isOk())
                                        .andReturn();


        List<CompanyResult> mockMvcResponse = mapper.readValue(mockMvcResult.getResponse().getContentAsString(), 
               new TypeReference<List<CompanyResult>>() {});

        Assert.assertEquals(3, mockMvcResponse.size());
        Assert.assertTrue(companyResult0.equals(mockMvcResponse.get(0)));
        Assert.assertTrue(companyResult1.equals(mockMvcResponse.get(1)));
        Assert.assertTrue(companyResult2.equals(mockMvcResponse.get(2)));

        Mockito.verify(invoiceServiceMock, Mockito.times(1)).parseCsv(ArgumentMatchers.any(MultipartFile.class));
        Mockito.verifyNoMoreInteractions(invoiceServiceMock);

    }


    private MockMultipartFile getCsvAsMultipartFile() throws IOException    {
        File file = new File("src/test/resources/sample.csv");
        FileInputStream input = new FileInputStream(file);
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                file.getName(), "text/plain", IOUtils.toByteArray(input));
        return multipartFile;
    }
}
