package com.rukevwe.invoicegenerator.business.api;

import com.rukevwe.invoicegenerator.model.Company;
import com.rukevwe.invoicegenerator.model.CompanyResult;
import com.rukevwe.invoicegenerator.model.InvoiceItem;
import com.rukevwe.invoicegenerator.repository.InvoiceRepository;
import com.rukevwe.invoicegenerator.utils.InvoiceUtils;
import com.rukevwe.invoicegenerator.utils.PdfUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CsvStreamParser implements IStreamParser {

    private VelocityEngine velocityEngine;
    private InvoiceRepository invoiceRepository;


    private static int EMPLOYEE_ID_INDEX = 0;
    private static int BILLABLE_RATE_INDEX = 1;
    private static int PROJECT_INDEX = 2;
    private static int START_TIME_INDEX = 4;
    private static int END_TIME_INDEX = 5;
    
    private static Map<String, Company> companyNameMapping;

    private static Map<Integer, List<InvoiceItem>> invoiceItemList;

    List<String> fileNames = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(CsvStreamParser.class);

    @Autowired
    public CsvStreamParser(VelocityEngine velocityEngine, InvoiceRepository invoiceRepository) {
        this.velocityEngine = velocityEngine;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public List<CompanyResult> parseCsv(MultipartFile csvFile) throws IOException, ParseException {

        List<CompanyResult> companyResultList = new ArrayList<>();
        companyNameMapping = new HashMap<>();
        InputStreamReader input = new InputStreamReader(csvFile.getInputStream());
        CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().parse(input);
        for (CSVRecord csvRecord : csvParser) {
            String employeeId = csvRecord.get(EMPLOYEE_ID_INDEX);
            int billableRate = Integer.valueOf(csvRecord.get(BILLABLE_RATE_INDEX));
            String companyName = csvRecord.get(PROJECT_INDEX);
            String startTime = csvRecord.get(START_TIME_INDEX) + ":00";
            String endTime = csvRecord.get(END_TIME_INDEX) + ":00";
            double numberOfHours = InvoiceUtils.getNumberofHoursWorked(startTime, endTime);
            if (numberOfHours < 0) {
                throw new ParseException("Error parsing the CSV file", (int) csvRecord.getRecordNumber());
            }
            double cost = InvoiceUtils.calculateCost(numberOfHours, billableRate);

            InvoiceItem invoiceItem = buildInvoiceItem(companyName, employeeId, numberOfHours, billableRate, cost);

            companyNameMapping = buildCompanyPojo(companyNameMapping, companyName, invoiceItem);
        }

        companyNameMapping.forEach((name, company) -> {
            String companyId = UUID.randomUUID().toString();
            invoiceRepository.saveCompanies(companyId, company);
            CompanyResult companyResult = new CompanyResult(companyId, company.getName(), company.getTotalAmount());
            companyResultList.add(companyResult);
        });

        return companyResultList;
    }

    @Override
    public List<InvoiceItem> getCompanyInvoiceItems(String invoiceId) {
        return invoiceRepository.findByInvoiceId(invoiceId);
    }


    private Map<String, Company> buildCompanyPojo(Map<String, Company> companyMappings, String companyName, InvoiceItem invoiceItem) {

        if (companyMappings.containsKey(companyName)) {
            Company company = companyMappings.get(companyName);
            company.getInvoiceItemList().add(invoiceItem);
            companyMappings.put(companyName, company);
        } else {
            Company company = new Company();
            company.setName(companyName);
            company.getInvoiceItemList().add(invoiceItem);
            companyMappings.put(companyName, company);
        }
        return companyMappings;
    }

    private InvoiceItem buildInvoiceItem(String companyName, String employeeId, double numberOfHours, int billableRate, double cost) {
        InvoiceItem invoiceItem = new InvoiceItem();
        invoiceItem.setName(companyName);
        invoiceItem.setEmployeeId(employeeId);
        invoiceItem.setNumberOfHours(numberOfHours);
        invoiceItem.setUnitPrice(billableRate);
        invoiceItem.setCost(cost);

        return invoiceItem;
    }

    @Override
    public List<String> getPdfs() {
        createPdfInvoices();
        return fileNames;
    }


    @Override
    public void createPdfInvoices() {
        Map<String, List<InvoiceItem>> mappings = new HashMap<>();
        companyNameMapping.forEach((id, company) ->
                mappings.put(company.getName(), company.getInvoiceItemList())
        );
        StringBuilder content = new StringBuilder();
        VelocityContext velocityContext = new VelocityContext();
        double totalCost;

        for (String pName : mappings.keySet()) {
            totalCost = 0.0;
            for (InvoiceItem invoiceItem : mappings.get(pName)) {
                content.append("<tr class= \"info-row\">")
                        .append("<td>" + invoiceItem.getEmployeeId() + "</td>")
                        .append("<td>" + invoiceItem.getNumberOfHours() + "</td>")
                        .append("<td>" + invoiceItem.getUnitPrice() + "</td>")
                        .append("<td>" + invoiceItem.getCost() + "</td></tr>" + "");
                totalCost += invoiceItem.getCost();
            }
            content.append("<tr class= \"total-row\">")
                    .append("<td></td>")
                    .append("<td></td>")
                    .append("<td>Total</td>")
                    .append("<td>" + totalCost + "</td></tr>");

            velocityContext.put("project", pName);
            velocityContext.put("body", content);
            StringWriter stringWriter = new StringWriter();
            velocityEngine.mergeTemplate("/templates/invoice-template.vm", "UTF-8", velocityContext, stringWriter);
            String text = stringWriter.toString();
            PdfUtils pdfUtils = PdfUtils.getInstance();
            try {
                String tDir = System.getProperty("java.io.tmpdir");
                String path = tDir + pName + ".pdf";
                File file = new File(path);
                file.createNewFile();
                pdfUtils.createPdfFile(text, path);
                fileNames.add(path);
                content = new StringBuilder();
            } catch (IOException e) {
                logger.error("Error creating pdf file");
            }

        }
    }

}
