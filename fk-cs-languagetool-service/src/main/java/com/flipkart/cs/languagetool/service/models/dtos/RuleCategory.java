package com.flipkart.cs.languagetool.service.models.dtos;

import lombok.Data;

/**
 * Created by anmol.kapoor on 03/01/17.
 */
@Data
public class RuleCategory {
    String id;
    String name;

    public RuleCategory(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public RuleCategory() {
    }
}

