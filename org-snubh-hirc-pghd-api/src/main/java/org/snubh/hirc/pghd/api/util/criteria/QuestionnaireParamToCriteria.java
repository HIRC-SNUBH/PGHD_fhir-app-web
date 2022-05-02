package org.snubh.hirc.pghd.api.util.criteria;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.snubh.hirc.pghd.api.dao.i.BaseParamToCriteria;
import org.snubh.hirc.pghd.api.dao.i.IParamToCriteria;
import org.snubh.hirc.pghd.api.dto.ObservationQuestionnaireDto;
import org.snubh.hirc.pghd.api.util.ValueSet;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;

@Component
public class QuestionnaireParamToCriteria extends BaseParamToCriteria implements IParamToCriteria {

	@Override
	public void addParamToCriteria(SearchParameterMap searchParameterMap, DetachedCriteria criteria) {
		DetachedCriteria obvCriteria = null;

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
				if ("status".equals(columnName)) {
					convertStatusToCriteria(criteria, queryParamList, queryParamType, columnName);
				} else if ("obvConcept.conceptId".equals(columnName)) {
					obvCriteria = DetachedCriteria.forClass(ObservationQuestionnaireDto.class);
					obvCriteria.setProjection(Property.forName("questionnaireResponse"));
					conceptParamToCriteria("obvConcept", queryParamType, obvCriteria, queryParamList, columnName);
					criteria.add(Property.forName("identifier").in(obvCriteria));
				} else {
					tokenParamToCriteria(columnName, queryParamList, criteria);
				}
			} else if (queryParamType instanceof StringParam) {
				stringParamToCriteria(columnName, queryParamList, criteria);
			}
		}

		if (obvCriteria != null) {
			createAliasSetToAlias(obvCriteria);
		}

	}

	private void convertStatusToCriteria(DetachedCriteria criteria, List<List<IQueryParameterType>> queryParamList, IQueryParameterType queryParamType, String columnName) {
		TokenParam token = (TokenParam) queryParamType;
		String value = token.getValue();
		if (!ValueSet.Questionnaire.ACTIVE.equals(value)) {
			criteria.add(Restrictions.eq("identifier", ValueSet.Concept.ERROR));
		}
	}

	private String matchingSearchParamNameToColumn(String searchParamName) {
		String name = null;
		switch (searchParamName) {
		case "questionnaire._id":
		case "_id":
			name = "identifier";
			break;
		case "questionnaire.status":
		case "status":
			name = "status";
			break;
		case "patient":
		case "subject":
			name = "subject.identifier";
			break;
		case "authored":
			name = "surveyEndDate";
			break;
		case "questionnaire.title":
		case "title":
			name = "surveySourceValue";
			break;
		case "encounter":
			name = "encounter";
			break;
		case "questionnaire.code":
		case "code":
			name = "obvConcept.conceptId";
			break;
		default:
			break;
		}

		return name;
	}

}