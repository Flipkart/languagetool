package com.flipkart.cs.languagetool.service.models.dtos;

import lombok.Data;

import java.util.List;

/**
 * Created by anmol.kapoor on 05/01/17.
 */
@Data
public class Paginated<T> {
    List<T> results;
    Long total;
    Long count;
    OrderByParam orderBy;
    OrderSeq orderSeq;

    public Paginated() {
    }

    public Paginated(List<T> results, Long total, Long count) {
        this.results = results;
        this.total = total;
        this.count = count;
    }

}
