package org.snubh.hirc.pghd.api.dao.i;

import java.text.ParseException;
import java.util.List;

import org.hl7.fhir.r4.model.DomainResource;
import org.springframework.orm.hibernate5.HibernateTemplate;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;

public interface IDao {

	List<? extends DomainResource> findBySearchParameter(SearchParameterMap searchParameterMap, int count, int offset) throws InstantiationException, IllegalAccessException;

	default List<? extends DomainResource> findBySearchParameterComposition(SearchParameterMap searchParameterMap, int count, int offset) throws InstantiationException, IllegalAccessException {
		return null;
	}

	<T> T findById(String id) throws InstantiationException, IllegalAccessException, ParseException;

	Long count(SearchParameterMap searchParameterMap) throws InstantiationException, IllegalAccessException;

	default Long countComposition(SearchParameterMap searchParameterMap) throws InstantiationException, IllegalAccessException, ParseException {
		return 0L;
	}

	HibernateTemplate getHibernateTemplate();
}