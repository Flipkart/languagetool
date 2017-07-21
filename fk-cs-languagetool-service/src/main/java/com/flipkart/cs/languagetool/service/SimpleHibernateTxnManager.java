package com.flipkart.cs.languagetool.service;


import com.flipkart.cs.languagetool.service.exception.ApiException;
import com.google.inject.Inject;
import io.dropwizard.hibernate.HibernateBundle;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by anmol.kapoor on 04/05/16.
 */
public class SimpleHibernateTxnManager {

    private final HibernateBundle hibernateBundle;
    private static final Logger logger = LoggerFactory.getLogger(SimpleHibernateTxnManager.class);
    private SessionFactory sessionFactory;
    private Session session1;

    @Inject
    public SimpleHibernateTxnManager(HibernateBundle hibernateBundle) {
        this.hibernateBundle = hibernateBundle;
    }

    private boolean isSessionCreatedAndBinded = false;
    public Session createAndBindHibernateSession() throws ApiException {
        if(isSessionCreatedAndBinded)
        {
            String msg = "A session is already binded using this TxnMgr, create a new object and manage.";
            logger.error(msg);
            throw new ApiException(msg);
        }
        SessionFactory sessionFactory = hibernateBundle.getSessionFactory();
        Session session1 = sessionFactory.openSession();
        ManagedSessionContext.bind(session1);
        session1.beginTransaction();
        isSessionCreatedAndBinded = true;
        this.session1 = session1;
        this.sessionFactory = sessionFactory;
        return session1;
    }

    public boolean closeSessionCommitOrRollBackTxn(boolean exceptionOccurred)
    {
        final Transaction txn = session1.getTransaction();
        if (exceptionOccurred) {
            txn.rollback();
        } else if (txn != null) {
            try {
                txn.commit();
            } catch (Exception e) {
                logger.error("not committing transaction", e);
                txn.rollback();
            }
        }
        session1.close();
        ManagedSessionContext.unbind(sessionFactory);
        return true;
    }

}
