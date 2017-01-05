package com.flipkart.cs.languagetool.language;

import com.flipkart.cs.languagetool.language.rules.BlacklistedWordRule;
import com.flipkart.cs.languagetool.service.LanguageToolService;
import com.flipkart.cs.languagetool.service.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.Rule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by anmol.kapoor on 02/01/17.
 */
@Slf4j
public class FlipkartLanguage extends AmericanEnglish {


    private  LanguageToolService languageToolService;


    public FlipkartLanguage() {
    }

    public FlipkartLanguage(LanguageToolService languageToolService) {
        this.languageToolService = languageToolService;
        log.info("Creating new Flipkart Language");
    }

    @Override
    public String[] getCountries() {
        return new String[]{"IN"};
    }

    @Override
    public String getName() {
        return "Flipkart English (IN)";
    }

    @Override
    public List<Rule> getRelevantRules(ResourceBundle messages) throws IOException {
        if(languageToolService==null)
        {
            throw new IOException("There was some issue with Flipkart Language creation .getRelaventRules. languageToolService is null");
        }
        List<Rule> rules = new ArrayList<>();
        rules.addAll(super.getRelevantRules(messages));
        try {
            rules.add(new BlacklistedWordRule(messages,languageToolService.getBlacklistedWords()));
        } catch (ApiException e) {
            throw new IOException("Unable to get Blacklisted words : "+e.getLocalizedMessage());
        }
        return rules;
    }

}
