package com.flipkart.cs.languagetool.service.cache;

import com.flipkart.cs.languagetool.service.exception.ApiException;
import com.flipkart.cs.languagetool.service.models.dao.RegisteredDictionaryDao;
import com.flipkart.cs.languagetool.service.models.dao.RequestedPhraseDao;
import com.flipkart.cs.languagetool.service.models.domain.RegisteredDictionary;
import com.flipkart.cs.languagetool.service.models.domain.RequestStatus;
import com.google.common.cache.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by anmol.kapoor on 04/01/17.
 */
@Singleton
@Slf4j
public class ValidationCache {

    private final RequestedPhraseDao requestedPhraseDao;
    private final RegisteredDictionaryDao registeredDictionaryDao;
    RemovalListener<RegisteredDictionary, CachedEntity> removalListener = new RemovalListener<RegisteredDictionary, CachedEntity>() {
        @Override
        public void onRemoval(RemovalNotification<RegisteredDictionary, CachedEntity> notification) {
            log.info("Removal from cache : " + notification.getKey() + " " + notification.getKey() + " reason: " + notification.getCause());
        }
    };

    // Loading cache for approved words
    LoadingCache<RegisteredDictionary, CachedEntity> approvedWordsCache = CacheBuilder.newBuilder()
            .concurrencyLevel(1)
            .removalListener(removalListener)
            .build(new CacheLoader<RegisteredDictionary, CachedEntity>() {
                @Override
                public CachedEntity load(RegisteredDictionary key) throws Exception {
                    return new CachedEntity(key.getCurrentVersionCreatedAt(), requestedPhraseDao.getPhrasesAsSetOfStatus(key, RequestStatus.APPROVED));
                }
            });

    // Loading cache for blacklisted words
    LoadingCache<RegisteredDictionary, CachedEntity> blacklistedWordsCache = CacheBuilder.newBuilder()
            .concurrencyLevel(1)
            .removalListener(removalListener)
            .build(new CacheLoader<RegisteredDictionary, CachedEntity>() {
                @Override
                public CachedEntity load(RegisteredDictionary key) throws Exception {
                    return new CachedEntity(key.getCurrentVersionCreatedAt(), requestedPhraseDao.getPhrasesAsSetOfStatus(key, RequestStatus.BLACKLISTED));
                }
            });


    /// have a validation to invalidate a value for a language if saveRequired time is changed.

    @Inject
    public ValidationCache(RequestedPhraseDao requestedPhraseDao, RegisteredDictionaryDao registeredDictionaryDao) {
        this.requestedPhraseDao = requestedPhraseDao;
        this.registeredDictionaryDao = registeredDictionaryDao;
    }

    public Set<String> validateAndGetApprovedWords(RegisteredDictionary key) throws ApiException {
        return validateAndGetWords(key, approvedWordsCache);
    }

    public Set<String> validateAndGetBlacklistedWords(RegisteredDictionary key) throws ApiException {
        return validateAndGetWords(key, blacklistedWordsCache);
    }

    private Set<String> validateAndGetWords(RegisteredDictionary dictionary, LoadingCache<RegisteredDictionary, CachedEntity> cache) throws ApiException {
        try {
            CachedEntity cachedEntity = cache.get(dictionary);
            if (dictionary.getCurrentVersionCreatedAt().isAfter(cachedEntity.getCacheCreatedAt())) {
                /// case where dictionary is at newer time than our cache
                log.info("Cache miss : called reload "+dictionary.getShortCode());
                cache.refresh(dictionary);
                return cache.get(dictionary).getPhrases();
            } else {
                log.info("Cache hit : "+dictionary.getShortCode());
                return cachedEntity.getPhrases();
            }

        } catch (ExecutionException e) {
            log.error("Unable to get from cache : " + e.getLocalizedMessage(), e);
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "Unable to get from cache.", e);
        }
    }


}
