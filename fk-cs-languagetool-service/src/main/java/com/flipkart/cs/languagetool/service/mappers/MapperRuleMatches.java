package com.flipkart.cs.languagetool.service.mappers;

import com.flipkart.cs.languagetool.service.models.dtos.*;
import com.google.inject.Inject;
import org.languagetool.Language;
import org.languagetool.rules.Category;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.patterns.AbstractPatternRule;
import org.languagetool.tools.ContextTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anmol.kapoor on 03/01/17.
 */
public class MapperRuleMatches {
    ContextTools contextTools ;
    private static final String START_MARKER = "__languagetool_start_marker";

    @Inject
    public MapperRuleMatches() {
        contextTools = new ContextTools();
        contextTools.setEscapeHtml(false);
        contextTools.setContextSize(40);
        contextTools.setErrorMarkerStart(START_MARKER);
        contextTools.setErrorMarkerEnd("");
    }

    public CheckTextResponse toCheckTextResponse(List<RuleMatch> ruleMatchList, CheckTextResponse checkTextResponse,
                                                 Language language, String text) {
        checkTextResponse.setLanguage(new LanguageResponse(language.getName(), language.getShortCode(), RequestHeaders.get().getDictionary()));
        checkTextResponse.setMatches(new ArrayList<>());
        if (ruleMatchList != null && !ruleMatchList.isEmpty()) {
            for (RuleMatch ruleMatch : ruleMatchList) {
                checkTextResponse.getMatches().add(mapMatchToMatchResponse(ruleMatch, new MatchResponse(), text));
            }
        }
        return checkTextResponse;
    }

    private MatchResponse mapMatchToMatchResponse(RuleMatch ruleMatch, MatchResponse matchResponse, String text) {
        matchResponse.setMessage(cleanSuggestion(ruleMatch.getMessage()));
        matchResponse.setShortMessage(cleanSuggestion(ruleMatch.getShortMessage()));
        matchResponse.setReplacements(mapSuggestionsToReplacements(ruleMatch.getSuggestedReplacements()));
        matchResponse.setOffset(ruleMatch.getFromPos());
        matchResponse.setLength(ruleMatch.getToPos() - ruleMatch.getFromPos());
        matchResponse.setContext(mapContextToContextResponse(ruleMatch.getFromPos(), ruleMatch.getToPos(), text, ruleMatch));
        matchResponse.setRule(mapRuleToRuleResponse(ruleMatch));
        return matchResponse;
    }

    private RuleResponse mapRuleToRuleResponse(RuleMatch match) {
        RuleResponse ruleResponse = new RuleResponse();
        ruleResponse.setId(match.getRule().getId());

        if (match.getRule() instanceof AbstractPatternRule) {
            AbstractPatternRule pRule = (AbstractPatternRule) match.getRule();
            if (pRule.getSubId() != null) {
                ruleResponse.setSubId(pRule.getSubId());
            }
        }
        ruleResponse.setDescription(match.getRule().getDescription());
        ruleResponse.setIssueType(match.getRule().getLocQualityIssueType().toString());
        if (match.getRule().getCategory() != null)
            ruleResponse.setCategory(mapCategoryToCategoryResponse(match.getRule().getCategory()));
        return ruleResponse;
    }

    private RuleCategory mapCategoryToCategoryResponse(Category category) {
        if (category.getId() != null) {
            RuleCategory ruleCategory = new RuleCategory(category.getId().toString(), category.getName());
            return ruleCategory;
        } else {
            return null;
        }
    }

    private ContextResponse mapContextToContextResponse(int fromPos, int toPos, String text, RuleMatch match) {

        String context = contextTools.getContext(fromPos, toPos, text);
        int contextOffset = context.indexOf(START_MARKER);
        context = context.replaceFirst(START_MARKER, "");
        return new ContextResponse(context, (long) contextOffset, (long) (match.getToPos() - match.getFromPos()));
    }

    private List<Replacement> mapSuggestionsToReplacements(List<String> suggestedReplacements) {
        if (suggestedReplacements == null || suggestedReplacements.isEmpty()) {
            return null;
        }
        List<Replacement> replacements = new ArrayList<>();
        for (String suggestedReplacement : suggestedReplacements) {
            replacements.add(new Replacement(suggestedReplacement));
        }
        return replacements;

    }

    private String cleanSuggestion(String s) {
        return s.replace("<suggestion>", "\"").replace("</suggestion>", "\"");
    }
}
