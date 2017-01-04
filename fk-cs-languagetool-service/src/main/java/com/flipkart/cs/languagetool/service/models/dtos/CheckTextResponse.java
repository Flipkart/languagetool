package com.flipkart.cs.languagetool.service.models.dtos;

import lombok.Data;

import java.util.List;

/**
 * Created by anmol.kapoor on 03/01/17.
 */
@Data
public class CheckTextResponse {
    private LanguageResponse language;
    private List<MatchResponse> matches;
}
