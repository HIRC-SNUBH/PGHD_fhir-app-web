package org.snubh.hirc.pghd.api.dao.i;

import org.hibernate.criterion.DetachedCriteria;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;

public interface IParamToCriteria {

	void addParamToCriteria(SearchParameterMap searchParameterMap, DetachedCriteria criteria);

	default void addAlias(String associationPath) {
	}
}
