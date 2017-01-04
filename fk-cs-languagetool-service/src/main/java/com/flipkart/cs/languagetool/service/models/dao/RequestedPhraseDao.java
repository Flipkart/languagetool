package com.flipkart.cs.languagetool.service.models.dao;

import com.flipkart.cs.languagetool.service.models.domain.RequestedPhrase;
import com.flipkart.cs.languagetool.service.models.dtos.RequestHeaders;
import com.google.inject.Inject;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by anmol.kapoor on 03/01/17.
 */
public class RequestedPhraseDao extends AbstractDAO<RequestedPhrase> {
    @Inject
    public RequestedPhraseDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public List<RequestedPhrase> findByIds(Set<String> phrases) {
        Criteria criteria = currentSession().createCriteria(RequestedPhrase.class);
        criteria.createAlias("registeredDictionaryList", "dic");
        criteria.add(
                Restrictions.and(Restrictions.eq("dic.shortCode", RequestHeaders.get().getDictionary()),
                        Restrictions.in("phrase", phrases))
        );
        List<RequestedPhrase> list = criteria.list();
        if(list == null || list.isEmpty())
        {
            return new ArrayList<RequestedPhrase>();
        }else
        {
            return list;
        }

    }

    public RequestedPhrase save(RequestedPhrase requestedPhrase) {
        requestedPhrase.setModifiedAt(DateTime.now());
        if(requestedPhrase.getCreatedAt()==null)
        {
            requestedPhrase.setCreatedAt(requestedPhrase.getModifiedAt());
        }
        requestedPhrase.setModifiedByUser(RequestHeaders.get().getUser());
        if(requestedPhrase.getCreatedByUser() == null)
        {
            requestedPhrase.setCreatedByUser(requestedPhrase.getModifiedByUser());
        }
            requestedPhrase.setModifiedBySystem(RequestHeaders.get().getClientId());
        if(requestedPhrase.getCreatedBySystem() == null)
        {
            requestedPhrase.setCreatedBySystem(requestedPhrase.getModifiedBySystem());
        }
        return persist(requestedPhrase);
    }
}
