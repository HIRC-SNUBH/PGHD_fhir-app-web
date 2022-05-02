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
public class DeviceParamToCriteria extends BaseParamToCriteria implements IParamToCriteria {

	@Override
	public void addParamToCriteria(SearchParameterMap searchParameterMap, DetachedCriteria criteria) {

		for (String searchParamName : searchParameterMap.keySet()) {
			List<List<IQueryParameterType>> queryParamList = searchParameterMap.get(searchParamName);
			IQueryParameterType queryParamType = queryParamList.get(0).get(0);

			String columnName = matchingSearchParamNameToColumn(searchParamName);
			if (columnName == null)
				continue;
			if (queryParamType instanceof DateParam) {
				dateParamToCriteria(columnName, queryParamList, criteria);
			} else if (queryParamType instanceof ReferenceParam) {
				referenceParamToCriteria(columnName, queryParamList, criteria);
			} else if (queryParamType instanceof TokenParam) {
				if ("device.type".equals(searchParamName)) {
					conceptParamToCriteria("deviceType", queryParamType, criteria, queryParamList, columnName);
				} else {
					tokenParamToCriteria(columnName, queryParamList, criteria);
				}
			} else if (queryParamType instanceof StringParam) {
				stringParamToCriteria(columnName, queryParamList, criteria);
			}
		}
		createAliasSetToAlias(criteria);
	}

	private String matchingSearchParamNameToColumn(String searchParamName) {
		String columnName = null;
		switch (searchParamName) {
		case "_id":
			columnName = "identifier";
			break;
		case "patient":
		case "subject":
			columnName = "patient";
			break;
		case "device.type":
			columnName = "deviceType.conceptId";
			break;
		default:
			break;
		}

		return columnName;
	}
}