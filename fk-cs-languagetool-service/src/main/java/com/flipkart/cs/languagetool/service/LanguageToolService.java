package com.flipkart.cs.languagetool.service;

import com.flipkart.cs.languagetool.service.models.dao.RequestedPhraseDao;
import com.flipkart.cs.languagetool.service.models.domain.RegisteredDictionary;
import com.flipkart.cs.languagetool.service.models.domain.RequestStatus;
import com.flipkart.cs.languagetool.service.models.domain.RequestedPhrase;
import com.flipkart.cs.languagetool.service.models.dtos.PhraseActionResponse;
import com.flipkart.cs.languagetool.service.models.dtos.PhraseRequest;
import com.flipkart.cs.languagetool.service.models.dtos.RequestHeaders;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by anmol.kapoor on 02/01/17.
 */
@Singleton
public class LanguageToolService {

    private final RequestedPhraseDao requestedPhraseDao;
    private final int LOAD_COUNT = 100;

    @Inject
    public LanguageToolService(RequestedPhraseDao requestedPhraseDao) {
        this.requestedPhraseDao = requestedPhraseDao;
    }

    public Set<String> getApprovedWords() {
        return new HashSet<>();
    }

    public Set<String> getBlacklistedWords() {
        return new HashSet<>();
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
        for (String phraseRequested : phraseRequest.getPhrases()) {
            RequestedPhrase requestedPhrase = null;
            PhraseActionResponse response = null;
            if (requestedPhrasesInDb.containsKey(phraseRequested)) {
                requestedPhrase = requestedPhrasesInDb.get(phraseRequested);
                response = changeStateToRequestedState(requestedPhrase, requestedStatus);
            } else {
                requestedPhrase = new RequestedPhrase(phraseRequested, requestedStatus, Lists.newArrayList(dictionary));
                dictionary.getRequestedPhraseList().add(requestedPhrase);
                response = new PhraseActionResponse(requestedPhrase.getPhrase(), requestedPhrase.getCurrentStatus(), requestedPhrase.getRequestCount(), "Created new Phrase in state : " + requestedPhrase.getCurrentStatus());
            }
            requestedPhrase = requestedPhraseDao.save(requestedPhrase);
            responseList.add(response);
        }
        return responseList;
    }

    private PhraseActionResponse changeStateToRequestedState(RequestedPhrase requestedPhrase, RequestStatus requestedStatus) {
        String messages = "";
        switch (requestedPhrase.getCurrentStatus()) {
            case REQUESTED:
                switch (requestedStatus) {
                    case REQUESTED:
                        requestedPhrase.setRequestCount(requestedPhrase.getRequestCount() + 1);
                        messages = "Increased the requested count";
                        break;
                    case APPROVED:
                        requestedPhrase.setCurrentStatus(RequestStatus.APPROVED);
                        messages = "Approved phrase.";
                        break;
                    case BLACKLISTED:
                        requestedPhrase.setCurrentStatus(RequestStatus.BLACKLISTED);
                        messages = "Blacklisted phrase.";
                        break;
                }
                break;
            case APPROVED:
                switch (requestedStatus) {
                    case REQUESTED:
                        messages = "Cannot be moved to requested, as already approved, only blacklisting is possible";
                        break;
                    case APPROVED:
                        messages = "Cannot be moved to approved, as already approved, only blacklisting is possible";
                        break;
                    case BLACKLISTED:
                        requestedPhrase.setCurrentStatus(RequestStatus.BLACKLISTED);
                        messages = "Blacklisted phrase.";
                        break;
                }
                break;
            case BLACKLISTED:
                switch (requestedStatus) {
                    case REQUESTED:
                        messages = "Cannot be moved to requested, as already blacklisted, only approving is possible";
                        break;
                    case APPROVED:
                        requestedPhrase.setCurrentStatus(RequestStatus.APPROVED);
                        messages = "Approved phrase.";
                        break;
                    case BLACKLISTED:
                        messages = "Cannot be moved to blacklisted, as already blacklisted, only approving is possible";
                        break;
                }
                break;
        }
        return new PhraseActionResponse(requestedPhrase.getPhrase(), requestedPhrase.getCurrentStatus(), requestedPhrase.getRequestCount(), messages + ". Current state: " + requestedPhrase.getCurrentStatus());
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
