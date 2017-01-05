package com.flipkart.cs.languagetool.service.bootstrap;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.flipkart.abt.rotationBundle.RotationManagementConfig;
import com.flipkart.abt.rotationBundle.bundle.RotationManagementBundle;
import com.flipkart.abt.rotationBundle.tasks.RotationManagementTask;
import com.flipkart.cs.languagetool.service.exception.ApiExceptionMapper;
import com.flipkart.cs.languagetool.service.exception.CustomJsonMappingExceptionMapper;
import com.flipkart.cs.languagetool.service.exception.GenericExceptionMapper;
import com.flipkart.cs.languagetool.service.exception.NotFoundExceptionMapper;
import com.flipkart.cs.languagetool.service.filters.RequestFilter;
import com.flipkart.cs.languagetool.service.filters.ResponseFilter;
import com.flipkart.cs.languagetool.service.resources.ConsoleResource;
import com.flipkart.cs.languagetool.service.resources.LanguageToolApiResource;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.ScanningHibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by anmol.kapoor on 02/01/17.
 */
@Slf4j
public class LanguageToolServiceApplication extends Application<LanguageToolServiceConfig> {
    private RotationManagementBundle<LanguageToolServiceConfig> rotationManagementBundle;
    private HibernateBundle<LanguageToolServiceConfig> hibernate;

    public static void main(String[] args) throws Exception {
        new LanguageToolServiceApplication().run(args);
    }

    @Override
    public void run(LanguageToolServiceConfig languageToolServiceConfig, Environment environment) throws Exception {
        RotationManagementTask task = rotationManagementBundle.getTask();
        /// register components to this task;
        Injector injector = createInjector(languageToolServiceConfig, hibernate,task);
        environment.jersey().register(injector.getInstance(ConsoleResource.class));
        environment.jersey().register(injector.getInstance(LanguageToolApiResource.class));
        environment.jersey().register(injector.getInstance(ApiExceptionMapper.class));
        environment.jersey().register(injector.getInstance(GenericExceptionMapper.class));
        environment.jersey().register(injector.getInstance(NotFoundExceptionMapper.class));
        environment.jersey().register(injector.getInstance(RequestFilter.class));
        environment.jersey().register(injector.getInstance(ResponseFilter.class));
        environment.jersey().register(injector.getInstance(ApiExceptionMapper.class));
        environment.jersey().register(injector.getInstance(CustomJsonMappingExceptionMapper.class));
        environment.jersey().register(injector.getInstance(GenericExceptionMapper.class));
        environment.jersey().register(injector.getInstance(NotFoundExceptionMapper.class));
    }

    private Injector createInjector(LanguageToolServiceConfig languageToolServiceConfig,
                                    HibernateBundle<LanguageToolServiceConfig> hibernate,
                                    RotationManagementTask task) {
        return Guice.createInjector(new LanguageToolServiceModule(languageToolServiceConfig, hibernate,task));
    }

    @Override
    public void initialize(Bootstrap<LanguageToolServiceConfig> bootstrap) {
        super.initialize(bootstrap);
        hibernate = new ScanningHibernateBundle<LanguageToolServiceConfig>("com.flipkart.cs.languagetool.service.models.domain") {
            @Override
            public PooledDataSourceFactory getDataSourceFactory(LanguageToolServiceConfig languageToolServiceConfig) {
                return languageToolServiceConfig.getDataSourceFactory();
            }
        };
        bootstrap.getObjectMapper().registerModule(new JodaModule());
        bootstrap.getObjectMapper().setDateFormat(new SimpleDateFormat("yyyy-mm-dd HH:mm:ss"));
        bootstrap.getObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        bootstrap.getObjectMapper().setTimeZone(TimeZone.getTimeZone("IST"));
        bootstrap.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        bootstrap.addBundle(hibernate);
        rotationManagementBundle = new RotationManagementBundle<LanguageToolServiceConfig>() {
            @Override
            public RotationManagementConfig getRotationManagementConfig(LanguageToolServiceConfig configuration) {
                return configuration.getRotationManagementConfig();
            }
        };
        bootstrap.addBundle(rotationManagementBundle);
        bootstrap.addBundle(new MultiPartBundle());
    }

    @Override
    public String getName() {
        return "LanguageToolServiceApplication";
    }
}
