package com.flipkart.cs.languagetool.service.models.dtos;

import com.flipkart.cs.languagetool.service.models.domain.RequestStatus;
import lombok.Data;

/**
 * Created by anmol.kapoor on 04/01/17.
 */
@Data
public class PhraseActionResponse {
    String phrase;
    RequestStatus requestStatus;
    Integer currentRequestCount;
    String message;

    public PhraseActionResponse() {
    }

    public PhraseActionResponse(String phrase, RequestStatus requestStatus, Integer currentRequestCount, String message) {
        this.phrase = phrase;
        this.requestStatus = requestStatus;
        this.currentRequestCount = currentRequestCount;
        this.message = message;
    }
}
