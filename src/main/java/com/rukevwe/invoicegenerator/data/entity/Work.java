package com.rukevwe.invoicegenerator.data.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Date;

@Entity
@Table(name = "WORK")
@Getter
@Setter
public class Work extends BaseEntity {
    @Column(name = "PROJECT")
    private String project;
    @Column(name = "DATE")
    private Date date;
    @Column(name = "START_TIME")
    private Double startTime;
    @Column(name = "END_TIME")
    private Double endTime;
    @Column(name = "EMPLOYEE_ID")
    @NotNull(message = "Employee information is required")
    private long employeeId;
}
