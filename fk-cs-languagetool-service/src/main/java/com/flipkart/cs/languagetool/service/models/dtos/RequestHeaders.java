package com.flipkart.cs.languagetool.service.models.dtos;

/**
 * Created by anmol.kapoor on 03/01/17.
 */

public class RequestHeaders {
    String user;
    String clientId;
    String dictionary;
    private static ThreadLocal<RequestHeaders> threadLocal = new ThreadLocal<RequestHeaders>() {
        @Override
        protected RequestHeaders initialValue() {
            return new RequestHeaders();
        }

    };

    public static RequestHeaders get() {
        return threadLocal.get();
    }

    public static void clear(){
         threadLocal.remove();
    }

    public static RequestHeaders createRequestHeadersFromHttpHeaders()
    {
        return new RequestHeaders();
    }

    public RequestHeaders setUser(String user) {
        this.user = user;
        return this;
    }

    public RequestHeaders setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public RequestHeaders setDictionary(String dictionary) {
        this.dictionary = dictionary;
        return this;
    }

    public String getUser() {
        return user;
    }

    public String getClientId() {
        return clientId;
    }

    public String getDictionary() {
        return dictionary;
    }


}
