package com.flipkart.cs.languagetool.service.resources;

import com.flipkart.cs.languagetool.service.LanguageToolService;
import com.flipkart.cs.languagetool.service.exception.ApiException;
import com.flipkart.cs.languagetool.service.mappers.MapperRequestedPhrase;
import com.flipkart.cs.languagetool.service.models.dao.RegisteredDictionaryDao;
import com.flipkart.cs.languagetool.service.models.dao.RequestedPhraseDao;
import com.flipkart.cs.languagetool.service.models.domain.RegisteredDictionary;
import com.flipkart.cs.languagetool.service.models.domain.RequestStatus;
import com.flipkart.cs.languagetool.service.models.domain.RequestedPhrase;
import com.flipkart.cs.languagetool.service.models.dtos.Paginated;
import com.flipkart.cs.languagetool.service.models.dtos.RequestHeaders;
import com.flipkart.cs.languagetool.service.models.dtos.RequestedPhraseResponse;
import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.joda.time.DateTime;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by anmol.kapoor on 02/01/17.
 */
@Slf4j
@Path("/console")

public class ConsoleResource {
    private final LanguageToolService languageToolService;
    private final RegisteredDictionaryDao registeredDictionaryDao;
    private final HibernateBundle hibernateBundle;
    private final RequestedPhraseDao requestedPhraseDao;
    private final MapperRequestedPhrase mapperRequestedPhrase;

    @Inject
    public ConsoleResource(LanguageToolService languageToolService, RegisteredDictionaryDao registeredDictionaryDao, HibernateBundle hibernateBundle, RequestedPhraseDao requestedPhraseDao, MapperRequestedPhrase mapperRequestedPhrase) {
        this.languageToolService = languageToolService;
        this.registeredDictionaryDao = registeredDictionaryDao;
        this.hibernateBundle = hibernateBundle;
        this.requestedPhraseDao = requestedPhraseDao;
        this.mapperRequestedPhrase = mapperRequestedPhrase;
    }

    private RegisteredDictionary validateAndGetDictionary() throws ApiException {
        String shortCode = RequestHeaders.get().getDictionary();
        Optional<RegisteredDictionary> possibleDictionary = registeredDictionaryDao.findById(shortCode);
        if (!possibleDictionary.isPresent()) {
            throw new ApiException(Response.Status.BAD_REQUEST, "Unable to find dictionary with shortcode : " + shortCode);

        } else {
            return possibleDictionary.get();
        }

    }


    @GET
    @Path("/phrases/{status}")
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Paginated<RequestedPhraseResponse> getRequestedPhrases(@PathParam("status") RequestStatus status, @QueryParam("page_no") Integer pageNo,
                                                                  @QueryParam("page_size") Integer pageSize) throws ApiException {
        RegisteredDictionary dictionary = validateAndGetDictionary();
        if (pageNo == null) {
            pageNo = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        Paginated<RequestedPhrase> requestedPhrasePaginated = requestedPhraseDao.getPhrasesForStatus(status, dictionary, pageNo, pageSize);
        List<RequestedPhraseResponse> requestedPhraseResponses = new ArrayList<>();
        for (RequestedPhrase requestedPhrase : requestedPhrasePaginated.getResults()) {
            requestedPhraseResponses.add(mapperRequestedPhrase.toRequestedPhraseResponse(requestedPhrase, new RequestedPhraseResponse()));
        }

        return new Paginated<>(requestedPhraseResponses, requestedPhrasePaginated.getTotal(), requestedPhrasePaginated.getCount());
    }


    @POST
    @Path("/bulkUpload/{status}")
    @UnitOfWork
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response bulkUpload(@PathParam("status") RequestStatus requestStatus, @FormDataParam("file") InputStream fileAsInputStream) throws ApiException {
        RegisteredDictionary dictionary = validateAndGetDictionary();
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.putSingle("Content-Disposition", "attachment; filename=\"" + "BulkUpload-" + requestStatus + "-Response-" + (DateTime.now().toString("dd MMMM hh:mm aa")) + ".csv" + "\"");

        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(fileAsInputStream, writer, "UTF-8");
        } catch (IOException e) {
            String msg = "Unable to convert file into string. +" + e.getLocalizedMessage();
            log.error(msg, e);
            throw new ApiException(Response.Status.INTERNAL_SERVER_ERROR, msg, e);
        }
        String fileAsString = writer.toString();
        Set<String> phrases = Sets.newHashSet(Splitter.on("\n").omitEmptyStrings().trimResults().split(fileAsString));

        UnitOfWorkAwareStreamingOutput streamingOutput = new UnitOfWorkAwareProxyFactory(hibernateBundle)
                .create(UnitOfWorkAwareStreamingOutput.class);
        streamingOutput.setParameters(languageToolService, phrases, requestStatus, dictionary.getShortCode(), registeredDictionaryDao);


        return Response.ok().entity(streamingOutput).replaceAll(headers).build();
    }
}
