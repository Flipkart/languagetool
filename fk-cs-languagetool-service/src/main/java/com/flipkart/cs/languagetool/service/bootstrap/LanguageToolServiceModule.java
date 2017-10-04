package com.flipkart.cs.languagetool.service.bootstrap;

import com.flipkart.abt.rotationBundle.tasks.RotationManagementTask;
import com.flipkart.cs.languagetool.language.FlipkartLanguage;
import com.flipkart.cs.languagetool.service.LanguageToolService;
import com.flipkart.cs.languagetool.service.exception.ApiException;
import com.flipkart.kloud.config.ConfigClient;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.dropwizard.hibernate.HibernateBundle;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.languagetool.JLanguageTool;
import org.languagetool.rules.Rule;
import org.languagetool.rules.spelling.SpellingCheckRule;

import java.util.Set;

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
    @Singleton
    public ConfigClient provideConfigClient()
    {
        return new ConfigClient();
    }

    @Provides
    public SessionFactory providerSessionFactory(HibernateBundle hibernateBundle) {
        return hibernateBundle.getSessionFactory();
    }

    @Provides
    @Singleton
    public FlipkartLanguage providerFlipkartLanguage(LanguageToolService languageToolService) {
        log.info("Creating new Flipkart Language");
        return new FlipkartLanguage(languageToolService);
    }

    @Provides
    public JLanguageTool providerJLanguageTool(FlipkartLanguage flipkartLanguage, LanguageToolService languageToolService) throws ApiException {
        log.info("Creating new JLanguageTool");
        Set<String> approvedWords = languageToolService.getApprovedWords();
        /// adding the ignored words of a dictionary
        // asking language service to give the list
        JLanguageTool jLanguageTool = new JLanguageTool(flipkartLanguage);
        for (Rule rule : jLanguageTool.getAllActiveRules()) {
            if (rule instanceof SpellingCheckRule) {
                ((SpellingCheckRule) rule).acceptPhrases(Lists.newArrayList(approvedWords));
            }
        }
        jLanguageTool.disableRule("WHITESPACE_RULE");
        log.info("Done adding stuff.");
        return jLanguageTool;

    }
}
