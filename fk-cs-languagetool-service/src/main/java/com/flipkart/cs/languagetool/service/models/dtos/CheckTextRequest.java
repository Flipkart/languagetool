package com.flipkart.cs.languagetool.service.models.dtos;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by anmol.kapoor on 03/01/17.
 */
@Data
public class CheckTextRequest {
    @NotBlank
    private String text;
}
