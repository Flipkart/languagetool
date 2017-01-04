package com.flipkart.cs.languagetool.service.models.dtos;

import com.flipkart.cs.languagetool.service.models.domain.RequestStatus;
import lombok.Data;

import java.util.Set;

/**
 * Created by anmol.kapoor on 03/01/17.
 */
@Data
public class PhraseRequest {
//// Add the constraints for validations
    private RequestStatus status;

    private Set<String> phrases;
}
