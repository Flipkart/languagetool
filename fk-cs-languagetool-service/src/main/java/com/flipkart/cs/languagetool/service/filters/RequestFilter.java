package com.flipkart.cs.languagetool.service.filters;

import com.flipkart.cs.languagetool.service.models.dtos.RequestHeaders;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import org.glassfish.jersey.message.internal.ReaderWriter;
import org.glassfish.jersey.server.ContainerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by anmol.kapoor on 03/01/17.
 */
@Provider
public class RequestFilter implements ContainerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(RequestFilter.class);
    public final static String TRANSACTION_ID_HEADER = "X_TRANSACTION_ID";
    public final static String CLIENT_ID = "X_LANGUAGETOOL_CLIENT_ID";
    public final String USER = "X_LANGUAGETOOL_USERNAME";
    public final String DICTIONARY = "X_LANGUAGETOOL_DICTIONARY";


    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        setTransactionId(containerRequestContext);
        MultivaluedMap<String, String> headers = containerRequestContext.getHeaders();
        if(!containerRequestContext.getMethod().toLowerCase().equals("options")) {
            populateRequestThreadContext(headers);
        }
        logRequest(containerRequestContext);
    }

    private void logRequest(ContainerRequestContext containerRequestContext) {
        logger.info("{} | {} ", containerRequestContext.getMethod(), containerRequestContext.getUriInfo().getAbsolutePath().toString());

        logger.info("Headers : " + Joiner.on(",").withKeyValueSeparator(":").join(containerRequestContext.getHeaders()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = containerRequestContext.getEntityStream();
        try {
            if (in.available() > 0) {
                ReaderWriter.writeTo(in, out);

                byte[] requestEntity = out.toByteArray();
                logger.info("Request body: {} ", out);
                containerRequestContext.setEntityStream(new ByteArrayInputStream(requestEntity));
            }

        } catch (IOException ex) {
            throw new ContainerException(ex);
        }
    }

    private void populateRequestThreadContext(MultivaluedMap<String, String> headers) {
        Optional<String> clientId = Optional.fromNullable(headers.getFirst(CLIENT_ID));
        Optional<String> username = Optional.fromNullable(headers.getFirst(USER));
        Optional<String> dictionary = Optional.fromNullable(headers.getFirst(DICTIONARY));

        if (!clientId.isPresent() || !username.isPresent()) {
            throw new RuntimeException(" Both X_LANGUAGETOOL_CLIENT_ID and X_LANGUAGETOOL_USERNAME are required headers to access LANGUAGETOOL apis");
        }
        if (!dictionary.isPresent()) {
            logger.error("No dictionary X_LANGUAGETOOL_DICTIONARY  header is found for client :  " + clientId.get() + ", username : " + username.get());
            throw new RuntimeException(DICTIONARY + " header is missing in the request.");
        }

        RequestHeaders.get().setClientId(clientId.get()).setUser(username.get()).setDictionary(dictionary.get());
    }

    private void setTransactionId(ContainerRequestContext containerRequestContext) {
        MultivaluedMap<String, String> headers = containerRequestContext.getHeaders();
        String transactionId = UUID.randomUUID().toString() + " - " + MachineHelper.getMachineName();
        org.slf4j.MDC.put("id", transactionId);
        logger.info("Transaction Id generated : " + transactionId);
        printOldTxnId(headers, transactionId);
    }


    private void printOldTxnId(MultivaluedMap<String, String> headers, String newTransactionId) {
        String oldTransactionId = headers.getFirst(TRANSACTION_ID_HEADER);
        if (oldTransactionId == null || oldTransactionId.isEmpty()) {
            return;
        }
        logger.info("Generated a new txn id : " + newTransactionId + ", For the txn id : " + oldTransactionId);
    }
}
