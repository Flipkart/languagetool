package com.flipkart.cs.languagetool.service.bootstrap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.abt.rotationBundle.RotationManagementConfig;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created by anmol.kapoor on 02/01/17.
 */
@Data
public class LanguageToolServiceConfig extends Configuration {

    @JsonProperty("database")
    @NotNull
    private DataSourceFactory dataSourceFactory;


    @NotNull
    private RotationManagementConfig rotationManagementConfig;
}
