package com.flipkart.cs.languagetool.service.models.domain;

import org.hibernate.annotations.Type;
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
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdAt;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime updatedAt;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime currentVersionCreatedAt;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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

    public DateTime getCurrentVersionCreatedAt() {
        return currentVersionCreatedAt;
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

    public void setCurrentVersionCreatedAt(DateTime currentVersionCreatedAt) {
        this.currentVersionCreatedAt = currentVersionCreatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegisteredDictionary that = (RegisteredDictionary) o;

        return shortCode.equals(that.shortCode);

    }

    @Override
    public int hashCode() {
        return shortCode.hashCode();
    }
}
