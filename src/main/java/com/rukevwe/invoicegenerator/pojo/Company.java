package com.rukevwe.invoicegenerator.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Company {
    
    private int id;
    private String projectName;
    private List<Project> projectList = new ArrayList<>();
    private double projectTotalAmount;
    
    public Company(String projectName) {
        this.projectName = projectName;
    }
    public Company(String projectName, double projectTotalAmount) {
        this.projectName = projectName;
        this.projectTotalAmount = projectTotalAmount;
    }

}
