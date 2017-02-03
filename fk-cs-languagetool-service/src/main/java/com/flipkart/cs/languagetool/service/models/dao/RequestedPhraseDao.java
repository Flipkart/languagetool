package com.flipkart.cs.languagetool.service.models.dao;

import com.flipkart.cs.languagetool.service.models.domain.RegisteredDictionary;
import com.flipkart.cs.languagetool.service.models.domain.RequestStatus;
import com.flipkart.cs.languagetool.service.models.domain.RequestedPhrase;
import com.flipkart.cs.languagetool.service.models.dtos.OrderByParam;
import com.flipkart.cs.languagetool.service.models.dtos.OrderSeq;
import com.flipkart.cs.languagetool.service.models.dtos.Paginated;
import com.flipkart.cs.languagetool.service.models.dtos.RequestHeaders;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
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
        criteria.createAlias("associatedRegisteredDictionary", "dic");
        criteria.add(
                Restrictions.and(Restrictions.eq("dic.shortCode", RequestHeaders.get().getDictionary()),
                        Restrictions.in("phrase", phrases))
        );
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<RequestedPhrase> list = criteria.list();
        if (list == null || list.isEmpty()) {
            return new ArrayList<RequestedPhrase>();
        } else {
            return list;
        }

    }

    public RequestedPhrase save(RequestedPhrase requestedPhrase) {
        requestedPhrase.setModifiedAt(DateTime.now());
        if (requestedPhrase.getCreatedAt() == null) {
            requestedPhrase.setCreatedAt(requestedPhrase.getModifiedAt());
        }
        requestedPhrase.setModifiedByUser(RequestHeaders.get().getUser());
        if (requestedPhrase.getCreatedByUser() == null) {
            requestedPhrase.setCreatedByUser(requestedPhrase.getModifiedByUser());
        }
        requestedPhrase.setModifiedBySystem(RequestHeaders.get().getClientId());
        if (requestedPhrase.getCreatedBySystem() == null) {
            requestedPhrase.setCreatedBySystem(requestedPhrase.getModifiedBySystem());
        }
        return persist(requestedPhrase);
    }

    public Set<String> getPhrasesAsSetOfStatus(RegisteredDictionary dictionary, RequestStatus status) {
        Criteria criteria = currentSession().createCriteria(RequestedPhrase.class);
        criteria.createAlias("associatedRegisteredDictionary", "dic");
        criteria.add(
                Restrictions.and(Restrictions.eq("dic.shortCode", dictionary.getShortCode()),
                        Restrictions.in("currentStatus", status))
        );
        ProjectionList proList = Projections.projectionList();
        proList.add(Projections.property("phrase"));
        criteria.setProjection(proList);
        List<String> list = criteria.list();
        Set<String> finalList = new HashSet<>();
        if (list != null && !list.isEmpty()) {
            for (String value : list) {
                finalList.add(value);
            }

        }

        return finalList;
    }


    public Paginated<RequestedPhrase> getPhrasesForStatus(RequestStatus status, RegisteredDictionary dictionary,
                                                          Integer pageNo, Integer pageSize,
                                                          Optional<OrderByParam> orderByParam,
                                                          Optional<OrderSeq> orderSeq) {
        Criteria countCriteria = createCriteriaQueryForGetPhrasesForStatus(dictionary, status);
        countCriteria.setProjection(Projections.rowCount());
        Long total = (Long) countCriteria.uniqueResult();

        Criteria criteria = createCriteriaQueryForGetPhrasesForStatus(dictionary, status);
        if (orderByParam.isPresent()) {

            if (orderSeq.isPresent() && orderSeq.get() == OrderSeq.desc) {
                criteria.addOrder(Order.desc(orderByParam.get().getPropertyName()));
            } else {
                criteria.addOrder(Order.asc(orderByParam.get().getPropertyName()));
            }
        }
        criteria.setFirstResult((pageNo - 1) * pageSize);
        criteria.setMaxResults(pageSize);
        List<RequestedPhrase> list = criteria.list();
        if (list == null || list.isEmpty()) {
            list = new ArrayList<>();
        }
        return new Paginated<>(list, total, (long) list.size());
    }

    private Criteria createCriteriaQueryForGetPhrasesForStatus(RegisteredDictionary dictionary, RequestStatus status) {
        Criteria criteria = currentSession().createCriteria(RequestedPhrase.class);
        criteria.createAlias("associatedRegisteredDictionary", "dic");
        criteria.add(
                Restrictions.and(Restrictions.eq("dic.shortCode", dictionary.getShortCode()),
                        Restrictions.in("currentStatus", status))
        );
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria;
    }
}
