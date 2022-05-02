package org.snubh.hirc.pghd.api.dao.i;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.snubh.hirc.pghd.api.util.ValueSet;

import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;

public class BaseParamToCriteria {

	private Set<String> aliasSet = new HashSet<String>();

	public Set<String> getAliasSet() {
		return aliasSet;
	}

	public void addAliasSet(String associationPath) {
		if (associationPath == null || associationPath.equals("")) {
			return;
		}

		if (!associationPath.contains(".")) {
			return;
		}

		aliasSet.add(associationPath.substring(0, associationPath.lastIndexOf('.')));
	}

	public void addAliasSet(String associationPath, String alias) {
		if (associationPath == null || associationPath.equals("")) {
			return;
		}

		if (!associationPath.contains(".")) {
			return;
		}

		aliasSet.add(String.format("%s|%s", associationPath.substring(0, associationPath.lastIndexOf('.')), alias));
	}

	public void dateParamToCriteria(String columnName, List<List<IQueryParameterType>> queryParamList, DetachedCriteria criteria) {
		for (List<IQueryParameterType> subQueryList : queryParamList) {
			Disjunction orClause = Restrictions.disjunction();
			for (IQueryParameterType query : subQueryList) {
				DateParam dataQuery = (DateParam) query;
				Criterion criterion = null;
				Date value = dataQuery.getValue();
				ParamPrefixEnum prefix = dataQuery.getPrefix();
				if ("period".equals(columnName)) {
					criterion = convertPeriodParamToCriterion(value, prefix);
				} else {
					criterion = convertDateParamToCriterion(columnName, value, prefix);
				}

				orClause.add(criterion);
			}
			criteria.add(orClause);
		}
	}

	private Criterion convertPeriodParamToCriterion(Date value, ParamPrefixEnum prefix) {
		Criterion criterion = null;
		switch (prefix) {
		case EQUAL:
			criterion = Restrictions.and(Restrictions.le("periodStart", value), Restrictions.ge("periodEnd", value));
			break;
		case LESSTHAN:
			criterion = Restrictions.lt("periodStart", value);
			break;
		case LESSTHAN_OR_EQUALS:
			criterion = Restrictions.le("periodStart", value);
			break;
		case GREATERTHAN:
			criterion = Restrictions.gt("periodEnd", value);
			break;
		case GREATERTHAN_OR_EQUALS:
			criterion = Restrictions.ge("periodEnd", value);
			break;
		default:
			break;
		}

		return criterion;
	}

	private Criterion convertDateParamToCriterion(String columnName, Date value, ParamPrefixEnum prefix) {
		Criterion criterion = null;
		switch (prefix) {
		case EQUAL:
			criterion = Restrictions.eq(columnName, value);
			break;
		case LESSTHAN_OR_EQUALS:
			criterion = Restrictions.le(columnName, value);
			break;
		case LESSTHAN:
			criterion = Restrictions.lt(columnName, value);
			break;
		case GREATERTHAN_OR_EQUALS:
			criterion = Restrictions.ge(columnName, value);
			break;
		case GREATERTHAN:
			criterion = Restrictions.gt(columnName, value);
			break;
		default:
			break;
		}
		return criterion;
	}

	public void referenceParamToCriteria(String columnName, List<List<IQueryParameterType>> queryParamList, DetachedCriteria criteria) {
		for (List<IQueryParameterType> subQueryList : queryParamList) {
			Disjunction orClause = Restrictions.disjunction();
			for (IQueryParameterType query : subQueryList) {
				ReferenceParam referenceParam = (ReferenceParam) query;
				Long value = referenceParam.getIdPartAsLong();
				orClause.add(Restrictions.eq(columnName, value));
			}
			criteria.add(orClause);
		}
	}

	public void stringParamToCriteria(String columnName, List<List<IQueryParameterType>> queryParamList, DetachedCriteria criteria) {
		for (List<IQueryParameterType> subQueryList : queryParamList) {
			Disjunction orClause = Restrictions.disjunction();
			for (IQueryParameterType query : subQueryList) {
				StringParam stringParam = (StringParam) query;
				if ("address.all".equals(columnName)) {
					orClause.add(convertStringParamToCriterion(stringParam, "address.state"));
					orClause.add(convertStringParamToCriterion(stringParam, "address.city"));
					orClause.add(convertStringParamToCriterion(stringParam, "address.zip"));
					orClause.add(convertStringParamToCriterion(stringParam, "address.addressLine"));
				} else {
					orClause.add(convertStringParamToCriterion(stringParam, columnName));
				}
			}
			criteria.add(orClause);
		}
	}

	public void tokenParamToCriteria(String columnName, List<List<IQueryParameterType>> queryParamList, DetachedCriteria criteria) {
		for (List<IQueryParameterType> subQueryList : queryParamList) {
			Disjunction orClause = Restrictions.disjunction();
			for (IQueryParameterType query : subQueryList) {
				TokenParam tokenParam = (TokenParam) query;
				String codeValue = tokenParam.getValue();
				String systemValue = tokenParam.getSystem();
				orClause.add(convertTokenParamToCriterion(codeValue, systemValue, columnName, criteria));
			}

			criteria.add(orClause);
		}
	}

	public void conceptParamToCriteria(String joinProperty, IQueryParameterType queryParamType, DetachedCriteria criteria, List<List<IQueryParameterType>> queryParamList, String columnName) {
		TokenParam tokenParam = (TokenParam) queryParamType;
		String system = tokenParam.getSystem();

		if ("".equals(system) || system == null) {
			searchJoinCode(criteria, queryParamList, joinProperty);
		} else if (!ValueSet.Concept.CONCEPT_ID_SYSTEM.equals(system)) {
			searchJoinSystemAndCode(criteria, queryParamList, joinProperty);
		} else {
			tokenParamToCriteria(columnName, queryParamList, criteria);
		}
	}

	private String convertSystemValue(String system) {
		String result = "";

		if (system != null) {
			switch (system) {
			case ValueSet.Concept.OMOP_SYSTEM:
				result = ValueSet.Concept.OMOP_SYSTEM_STR;
				break;
			case ValueSet.Concept.SNUBH_SYSTEM:
				result = ValueSet.Concept.SNUBH_SYSTEYM_STR;
				break;
			default:
				result = system;
				break;
			}
		}

		return result;
	}

	public void searchJoinCode(DetachedCriteria criteria, List<List<IQueryParameterType>> queryParamList, String joinColumnName) {
		String codePath = String.format("%s.conceptCode", joinColumnName);
		String alias = "";

		if (!joinColumnName.contains(".")) {
			alias = joinColumnName;
			addAliasSet(codePath);
		} else {
			alias = joinColumnName.substring(joinColumnName.lastIndexOf(".") + 1);
			addAliasSet(codePath, alias);
		}

		String columnName = String.format("%s.conceptCode", alias);
		tokenParamToCriteria(columnName, queryParamList, criteria);
	}

	public void searchJoinSystemAndCode(DetachedCriteria criteria, List<List<IQueryParameterType>> queryParamList, String joinColumnName) {
		String codeAssociationPath = String.format("%s.conceptCode", joinColumnName);
		String codeAlias = "";
		if (!joinColumnName.contains(".")) {
			codeAlias = joinColumnName;
			addAliasSet(codeAssociationPath);
		} else {
			codeAlias = joinColumnName.substring(joinColumnName.lastIndexOf(".") + 1);
			addAliasSet(codeAssociationPath, codeAlias);
		}
		String codeColumnName = String.format("%s.conceptCode", codeAlias);
		tokenParamToCriteria(codeColumnName, queryParamList, criteria);

		String codeSystemPath = "codeSystemId.codeSystem";
		String systemAlias = String.format("%s%s", codeAlias, "codeSystemId");

		addAliasSet(String.format("%s.%s", joinColumnName, codeSystemPath), systemAlias);

		String codeSystemColumnName = String.format("%s.%s", systemAlias, "codeSystem");
		tokenParamToCriteria(codeSystemColumnName, queryParamList, criteria);
	}

	private Criterion convertStringParamToCriterion(StringParam stringParam, String columnName) {
		Criterion criterion = null;
		String value = stringParam.getValue();
		if (stringParam.isExact()) {
			criterion = Restrictions.ilike(columnName, value, MatchMode.EXACT);
		} else if (stringParam.isContains()) {
			criterion = Restrictions.ilike(columnName, value, MatchMode.ANYWHERE);
		} else {
			criterion = Restrictions.ilike(columnName, value, MatchMode.START);
		}

		return criterion;
	}

	private Criterion convertTokenParamToCriterion(String codeValue, String systemValue, String columnName, DetachedCriteria criteria) {
		Criterion criterion = null;
		String system = "";
		if (columnName.contains("gender")) {
			criterion = convertGenderToCriterion(codeValue, systemValue, columnName);
		} else if ("visitClass".equals(columnName)) {
			criterion = convertEncounterClassToCriterion(codeValue, systemValue, columnName);
		} else if (columnName.contains(".conceptCode")) {
			criterion = Restrictions.eq(columnName, codeValue);
		} else if (columnName.contains(".codeSystem")) {
			system = convertSystemValue(systemValue);
			criterion = Restrictions.eq(columnName, system);
		} else {
			if (columnName.contains(".")) {
				addAliasSet(columnName);
			}
			Long localCode = Long.parseLong(codeValue);
			criterion = Restrictions.eq(columnName, localCode);
		}

		return criterion;
	}

	private Criterion convertGenderToCriterion(String codeValue, String systemValue, String columnName) {
		Long localCode = null;

		if (systemValue == null || "".equals(systemValue)) {
			return convertGenderConceptToCriterion(codeValue, columnName);
		}

		if (ValueSet.Concept.CONCEPT_ID_SYSTEM.equalsIgnoreCase(systemValue)) {
			localCode = Long.parseLong(codeValue);
		} else if (ValueSet.Gender.SYSTEM.equalsIgnoreCase(systemValue)) {
			switch (codeValue) {
			case ValueSet.Gender.MALE_CODE:
				localCode = ValueSet.Gender.M;
				break;
			case ValueSet.Gender.FEMALE_CODE:
				localCode = ValueSet.Gender.F;
				break;
			default:
				localCode = ValueSet.Concept.ERROR;
				break;
			}
		}

		Criterion criterion = Restrictions.eq(columnName, localCode);

		return criterion;
	}

	private Criterion convertGenderConceptToCriterion(String codeValue, String columnName) {
		Long localCode = null;

		switch (codeValue) {
		case ValueSet.Gender.FHIR_CODE_MALE:
			localCode = ValueSet.Gender.M;
			break;
		case ValueSet.Gender.FHIR_CODE_FEMALE:
			localCode = ValueSet.Gender.F;
			break;
		default:
			localCode = ValueSet.Concept.ERROR;
			break;
		}

		return Restrictions.eq(columnName, localCode);
	}

	private Criterion convertEncounterClassToCriterion(String codeValue, String systemValue, String columnName) {
		Long localCode = null;

		if (systemValue == null || "".equals(systemValue) || ValueSet.EncounterType.FHIR_SYSTEM.equals(systemValue)) {
			return convertEncounterClassConceptToCriterion(codeValue, columnName);
		}

		if (ValueSet.Concept.CONCEPT_ID_SYSTEM.equalsIgnoreCase(systemValue)) {
			localCode = Long.parseLong(codeValue);
		} else if (ValueSet.EncounterType.SYSTEM.equalsIgnoreCase(systemValue) && ValueSet.EncounterType.LABORATORY_CONCEPT.equals(codeValue)) {
			localCode = ValueSet.EncounterType.LABORATORY_ID;
		} else {
			localCode = ValueSet.Concept.ERROR;
		}

		Criterion criterion = Restrictions.eq(columnName, localCode);

		return criterion;
	}

	private Criterion convertEncounterClassConceptToCriterion(String codeValue, String columnName) {
		Criterion criterion = null;
		switch (codeValue) {
		case ValueSet.EncounterType.FHIR_CODE_IMP:
			criterion = Restrictions.or(Restrictions.eq(columnName, ValueSet.EncounterType.IMP), Restrictions.eq(columnName, ValueSet.EncounterType.STILL_PATIENT));
			break;
		case ValueSet.EncounterType.FHIR_CODE_AMB:
			criterion = Restrictions.eq(columnName, ValueSet.EncounterType.AMB);
			break;
		case ValueSet.EncounterType.FHIR_CODE_EMER:
			criterion = Restrictions.eq(columnName, ValueSet.EncounterType.EMER);
			break;
		case ValueSet.EncounterType.LABORATORY_CONCEPT:
			criterion = Restrictions.eq(columnName, ValueSet.EncounterType.LABORATORY_ID);
			break;

		case ValueSet.EncounterType.FHIR_CODE_IN_PROGRESS:
			criterion = Restrictions.eq(columnName, ValueSet.EncounterType.STILL_PATIENT);
			break;
		case ValueSet.EncounterType.FHIR_CODE_FINISHED:
			criterion = Restrictions.ne(columnName, ValueSet.EncounterType.STILL_PATIENT);
			break;
		default:
			criterion = Restrictions.eq(columnName, ValueSet.Concept.ERROR);
			break;
		}

		return criterion;
	}

	protected void createAliasSetToAlias(DetachedCriteria criteria) {
		for (String string : aliasSet) {
			if (string != null) {
				String associationPath = "";
				String alias = null;
				if (string.contains("|")) {
					String[] split = string.split("\\|");
					associationPath = split[0];
					alias = split[1];
				} else {
					associationPath = string;
				}
				alias = (alias == null ? associationPath : alias);
				criteria.createAlias(associationPath, alias, JoinType.LEFT_OUTER_JOIN);
			}
		}
	}

}