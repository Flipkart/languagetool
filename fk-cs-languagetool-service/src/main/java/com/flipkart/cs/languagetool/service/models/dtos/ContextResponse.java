package com.flipkart.cs.languagetool.service.models.dtos;

import lombok.Data;

/**
 * Created by anmol.kapoor on 03/01/17.
 */
@Data
public class ContextResponse {
    String text;
    Long offset;
    Long length;

    public ContextResponse() {
    }

    public ContextResponse(String text, Long offset, Long length) {
        this.text = text;
        this.offset = offset;
        this.length = length;
    }
}
