package com.rukevwe.invoicegenerator.data.repository;

import com.rukevwe.invoicegenerator.data.entity.Work;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WorkRepository extends CrudRepository<Work, Long> {
    Iterable<Work> findAll();
}
