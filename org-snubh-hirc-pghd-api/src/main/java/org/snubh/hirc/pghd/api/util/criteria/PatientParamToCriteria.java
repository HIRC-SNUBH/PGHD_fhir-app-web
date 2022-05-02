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
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;

@Component
public class PatientParamToCriteria extends BaseParamToCriteria implements IParamToCriteria {

	@Override
	public void addParamToCriteria(SearchParameterMap searchParameterMap, DetachedCriteria criteria) {

		for (String searchParamName : searchParameterMap.keySet()) {
			IQueryParameterType queryParamType = searchParameterMap.get(searchParamName).get(0).get(0);
			List<List<IQueryParameterType>> queryParamList = searchParameterMap.get(searchParamName);

			String columnName = matchingSearchParamNameToColumn(searchParamName);
			if (columnName == null)
				continue;
			if (queryParamType instanceof DateParam) {
				if ("death-date".equals(searchParamName)) {
					addAliasSet(columnName);
					dateParamToCriteria(columnName, queryParamList, criteria);
				} else {
					dateParamToCriteria(columnName, queryParamList, criteria);
				}
			} else if (queryParamType instanceof ReferenceParam) {
				referenceParamToCriteria(columnName, queryParamList, criteria);
			} else if (queryParamType instanceof TokenParam) {
				tokenParamToCriteria(columnName, queryParamList, criteria);
			} else if (queryParamType instanceof StringParam) {
				if (searchParamName.contains("address")) {
					addAliasSet(columnName);
					stringParamToCriteria(columnName, queryParamList, criteria);
				} else {
					stringParamToCriteria(columnName, queryParamList, criteria);
				}
			}
		}
		createAliasSetToAlias(criteria);
	}

	private String matchingSearchParamNameToColumn(String searchParamName) {
		String name = null;
		switch (searchParamName) {
		case "_id":
		case "identifier":
			name = "identifier";
			break;
		case "organization":
			name = "organizationId";
			break;
		case "gender":
			name = "genderConcept";
			break;
		case "birthdate":
			name = "birthDate";
			break;
		case "address":
			name = "address.all";
			break;
		case "address-state":
			name = "address.state";
			break;
		case "address-city":
			name = "address.city";
			break;
		case "address-postalcode":
			name = "address.zip";
			break;
		case "death-date":
			name = "death.deathDate";
			break;
		default:
			break;
		}

		return name;
	}
}