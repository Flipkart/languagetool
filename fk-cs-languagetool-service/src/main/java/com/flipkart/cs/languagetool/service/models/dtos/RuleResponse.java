package com.flipkart.cs.languagetool.service.models.dtos;

import lombok.Data;

/**
 * Created by anmol.kapoor on 03/01/17.
 */
@Data
public class RuleResponse {
    String id;
    String subId;
    String description;
    String issueType;
    RuleCategory category;
}
