package com.flipkart.cs.languagetool.service;

import com.flipkart.cs.languagetool.service.cache.ValidationCache;
import com.flipkart.cs.languagetool.service.exception.ApiException;
import com.flipkart.cs.languagetool.service.models.dao.RegisteredDictionaryDao;
import com.flipkart.cs.languagetool.service.models.dao.RequestedPhraseDao;
import com.flipkart.cs.languagetool.service.models.domain.RegisteredDictionary;
import com.flipkart.cs.languagetool.service.models.domain.RequestStatus;
import com.flipkart.cs.languagetool.service.models.domain.RequestedPhrase;
import com.flipkart.cs.languagetool.service.models.dtos.PhraseActionResponse;
import com.flipkart.cs.languagetool.service.models.dtos.PhraseRequest;
import com.flipkart.cs.languagetool.service.models.dtos.RequestHeaders;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import javax.annotation.Nullable;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by anmol.kapoor on 02/01/17.
 */
@Singleton
@Slf4j
public class LanguageToolService {

    private final RequestedPhraseDao requestedPhraseDao;
    private final int LOAD_COUNT = 100;

    private final ValidationCache validationCache;


    private final RegisteredDictionaryDao registeredDictionaryDao;

    @Inject
    public LanguageToolService(RequestedPhraseDao requestedPhraseDao, ValidationCache validationCache, RegisteredDictionaryDao registeredDictionaryDao) {
        this.requestedPhraseDao = requestedPhraseDao;
        this.validationCache = validationCache;
        this.registeredDictionaryDao = registeredDictionaryDao;
    }

    public Set<String> getApprovedWords() throws ApiException {
        Optional<RegisteredDictionary> possibleDictionary = registeredDictionaryDao.findById(RequestHeaders.get().getDictionary());
        if (!possibleDictionary.isPresent()) {
            throw new ApiException(Response.Status.BAD_REQUEST, "Unable to find dictionary : " + RequestHeaders.get().getDictionary());
        }
        return getApprovedWords(possibleDictionary.get());
    }

    public Set<String> getApprovedWords(RegisteredDictionary dictionary) throws ApiException {
        log.info("Getting approved words for : " + dictionary.getShortCode());
        return validationCache.validateAndGetApprovedWords(dictionary);
    }

    public Set<String> getBlacklistedWords(RegisteredDictionary dictionary) throws ApiException {
        log.info("Getting blacklisted words for : " + dictionary.getShortCode());
        return validationCache.validateAndGetBlacklistedWords(dictionary);
    }

    public Set<String> getBlacklistedWords() throws ApiException {

        Optional<RegisteredDictionary> possibleDictionary = registeredDictionaryDao.findById(RequestHeaders.get().getDictionary());
        if (!possibleDictionary.isPresent()) {
            throw new ApiException(Response.Status.BAD_REQUEST, "Unable to find dictionary : " + RequestHeaders.get().getDictionary());
        }
        return getBlacklistedWords(possibleDictionary.get());
    }

    public boolean refreshCache() {
        return true;
    }

    public List<PhraseActionResponse> bulkRequestActionOnPhrases(PhraseRequest phraseRequest, RegisteredDictionary dictionary) {
        RequestStatus requestedStatus = phraseRequest.getStatus();
        Map<String, RequestedPhrase> requestedPhrasesInDb = findIterativelyByLoadCount(phraseRequest.getPhrases());
        /// moving to requested statuses for each of them..
        //// iterating and finding in map... if found.. status change stuff.. or not found. just add new one

        List<PhraseActionResponse> responseList = new ArrayList<>();
        boolean isCacheReloadRequired = false;
        for (String phraseRequested : phraseRequest.getPhrases()) {
            RequestedPhrase requestedPhrase = null;
            PhraseActionResponse response = null;
            if (requestedPhrasesInDb.containsKey(phraseRequested)) {
                requestedPhrase = requestedPhrasesInDb.get(phraseRequested);
                response = changeStateToRequestedState(requestedPhrase, requestedStatus);
            } else {
                requestedPhrase = new RequestedPhrase(phraseRequested, requestedStatus, dictionary);
                dictionary.getRequestedPhraseList().add(requestedPhrase);
                response = new PhraseActionResponse(requestedPhrase.getPhrase(), requestedPhrase.getCurrentStatus(), requestedPhrase.getRequestCount(), "Created new Phrase in state : " + requestedPhrase.getCurrentStatus(), true, true);
            }
            if (response.isSaveRequired()) {
                requestedPhrase = requestedPhraseDao.save(requestedPhrase);
            }
            if (response.isCacheReloadRequired()) {
                isCacheReloadRequired = true;
            }
            responseList.add(response);
        }
        if (isCacheReloadRequired) {
            dictionary.setCurrentVersionCreatedAt(DateTime.now());
        }
        return responseList;
    }

    private PhraseActionResponse changeStateToRequestedState(RequestedPhrase requestedPhrase, RequestStatus requestedStatus) {
        String messages = "";
        boolean isSaveRequired = false;
        boolean isCacheReloadRequired = false;
        switch (requestedPhrase.getCurrentStatus()) {
            case REQUESTED:
                switch (requestedStatus) {
                    case REQUESTED:
                        requestedPhrase.setRequestCount(requestedPhrase.getRequestCount() + 1);
                        messages = "Increased the requested count";
                        isSaveRequired = true;
                        break;
                    case APPROVED:
                        requestedPhrase.setCurrentStatus(RequestStatus.APPROVED);
                        messages = "Approved phrase.";
                        isSaveRequired = true;
                        isCacheReloadRequired = true;
                        break;
                    case BLACKLISTED:
                        requestedPhrase.setCurrentStatus(RequestStatus.BLACKLISTED);
                        messages = "Blacklisted phrase.";
                        isSaveRequired = true;
                        isCacheReloadRequired = true;
                        break;
                }
                break;
            case APPROVED:
                switch (requestedStatus) {
                    case REQUESTED:
                        messages = "Cannot be moved to requested as already approved. Only blacklisting is possible";
                        break;
                    case APPROVED:
                        messages = "Cannot be moved to approved as already approved. Only blacklisting is possible";
                        break;
                    case BLACKLISTED:
                        requestedPhrase.setCurrentStatus(RequestStatus.BLACKLISTED);
                        messages = "Blacklisted phrase.";
                        isSaveRequired = true;
                        isCacheReloadRequired = true;
                        break;
                }
                break;
            case BLACKLISTED:
                switch (requestedStatus) {
                    case REQUESTED:
                        messages = "Cannot be moved to requested as already blacklisted. Only approving is possible";
                        break;
                    case APPROVED:
                        requestedPhrase.setCurrentStatus(RequestStatus.APPROVED);
                        messages = "Approved phrase.";
                        isSaveRequired = true;
                        isCacheReloadRequired = true;
                        break;
                    case BLACKLISTED:
                        messages = "Cannot be moved to blacklisted as already blacklisted. Only approving is possible";
                        break;
                }
                break;
        }
        return new PhraseActionResponse(requestedPhrase.getPhrase(), requestedPhrase.getCurrentStatus(), requestedPhrase.getRequestCount(), messages + ". Current state - " + requestedPhrase.getCurrentStatus(), isSaveRequired, isCacheReloadRequired);
    }

    private Map<String, RequestedPhrase> findIterativelyByLoadCount(Set<String> phrases) {
        List<RequestedPhrase> requestedPhraseList = requestedPhraseDao.findByIds(phrases);
        ImmutableMap<String, RequestedPhrase> phraseToRequestedPhrase = Maps.uniqueIndex(requestedPhraseList, new Function<RequestedPhrase, String>() {
            @Nullable
            @Override
            public String apply(@Nullable RequestedPhrase input) {
                return input.getPhrase();
            }
        });
        return phraseToRequestedPhrase;
    }
}
