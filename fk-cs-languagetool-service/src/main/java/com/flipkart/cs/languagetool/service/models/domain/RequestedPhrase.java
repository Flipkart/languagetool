package com.flipkart.cs.languagetool.service.models.domain;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;

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
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime createdAt;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime modifiedAt;
    private String createdByUser;
    private String modifiedByUser;
    private String createdBySystem;
    private String modifiedBySystem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dictionary_id", nullable = false)
    private RegisteredDictionary associatedRegisteredDictionary;

    public RequestedPhrase() {
    }

    public RequestedPhrase(String phrase, RequestStatus currentStatus, RegisteredDictionary associatedRegisteredDictionary) {
        this.phrase = phrase;
        this.currentStatus = currentStatus;
        this.associatedRegisteredDictionary = associatedRegisteredDictionary;
        this.modifiedBySystem = createdBySystem;
        this.requestCount = 0;
        if (currentStatus == RequestStatus.REQUESTED) {
            this.requestCount = this.requestCount + 1;
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

    public RegisteredDictionary getAssociatedRegisteredDictionary() {
        return associatedRegisteredDictionary;
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

    public void setAssociatedRegisteredDictionary(RegisteredDictionary associatedRegisteredDictionary) {
        this.associatedRegisteredDictionary = associatedRegisteredDictionary;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestedPhrase that = (RequestedPhrase) o;
        return Objects.equal(phrase, that.phrase) &&
                currentStatus == that.currentStatus &&
                Objects.equal(requestCount, that.requestCount) &&
                Objects.equal(createdAt, that.createdAt) &&
                Objects.equal(modifiedAt, that.modifiedAt) &&
                Objects.equal(createdByUser, that.createdByUser) &&
                Objects.equal(modifiedByUser, that.modifiedByUser) &&
                Objects.equal(createdBySystem, that.createdBySystem) &&
                Objects.equal(modifiedBySystem, that.modifiedBySystem);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(phrase, currentStatus, requestCount, createdAt, modifiedAt, createdByUser, modifiedByUser, createdBySystem, modifiedBySystem);
    }
}
