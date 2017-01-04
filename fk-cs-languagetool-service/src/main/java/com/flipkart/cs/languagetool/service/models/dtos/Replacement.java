package com.flipkart.cs.languagetool.service.models.dtos;

import lombok.Data;

/**
 * Created by anmol.kapoor on 03/01/17.
 */
@Data
public class Replacement {
    String value;

    public Replacement(String value) {
        this.value = value;
    }

    public Replacement() {
    }
}
