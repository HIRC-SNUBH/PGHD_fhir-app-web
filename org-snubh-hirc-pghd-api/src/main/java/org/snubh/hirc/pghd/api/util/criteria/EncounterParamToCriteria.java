package org.snubh.hirc.pghd.api.util.criteria;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.snubh.hirc.pghd.api.dao.i.BaseParamToCriteria;
import org.snubh.hirc.pghd.api.dao.i.IParamToCriteria;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;

@Component
public class EncounterParamToCriteria extends BaseParamToCriteria implements IParamToCriteria {

	@Override
	public void addParamToCriteria(SearchParameterMap searchParameterMap, DetachedCriteria criteria) {

		for (String searchParamName : searchParameterMap.keySet()) {
			IQueryParameterType queryParamType = searchParameterMap.get(searchParamName).get(0).get(0);
			List<List<IQueryParameterType>> queryParamList = searchParameterMap.get(searchParamName);

			String columnName = matchingSearchParamNameToColumn(searchParamName);
			if (columnName == null)
				continue;
			if (queryParamType instanceof DateParam) {
				dateParamToCriteria(columnName, queryParamList, criteria);
			} else if (queryParamType instanceof ReferenceParam) {
				referenceParamToCriteria(columnName, queryParamList, criteria);
			} else if (queryParamType instanceof TokenParam) {
				tokenParamToCriteria(columnName, queryParamList, criteria);
			}
		}

	}

	private String matchingSearchParamNameToColumn(String searchParamName) {
		String name = null;
		switch (searchParamName) {
		case "_id":
			name = "identifier";
			break;
		case "patient":
		case "subject":
			name = "subject";
			break;
		case "class":
			name = "visitClass";
			break;
		case "status":
			name = "visitClass";
			break;
		case "service-provider":
			name = "serviceProvider";
			break;
		case "date":
			name = "period";
			break;
		default:
			break;
		}

		return name;
	}
}