package com.flipkart.cs.languagetool.service.cache;

import lombok.Data;
import org.joda.time.DateTime;

import java.util.Set;

/**
 * Created by anmol.kapoor on 04/01/17.
 */
@Data
public class CachedEntity {
    DateTime cacheCreatedAt;
    Set<String> phrases;

    public CachedEntity() {
    }

    public CachedEntity(DateTime cacheCreatedAt, Set<String> phrases) {
        this.cacheCreatedAt = cacheCreatedAt;
        this.phrases = phrases;
    }
}
