package com.flipkart.cs.languagetool.language;

import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.Rule;
import org.languagetool.rules.en.BlacklistedWordRule;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by anmol.kapoor on 02/01/17.
 */
public class FlipkartLanguage extends AmericanEnglish {

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
        List<Rule> rules = new ArrayList<>();
        rules.addAll(super.getRelevantRules(messages));
        rules.add(new BlacklistedWordRule(messages));
        return rules;
    }

}
