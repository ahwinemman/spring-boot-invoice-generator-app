package com.rukevwe.invoicegenerator.business.service;


import com.rukevwe.invoicegenerator.business.utils.PdfUtils;
import com.rukevwe.invoicegenerator.data.entity.Work;
import com.rukevwe.invoicegenerator.data.repository.WorkRepository;
import com.rukevwe.invoicegenerator.pojo.Project;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceService {

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private WorkRepository workRepository;

    public static int numberOfHoursInADay = 24;
    
    List<String> fileNames = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    public List<String> generate(MultipartFile file) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            Map<String, List<Project>> projectMappings = new HashMap<>();

            projectMappings = parseWorksheet(worksheet);
            createPdfInvoices(projectMappings);
        } catch (IOException e) {
            logger.error("Error retrieving excel file");
        }
        return fileNames;
    }

    private void createPdfInvoices(Map<String, List<Project>> projectMappings) {
        StringBuilder content = new StringBuilder();
        VelocityContext velocityContext = new VelocityContext();
        double totalCost;
        
        for (String pName : projectMappings.keySet()) {
            totalCost = 0.0;
            for (Project proj : projectMappings.get(pName)) {
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


    private Map<String, List<Project>> parseWorksheet(XSSFSheet worksheet) {
        String projectName;
        Map<String, List<Project>> projectMappings = new HashMap<>();

        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = worksheet.getRow(i);

            long employeeId = (long) row.getCell(0).getNumericCellValue();
            int billableRate = (int) row.getCell(1).getNumericCellValue();
            projectName = row.getCell(2).getStringCellValue();
            Date date = row.getCell(3).getDateCellValue();
            double startTime = (double) row.getCell(4).getNumericCellValue();
            double endTime = (double) row.getCell(5).getNumericCellValue();

            saveWork(employeeId, billableRate, projectName, date, startTime, endTime);
            double numberOfHours = calculateNumberOfHours(startTime, endTime);
            double cost = calculateCost(numberOfHours, billableRate);

            Project project = buildProjectPojo(employeeId, numberOfHours, billableRate, cost);
            List<Project> projectList = new ArrayList<>();
            projectList.add(project);

            if (projectMappings.containsKey(projectName)) {
                projectMappings.get(projectName).add(project);
            } else {
                projectMappings.put(projectName, projectList);
            }

        }
        return projectMappings;
    }

    private Project buildProjectPojo(long employeeId, double numberOfHours, int billableRate, double cost) {
        Project project = new Project();
        project.setEmployeeId(employeeId);
        project.setNumberOfHours(numberOfHours);
        project.setUnitPrice(billableRate);
        project.setCost(cost);
        return project;
    }
    
    private void saveWork(long employeeId, int billableRate, String projectName, Date date, double startTime, double endTime) {
        Work work = new Work();
        work.setEmployeeId(employeeId);
        work.setProject(projectName);
        work.setDate(new java.sql.Date(date.getTime()));
        work.setStartTime(startTime);
        work.setEndTime(endTime);
        workRepository.save(work);
    }

    private double calculateCost(double numberOfHours, int billableRate) {
        return numberOfHours * billableRate;
    }

    private double calculateNumberOfHours(double startTime, double endTime) {
        double timeDifference = endTime - startTime;
        double numberOfHours = Math.round(timeDifference * numberOfHoursInADay * 10) / 10.0;
        return numberOfHours;
    }
}
