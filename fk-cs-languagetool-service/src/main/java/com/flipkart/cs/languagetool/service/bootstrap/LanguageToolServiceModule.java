package com.flipkart.cs.languagetool.service.bootstrap;

import com.flipkart.abt.rotationBundle.tasks.RotationManagementTask;
import com.flipkart.cs.languagetool.language.FlipkartLanguage;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.dropwizard.hibernate.HibernateBundle;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.languagetool.JLanguageTool;

/**
 * Created by anmol.kapoor on 02/01/17.
 */
@Slf4j
public class LanguageToolServiceModule extends AbstractModule {

    private final LanguageToolServiceConfig languageToolServiceConfig;
    private final HibernateBundle<LanguageToolServiceConfig> hibernate;
    private final RotationManagementTask rotationTask;

    public LanguageToolServiceModule(LanguageToolServiceConfig languageToolServiceConfig,
                                     HibernateBundle<LanguageToolServiceConfig> hibernate,
                                     RotationManagementTask task) {
        this.languageToolServiceConfig = languageToolServiceConfig;
        this.hibernate = hibernate;
        this.rotationTask = task;
    }

    @Override
    protected void configure() {
        bind(LanguageToolServiceConfig.class).toInstance(languageToolServiceConfig);
        bind(HibernateBundle.class).toInstance(hibernate);
        bind(RotationManagementTask.class).toInstance(rotationTask);

    }

    @Provides
    public SessionFactory providerSessionFactory(HibernateBundle hibernateBundle) {
        return hibernateBundle.getSessionFactory();
    }

    @Provides
    @Singleton
    public FlipkartLanguage providerFlipkartLanguage() {
        log.info("Creating new Flipkart Language");
        return new FlipkartLanguage();
    }

    @Provides
    public JLanguageTool providerJLanguageTool(FlipkartLanguage flipkartLanguage) {
        log.info("Creating new JLanguageTool");
        return new JLanguageTool(flipkartLanguage);
    }
}
