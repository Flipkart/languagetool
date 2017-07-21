package com.flipkart.cs.languagetool.service.cache;

import com.flipkart.cs.languagetool.service.SimpleHibernateTxnManager;
import com.flipkart.cs.languagetool.service.exception.ApiException;
import com.flipkart.cs.languagetool.service.models.dao.RegisteredDictionaryDao;
import com.flipkart.cs.languagetool.service.models.dao.RequestedPhraseDao;
import com.flipkart.cs.languagetool.service.models.domain.RegisteredDictionary;
import com.flipkart.cs.languagetool.service.models.domain.RequestStatus;
import com.google.common.cache.*;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by anmol.kapoor on 04/01/17.
 */
@Singleton
@Slf4j
public class ValidationCache {

    private final RequestedPhraseDao requestedPhraseDao;
    private final RegisteredDictionaryDao registeredDictionaryDao;
    private final Provider<SimpleHibernateTxnManager> simpleHibernateTxnManager;
    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("ValidationCache-pool-%d").setDaemon(true).build();
    ExecutorService parentExecutor = Executors.newSingleThreadExecutor(threadFactory);
    final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(parentExecutor);

    RemovalListener<String, CachedEntity> removalListener = new RemovalListener<String, CachedEntity>() {
        @Override
        public void onRemoval(RemovalNotification<String, CachedEntity> notification) {
            log.info("Removal from cache : " + notification.getKey() + " reason: " + notification.getCause());
        }
    };

    // Loading cache for blacklisted words
//    private final LoadingCache<String, CachedEntity> blacklistedWordsCache = CacheBuilder.newBuilder()
////            .concurrencyLevel(1)
//            .removalListener(removalListener)
//            .build(new CacheLoader<String, CachedEntity>() {
//                @Override
//                public CachedEntity load(String shortCode) throws Exception {
//                    RegisteredDictionary registeredDictionary = registeredDictionaryDao.findById(shortCode).get();
//                    return new CachedEntity(registeredDictionary.getCurrentVersionCreatedAt(), requestedPhraseDao.getPhrasesAsSetOfStatus(registeredDictionary, RequestStatus.BLACKLISTED));
//                }
//            });

    private final LoadingCache<String, CachedEntity> blacklistedWordsCache = CacheBuilder.newBuilder().concurrencyLevel(1)
            .removalListener(removalListener)
            .build(new CacheLoader<String, CachedEntity>() {
                @Override
                public CachedEntity load(String shortCode) throws Exception {
                    log.info("Loading Blacklisted words key : "+shortCode);
                    SimpleHibernateTxnManager simpleHibernateTxnManagerInstance = ValidationCache.this.simpleHibernateTxnManager.get();
                    simpleHibernateTxnManagerInstance.createAndBindHibernateSession();
                    boolean exceptionOccurred = false;
                    try {
                        RegisteredDictionary registeredDictionary = registeredDictionaryDao.findById(shortCode).get();
                        return new CachedEntity(registeredDictionary.getCurrentVersionCreatedAt(),
                                requestedPhraseDao.getPhrasesAsSetOfStatus(registeredDictionary, RequestStatus.BLACKLISTED));
                    } catch (Exception e) {
                        exceptionOccurred = true;
                        log.error(e.getLocalizedMessage(), e);
                        throw e;
                    } finally {
                        simpleHibernateTxnManagerInstance.closeSessionCommitOrRollBackTxn(exceptionOccurred);
                    }

                }

                @Override
                public ListenableFuture<CachedEntity> reload(String key, CachedEntity oldValue) throws Exception {
                    log.info("Reloading cache Key : " + key);
                    ListenableFuture<CachedEntity> submit = executorService.submit(new Callable<CachedEntity>() {
                        @Override
                        public CachedEntity call() throws Exception {
                            return load(key);
                        }
                    });
                    return submit;
                }
            });

    // Loading cache for approved words
//    private final LoadingCache<RegisteredDictionary, CachedEntity> approvedWordsCache = CacheBuilder.newBuilder()
//            .concurrencyLevel(1)
//            .removalListener(removalListener)
//            .build(new CacheLoader<RegisteredDictionary, CachedEntity>() {
//                @Override
//                public CachedEntity load(RegisteredDictionary key) throws Exception {
//                    return new CachedEntity(key.getCurrentVersionCreatedAt(), requestedPhraseDao.getPhrasesAsSetOfStatus(key, RequestStatus.APPROVED));
//                }
//            });

    private final LoadingCache<String, CachedEntity> approvedWordsCache = CacheBuilder.newBuilder()
            .concurrencyLevel(1)
            .removalListener(removalListener)
            .build(new CacheLoader<String, CachedEntity>() {
                @Override
                public CachedEntity load(String shortCode) throws Exception {
                    log.info("Loading Approved words key : "+shortCode);
                    SimpleHibernateTxnManager simpleHibernateTxnManagerInstance = ValidationCache.this.simpleHibernateTxnManager.get();
                    simpleHibernateTxnManagerInstance.createAndBindHibernateSession();
                    boolean exceptionOccurred = false;
                    try {

                        RegisteredDictionary registeredDictionary = registeredDictionaryDao.findById(shortCode).get();
                        return new CachedEntity(registeredDictionary.getCurrentVersionCreatedAt(), requestedPhraseDao.getPhrasesAsSetOfStatus(registeredDictionary, RequestStatus.APPROVED));
                    } catch (Exception e) {
                        exceptionOccurred = true;
                        log.error(e.getLocalizedMessage(), e);
                        throw e;
                    } finally {
                        simpleHibernateTxnManagerInstance.closeSessionCommitOrRollBackTxn(exceptionOccurred);
                    }
                }


                @Override
                public ListenableFuture<CachedEntity> reload(String key, CachedEntity oldValue) throws Exception {
                    log.info("Reloading cache Key : " + key);
                    ListenableFuture<CachedEntity> submit = executorService.submit(new Callable<CachedEntity>() {
                        @Override
                        public CachedEntity call() throws Exception {
                            return load(key);
                        }
                    });
                    return submit;
                }
            });


    /// have a validation to invalidate a value for a language if saveRequired time is changed.

    @Inject
    public ValidationCache(RequestedPhraseDao requestedPhraseDao, RegisteredDictionaryDao registeredDictionaryDao, Provider<SimpleHibernateTxnManager> simpleHibernateTxnManager) {
        this.requestedPhraseDao = requestedPhraseDao;
        this.registeredDictionaryDao = registeredDictionaryDao;
        this.simpleHibernateTxnManager = simpleHibernateTxnManager;
    }

    public Set<String> validateAndGetApprovedWords(RegisteredDictionary key) throws ApiException {
        return validateAndGetWords(key, approvedWordsCache);
    }

    public Set<String> validateAndGetBlacklistedWords(RegisteredDictionary key) throws ApiException {
        return validateAndGetWords(key, blacklistedWordsCache);
    }


    public void loadValidationCaches() throws ExecutionException {
        blacklistedWordsCache.get("cs-email");
        approvedWordsCache.get("cs-email");
    }

    private Set<String> validateAndGetWords(RegisteredDictionary dictionary, LoadingCache<String, CachedEntity> cache) throws ApiException {
        try {
            CachedEntity cachedEntity = cache.get(dictionary.getShortCode());
            if (dictionary.getCurrentVersionCreatedAt().isAfter(cachedEntity.getCacheCreatedAt())) {
                /// case where dictionary is at newer time than our cache
                log.info("Cache miss : called reload " + dictionary.getShortCode());
                cache.refresh(dictionary.getShortCode());
                return cache.get(dictionary.getShortCode()).getPhrases();
            } else {
                log.info("Cache hit : " + dictionary.getShortCode());
                return cachedEntity.getPhrases();
            }

        } catch (ExecutionException e) {
            log.error("Unable to get from cache : " + e.getLocalizedMessage(), e);
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, "Unable to get from cache.", e);
        }
    }


}
