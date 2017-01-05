package com.flipkart.cs.languagetool.language.rules;

import org.languagetool.AnalyzedSentence;
import org.languagetool.AnalyzedToken;
import org.languagetool.AnalyzedTokenReadings;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by anmol.kapoor on 02/01/17.
 */
public class BlacklistedWordRule extends Rule {

    private final Logger log = LoggerFactory.getLogger(BlacklistedWordRule.class);

    private final Set<String> blacklistedWords;

    @Override
    public String getId() {
        return "BLACKLISTED_WORD_RULE";  // a unique id that doesn't change over time
    }

    @Override
    public String getDescription() {
        return "Blacklists the use of words in flipkart communications.";  // shown in the configuration dialog
    }

    public BlacklistedWordRule(ResourceBundle messages, Set<String> blacklistedWords) {
        super(messages);
        this.blacklistedWords = blacklistedWords;
    }

    // This is the method with the error detection logic that you need to implement:
    @Override
    public RuleMatch[] match(AnalyzedSentence sentence) throws IOException {
        List<RuleMatch> ruleMatches = new ArrayList<>();

        /// Getting  all the Lemma's from the sentence.

        /// Checking if any one is not black listed by flipkart

        /// marking it as wrong.

        // Let's get all the tokens (i.e. words) of this sentence, but not the spaces:
        AnalyzedTokenReadings[] tokens = sentence.getTokensWithoutWhitespace();

        // No let's iterate over those - note that the first token will
        // be a special token that indicates the start of a sentence:
        for (AnalyzedTokenReadings token : tokens) {

            log.info("Token: " + token.getToken());  // the original word from the input text

            // A word can have more than one reading, e.g. 'dance' can be a verb or a noun,
            // so we iterate over the readings:
            Set<String> lemmas = new HashSet<>();
            for (AnalyzedToken analyzedToken : token.getReadings()) {
                String lemma = analyzedToken.getLemma();
                log.info("  Lemma: " + lemma);
                if (lemma != null) {
                    lemmas.add(lemma);
                }
            }

            for (String lemma : lemmas) {
                if (blacklistedWords.contains(lemma)) {
                    RuleMatch ruleMatch = new RuleMatch(this, token.getStartPos(), token.getEndPos(), "Use of " + lemma + " word is not approved. Please change.");
                    ruleMatches.add(ruleMatch);

                }
            }

        }

        return toRuleMatchArray(ruleMatches);
    }

//    private boolean isBlacklisted(String lemma) throws IOException {
//        Set<String> blacklistedWords = null;
//        try {
//            blacklistedWords = languageToolService.getBlacklistedWords();
//        } catch (ApiException e) {
//            throw new IOException(e.getMessage());
//        }
//        return (blacklistedWords.contains(lemma));
//
//    }

    @Override
    public void reset() {
        // if we had some internal state kept in member variables, we would need to reset them here
    }

}
