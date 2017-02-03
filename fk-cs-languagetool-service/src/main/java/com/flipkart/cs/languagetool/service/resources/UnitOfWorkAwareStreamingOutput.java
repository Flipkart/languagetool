package com.flipkart.cs.languagetool.service.resources;

import com.flipkart.cs.languagetool.service.LanguageToolService;
import com.flipkart.cs.languagetool.service.exception.ApiException;
import com.flipkart.cs.languagetool.service.models.dao.RegisteredDictionaryDao;
import com.flipkart.cs.languagetool.service.models.domain.RegisteredDictionary;
import com.flipkart.cs.languagetool.service.models.domain.RequestStatus;
import com.flipkart.cs.languagetool.service.models.dtos.PhraseActionResponse;
import com.flipkart.cs.languagetool.service.models.dtos.PhraseRequest;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import io.dropwizard.hibernate.UnitOfWork;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;


/**
 * Created by anmol.kapoor on 04/01/17.
 */
public class UnitOfWorkAwareStreamingOutput implements StreamingOutput {


    private LanguageToolService languageToolService;
    private Set<String> phrases;
    private RequestStatus status;
    private String dictionaryShortCode;
    private static final Logger log = LoggerFactory.getLogger(UnitOfWorkAwareStreamingOutput.class);
    private RegisteredDictionaryDao registeredDictionaryDao;

    public UnitOfWorkAwareStreamingOutput() {
    }


    public void setParameters(LanguageToolService languageToolService, Set<String> phrases, RequestStatus status, String dictionaryShortCode, RegisteredDictionaryDao registeredDictionaryDao) {
        this.languageToolService = languageToolService;
        this.phrases = phrases;
        this.status = status;
        this.dictionaryShortCode = dictionaryShortCode;
        this.registeredDictionaryDao = registeredDictionaryDao;
    }

    @Override
    @UnitOfWork
    public void write(OutputStream output) throws IOException, WebApplicationException {
        boolean exceptionThrown = false;
        try {

            Optional<RegisteredDictionary> possibleDictionary = registeredDictionaryDao.findById(this.dictionaryShortCode);
            if (!possibleDictionary.isPresent()) {
                throw new ApiException("Unable to find dictionary with shortcode : " + dictionaryShortCode);
            }
            RegisteredDictionary dictionary = possibleDictionary.get();

            /// Do it for like 50 or 100 phrases at one time..
            for (String phrase : phrases) {
                PhraseRequest request = new PhraseRequest();
                request.setStatus(status);
                request.setPhrases(Sets.newHashSet(phrase));
                List<PhraseActionResponse> responseList = languageToolService.bulkRequestActionOnPhrases(request, dictionary);
                log.info("request: " + request.toString());
                log.info("response: " + responseList.toString());
                for (PhraseActionResponse response : responseList) {
                    output.write((response.getPhrase() + "," + response.getMessage() + "\n").getBytes("UTF-8"));
                }
            }

        } catch (Exception e) {
            exceptionThrown = true;
            log.error("Unable to complete bulk upload : " + e.getLocalizedMessage(), e);
        } finally {
            if (exceptionThrown) {
                String message = "\n  \"#Error : There was error in completing bulk request. Please try again. \" " +
                        "\n\n#Error : There was error in completing bulk request. Please try again.";
                output.write(message.getBytes());
            }
            IOUtils.closeQuietly(output);
        }
    }
}
