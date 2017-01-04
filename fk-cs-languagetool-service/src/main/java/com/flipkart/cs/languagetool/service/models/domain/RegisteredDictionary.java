package com.flipkart.cs.languagetool.service.models.domain;

import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.List;

/**
 * Created by anmol.kapoor on 02/01/17.
 */
@Entity
@Table(name = "registered_dictionary")
public class RegisteredDictionary {
    @Id
    private String shortCode;
    private String name;
    private DateTime createdAt;
    private DateTime updatedAt;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "dictionary_to_phrase", joinColumns = {
            @JoinColumn(name = "languageId", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "requestedPhraseId",
                    nullable = false, updatable = false) })
    private List<RequestedPhrase> requestedPhraseList;


    // GETTERS

    public String getShortCode() {
        return shortCode;
    }

    public String getName() {
        return name;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<RequestedPhrase> getRequestedPhraseList() {
        return requestedPhraseList;
    }


    // SETTERS


    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setRequestedPhraseList(List<RequestedPhrase> requestedPhraseList) {
        this.requestedPhraseList = requestedPhraseList;
    }
}
