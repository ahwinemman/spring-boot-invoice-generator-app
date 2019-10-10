package com.rukevwe.invoicegenerator.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InvoiceUtils {

    public static double calculateCost(double numberOfHours, int billableRate) {
        return numberOfHours * billableRate;
    }

    public static double getNumberofHoursWorked(String startTime, String endTime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date startDate = format.parse(startTime);
        Date endDate = format.parse(endTime);

        return (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60);
    }

}
