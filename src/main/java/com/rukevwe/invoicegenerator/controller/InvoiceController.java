package com.rukevwe.invoicegenerator.controller;

import com.rukevwe.invoicegenerator.business.service.InvoiceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping(value = "/invoice")
public class InvoiceController {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);
    
    @Autowired
    private InvoiceService invoiceService;

    
    @RequestMapping(value="/parse-csv", method = RequestMethod.POST)
    public ResponseEntity<Object> getCompanies(@RequestParam("file") MultipartFile csvFile, HttpServletResponse response) {
        
        try {
            return new ResponseEntity<>(invoiceService.parseCsv(csvFile), HttpStatus.OK);
        } catch (ParseException e) {
            return new ResponseEntity<>(Collections.singletonMap("errorMessage", e.getMessage() + " at line" + e.getErrorOffset()), HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(Collections.singletonMap("errorMessage", "Error parsing CSV file"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @RequestMapping(value="/company/{invoiceId}", method = RequestMethod.GET)
    public ResponseEntity<Object> getCompanyDetails(@PathVariable("invoiceId") String invoiceId) {
        return new ResponseEntity<>(invoiceService.getCompanyInvoiceItems(invoiceId), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/pdf-all", method = RequestMethod.GET)
    public void generatePdfs(HttpServletResponse response) {

        List<String> fileList = invoiceService.getPdfs();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=invoices.zip");
        response.setStatus(HttpServletResponse.SC_OK);

        try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
            for (String fil : fileList) {
                FileSystemResource resource = new FileSystemResource(fil);

                ZipEntry e = new ZipEntry(resource.getFilename());
                e.setSize(resource.contentLength());
                e.setTime(System.currentTimeMillis());
                zippedOut.putNextEntry(e);
                StreamUtils.copy(resource.getInputStream(), zippedOut);
                zippedOut.closeEntry();
            }
            zippedOut.finish();
        } catch (Exception e) {
            logger.error("Error occurred while bundling your files");
        }
    }
    
}
