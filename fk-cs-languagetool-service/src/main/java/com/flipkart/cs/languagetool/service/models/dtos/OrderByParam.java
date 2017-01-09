package com.flipkart.cs.languagetool.service.models.dtos;

/**
 * Created by anmol.kapoor on 09/01/17.
 */
public enum  OrderByParam {
    PHRASE("phrase"), REQUEST_COUNT("requestCount"), MODIFIED_AT("modifiedAt");

    private String propertyName;

    public String getPropertyName() {
        return propertyName;
    }

    private OrderByParam(String propertyName) {
        this.propertyName = propertyName;
    }
}
