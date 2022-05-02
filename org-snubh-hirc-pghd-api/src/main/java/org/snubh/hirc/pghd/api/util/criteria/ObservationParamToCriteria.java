package org.snubh.hirc.pghd.api.util.criteria;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.snubh.hirc.pghd.api.dao.i.BaseParamToCriteria;
import org.snubh.hirc.pghd.api.dao.i.IParamToCriteria;
import org.snubh.hirc.pghd.api.util.ValueSet;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;

public class ObservationParamToCriteria extends BaseParamToCriteria implements IParamToCriteria {

	@Override
	public void addParamToCriteria(SearchParameterMap searchParameterMap, DetachedCriteria criteria) {

		for (String searchParamName : searchParameterMap.keySet()) {
			List<List<IQueryParameterType>> queryParamList = searchParameterMap.get(searchParamName);
			IQueryParameterType queryParamType = queryParamList.get(0).get(0);
			String columnName = matchingSearchParamNameToColumn(searchParamName);
			if (columnName == null)
				continue;
			if (queryParamType instanceof DateParam) {
				if ("deviceUseStartTiming".equals(searchParamName) || "deviceUseEndTiming".equals(searchParamName)) {
					addAliasSet(columnName);
					dateParamToCriteria(columnName, queryParamList, criteria);
				} else {
					dateParamToCriteria(columnName, queryParamList, criteria);
				}
			} else if (queryParamType instanceof ReferenceParam) {
				referenceParamToCriteria(columnName, queryParamList, criteria);
			} else if (queryParamType instanceof TokenParam) {
				if ("code".equals(searchParamName)) {
					conceptParamToCriteria("obvConcept", queryParamType, criteria, queryParamList, columnName);
				} else if ("component-code".equals(searchParamName)) {
					conceptParamToCriteria("componentConcept", queryParamType, criteria, queryParamList, columnName);
				} else if ("category".equals(searchParamName)) {
					conceptParamToCriteria("obvCategory", queryParamType, criteria, queryParamList, columnName);
				} else if ("device.type".equals(searchParamName)) {
					addAliasSet("deviceExposure.deviceType");
					conceptParamToCriteria("deviceExposure.deviceType", queryParamType, criteria, queryParamList, columnName);
				} else if ("subject.gender".equals(searchParamName) || "patient.gender".equals(searchParamName)) {
					addAliasSet(columnName);
					convertTokenColumnNameToCriteria(criteria, queryParamList, queryParamType, columnName);
				} else {
					convertTokenColumnNameToCriteria(criteria, queryParamList, queryParamType, columnName);
				}
			} else if (queryParamType instanceof StringParam) {
				stringParamToCriteria(columnName, queryParamList, criteria);
			}
		}
		criteria.add(Restrictions.eq("questionnaireConcept", ValueSet.ObservationEventFieldConcept.DEVICE_EXPOSURE_FIELD));
		createAliasSetToAlias(criteria);
	}

	private void convertTokenColumnNameToCriteria(DetachedCriteria criteria, List<List<IQueryParameterType>> queryParamList, IQueryParameterType queryParamType, String columnName) {
		if ("status".equals(columnName)) {
			TokenParam token = (TokenParam) queryParamType;
			String value = token.getValue();
			if (!ValueSet.ObservationCategory.FINAL.equals(value)) {
				criteria.add(Restrictions.eq("identifier", ValueSet.Concept.ERROR));
			}
		} else {
			tokenParamToCriteria(columnName, queryParamList, criteria);
		}
	}

	private String matchingSearchParamNameToColumn(String searchParamName) {
		String columnName = null;
		switch (searchParamName) {
		case "_id":
			columnName = "identifier";
			break;
		case "status":
			columnName = "status";
			break;
		case "code":
			columnName = "obvConcept.conceptId";
			break;
		case "component-code":
			columnName = "componentConcept.conceptId";
			break;
		case "patient":
		case "subject":
			columnName = "subject.identifier";
			break;
		case "date":
			columnName = "effectiveDate";
			break;
		case "category":
			columnName = "obvCategory.conceptId";
			break;
		case "device.type":
			columnName = "deviceExposure.deviceType.conceptId";
			break;
		case "subject.gender":
		case "patient.gender":
			columnName = "subject.genderConceptId";
			break;
		case "deviceUseStartTiming":
			columnName = "deviceExposure.deviceExposureStartDate";
			break;
		case "deviceUseEndTiming":
			columnName = "deviceExposure.deviceExposureEndDate";
			break;
		default:
			break;
		}

		return columnName;
	}

	@Override
	public void addAlias(String associationPath) {
		addAliasSet(associationPath);
	}
}