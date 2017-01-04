package com.flipkart.cs.languagetool.service.models.domain;

import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;

/**
 * Created by anmol.kapoor on 02/01/17.
 */
@Entity
@Table(name = "requested_phrase")
public class RequestedPhrase {
    @Id
    private String phrase;
    @Enumerated(EnumType.STRING)
    private RequestStatus currentStatus;
    private Integer requestCount;
    private DateTime createdAt;
    private DateTime modifiedAt;
    private String createdByUser;
    private String modifiedByUser;
    private String createdBySystem;
    private String modifiedBySystem;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "requestedPhraseList")
    private List<RegisteredDictionary> registeredDictionaryList;

    public RequestedPhrase() {
    }

    public RequestedPhrase(String phrase, RequestStatus currentStatus, List<RegisteredDictionary> registeredDictionary) {
        this.phrase = phrase;
        this.currentStatus = currentStatus;
        this.registeredDictionaryList = registeredDictionary;
        this.modifiedBySystem = createdBySystem;
        this.requestCount = 0;
        if(currentStatus == RequestStatus.REQUESTED)
        {
            this.requestCount = this.requestCount+1;
        }


    }

    /// Getters

    public String getPhrase() {
        return phrase;
    }

    public RequestStatus getCurrentStatus() {
        return currentStatus;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getModifiedAt() {
        return modifiedAt;
    }

    public List<RegisteredDictionary> getRegisteredDictionaryList() {
        return registeredDictionaryList;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }

    public String getModifiedByUser() {
        return modifiedByUser;
    }

    public String getCreatedBySystem() {
        return createdBySystem;
    }

    public String getModifiedBySystem() {
        return modifiedBySystem;
    }

    public Integer getRequestCount() {
        return requestCount;
    }
    /// Setters


    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public void setCurrentStatus(RequestStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setModifiedAt(DateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public void setRegisteredDictionaryList(List<RegisteredDictionary> registeredDictionaryList) {
        this.registeredDictionaryList = registeredDictionaryList;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }

    public void setModifiedByUser(String modifiedByUser) {
        this.modifiedByUser = modifiedByUser;
    }

    public void setCreatedBySystem(String createdBySystem) {
        this.createdBySystem = createdBySystem;
    }

    public void setModifiedBySystem(String modifiedBySystem) {
        this.modifiedBySystem = modifiedBySystem;
    }

    public void setRequestCount(Integer requestCount) {
        this.requestCount = requestCount;
    }
}
