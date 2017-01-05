package com.flipkart.cs.languagetool.service.models.dtos;

import com.flipkart.cs.languagetool.service.models.domain.RequestStatus;
import lombok.Data;
import org.joda.time.DateTime;

/**
 * Created by anmol.kapoor on 05/01/17.
 */
@Data
public class RequestedPhraseResponse {
    private String phrase;
    private RequestStatus currentStatus;
    private Integer requestCount;
    private DateTime modifiedAt;
    private String modifiedByUser;
    private String modifiedBySystem;
}
