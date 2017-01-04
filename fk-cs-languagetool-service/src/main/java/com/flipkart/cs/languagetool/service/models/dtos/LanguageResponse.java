package com.flipkart.cs.languagetool.service.models.dtos;

import lombok.Data;

/**
 * Created by anmol.kapoor on 03/01/17.
 */
@Data
public class LanguageResponse {
    private String name;
    private String code;
    private String dictionary;

    public LanguageResponse(String name, String code, String dictionary) {
        this.name = name;
        this.code = code;
        this.dictionary = dictionary;
    }

    public LanguageResponse() {
    }
}
