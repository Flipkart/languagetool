package com.flipkart.cs.languagetool.service.mappers;

import com.flipkart.cs.languagetool.service.models.domain.RequestedPhrase;
import com.flipkart.cs.languagetool.service.models.dtos.RequestedPhraseResponse;

/**
 * Created by anmol.kapoor on 05/01/17.
 */
public class MapperRequestedPhrase {
    public RequestedPhraseResponse toRequestedPhraseResponse(RequestedPhrase phrase, RequestedPhraseResponse response) {
        response.setPhrase(phrase.getPhrase());
        response.setCurrentStatus(phrase.getCurrentStatus());
        response.setRequestCount(phrase.getRequestCount());
        response.setModifiedAt(phrase.getModifiedAt());
        response.setModifiedByUser(phrase.getModifiedByUser());
        response.setModifiedBySystem(phrase.getModifiedBySystem());
        return response;
    }
}
