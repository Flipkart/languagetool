package com.flipkart.cs.languagetool.service.models.dao;

import com.flipkart.cs.languagetool.service.models.domain.RegisteredDictionary;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;



/**
 * Created by anmol.kapoor on 04/01/17.
 */
public class RegisteredDictionaryDao extends AbstractDAO<RegisteredDictionary> {
    @Inject
    public RegisteredDictionaryDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<RegisteredDictionary> findById(String id)
    {
        return Optional.fromNullable( get(id));
    }
}
