package com.rukevwe.invoicegenerator.business.utils;

import com.lowagie.text.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Component
public class PdfUtils {

    private static final Logger log = LoggerFactory.getLogger(PdfUtils.class);

    private static PdfUtils pdfUtils;

    private PdfUtils() {

    }

    public static synchronized PdfUtils getInstance() {
        if (pdfUtils == null) {
            pdfUtils = new PdfUtils();
        }
        return pdfUtils;
    }

    public void createPdfFile(String content, String pdfPath) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(pdfPath));
            content = content.replace("&", "&amp;");
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(content);
            renderer.layout();
            renderer.createPDF(os);
            os.close();
            os = null;
        } catch (IOException | DocumentException e) {
            log.error("Error  " + e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("Error  " + e.getMessage());
                }
            }
        }

    }

    public void createPdfFile(String[] content, String pdfPath) {

        log.info("content length====" + content.length);
        OutputStream os = null;
        final File outputFile = new File(pdfPath);
        try {
            os = new FileOutputStream(outputFile);
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(content[0].replace("&", "&amp;"));
            renderer.layout();
            renderer.createPDF(os, false);
            for (int i = 1; i < content.length; i++) {
                renderer.setDocumentFromString(content[i].replace("&", "&amp;"));
                renderer.layout();
                renderer.writeNextDocument(i);
            }
            renderer.finishPDF();

            log.info("Sample file with " + content.length + " documents rendered as PDF to " + outputFile);

        } catch (IOException | DocumentException e) {
            log.error("Error 1111  " + e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    log.error("Err  " + e.getMessage());
                }
            }
        }

    }

}
