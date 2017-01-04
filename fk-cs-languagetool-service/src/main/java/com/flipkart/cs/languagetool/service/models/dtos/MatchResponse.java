package com.flipkart.cs.languagetool.service.models.dtos;

import lombok.Data;

import java.util.List;

/**
 * Created by anmol.kapoor on 03/01/17.
 */
@Data
public class MatchResponse {
    ContextResponse context;
    List<Replacement> replacements;
    RuleResponse rule;
    String message;
    String shortMessage;
    Integer offset;
    Integer length;


}
