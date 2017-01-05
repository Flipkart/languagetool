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
    boolean saveRequired = false;
    boolean cacheReloadRequired = false;

    public PhraseActionResponse() {
    }

    public PhraseActionResponse(String phrase, RequestStatus requestStatus, Integer currentRequestCount, String message, boolean isSaveRequired , boolean isCacheReloadRequired) {
        this.phrase = phrase;
        this.requestStatus = requestStatus;
        this.currentRequestCount = currentRequestCount;
        this.message = message;
        this.saveRequired = isSaveRequired;
        this.cacheReloadRequired = isCacheReloadRequired;
    }
}
