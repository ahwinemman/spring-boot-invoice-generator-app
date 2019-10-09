package com.rukevwe.invoicegenerator.business.service;


import com.rukevwe.invoicegenerator.business.utils.PdfUtils;
import com.rukevwe.invoicegenerator.pojo.Company;
import com.rukevwe.invoicegenerator.pojo.Project;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceService {

    private VelocityEngine velocityEngine;
    

    private static int EMPLOYEE_ID_INDEX = 0;
    private static int BILLABLE_RATE_INDEX = 1;
    private static int PROJECT_INDEX = 2;
    private static int START_TIME_INDEX = 4;
    private static int END_TIME_INDEX = 5;
    private int id = 1;

    private static List<Company> cachedCompanyDetails = new ArrayList<>();
    private static Map<String, Double> companyNamesAndTotalAmounts = new HashMap<>();
    private static Map<String, Company> companyMappings = new HashMap<>();
    
    List<String> fileNames = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);
    
   

    @Autowired
    public InvoiceService(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }


    public  Map<String, Double> parseCsv(MultipartFile csvFile) throws IOException, ParseException {
        
        String companyName = "";

        InputStreamReader input = new InputStreamReader(csvFile.getInputStream());
        CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().parse(input);
        for (CSVRecord csvRecord : csvParser) {
            String employeeId = csvRecord.get(EMPLOYEE_ID_INDEX);
            int billableRate = Integer.valueOf(csvRecord.get(BILLABLE_RATE_INDEX));
            companyName = csvRecord.get(PROJECT_INDEX);
            String startTime = csvRecord.get(START_TIME_INDEX) + ":00";
            String endTime = csvRecord.get(END_TIME_INDEX) + ":00";
            double numberOfHours = getNumberofHoursWorked(startTime, endTime);
            if (numberOfHours < 0) {
                throw new ParseException("Error parsing the CSV file", (int) csvRecord.getRecordNumber());
            }
            double cost = calculateCost(numberOfHours, billableRate);

            Project project = buildProjectPojo(employeeId, numberOfHours, billableRate, cost);

            companyMappings = buildCompanyPojo(companyMappings, companyName, project, project.getCost());
        }

        cachedCompanyDetails = new ArrayList<>(companyMappings.values());
        cachedCompanyDetails.forEach(company -> 
            companyNamesAndTotalAmounts.put(company.getProjectName(), company.getProjectTotalAmount())
        );
        return companyNamesAndTotalAmounts;
    }

    public List<Project> getcompanyProjects(String invoiceId) {
        Company company = companyMappings.get(invoiceId);
        List<Project> projectList = company.getProjectList();
        return projectList;
    }
    
    public Map<String, Double> getCompanies() {
        return companyNamesAndTotalAmounts;
    }
    
    
    private Map<String, Company> buildCompanyPojo(Map<String, Company> companyMappings, String companyName, Project project, double currentAmount) {

        if (companyMappings.containsKey(companyName)) {
            Company company = companyMappings.get(companyName);
            double currentTotalAmount = company.getProjectTotalAmount() + currentAmount;
            company.setProjectTotalAmount(currentTotalAmount);
            company.getProjectList().add(project);
            companyMappings.put(String.valueOf(id), company);
        } else {
            Company company = new Company(companyName);
            company.getProjectList().add(project);
            company.setProjectTotalAmount(currentAmount);
            company.setId(id);
            companyMappings.put(String.valueOf(id), company);
            id += 1;
        }
        return companyMappings;
    }

    private Project buildProjectPojo(String employeeId, double numberOfHours, int billableRate, double cost) {
        Project project = new Project();
        project.setEmployeeId(employeeId);
        project.setNumberOfHours(numberOfHours);
        project.setUnitPrice(billableRate);
        project.setCost(cost);

        return project;
    }

    public List<String> getPdfs() {
        createPdfInvoices();
        return fileNames;
    }

    private void createPdfInvoices() {
        Map<String, List<Project>> mappings = new HashMap<>();
        cachedCompanyDetails.forEach(company ->
                mappings.put(company.getProjectName(), company.getProjectList())
        );
        StringBuilder content = new StringBuilder();
        VelocityContext velocityContext = new VelocityContext();
        double totalCost;

        for (String pName : mappings.keySet()) {
            totalCost = 0.0;
            for (Project proj : mappings.get(pName)) {
                content.append("<tr class= \"info-row\">")
                        .append("<td>" + proj.getEmployeeId() + "</td>")
                        .append("<td>" + proj.getNumberOfHours() + "</td>")
                        .append("<td>" + proj.getUnitPrice() + "</td>")
                        .append("<td>" + proj.getCost() + "</td></tr>" + "");
                totalCost += proj.getCost();
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


    private double calculateCost(double numberOfHours, int billableRate) {
        return numberOfHours * billableRate;
    }

    private double getNumberofHoursWorked(String startTime, String endTime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date startDate = format.parse(startTime);
        Date endDate = format.parse(endTime);

        return (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60);
    }
}
