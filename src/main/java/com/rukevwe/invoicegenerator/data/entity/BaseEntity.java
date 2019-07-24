package com.rukevwe.invoicegenerator.data.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.ZonedDateTime;

@MappedSuperclass
public abstract class BaseEntity  implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false, insertable = true, updatable = false)
    private Long id;

    @Column(name = "DATE_CREATED", nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime dateCreated = ZonedDateTime.now();


}
