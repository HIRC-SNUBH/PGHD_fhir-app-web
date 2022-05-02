package org.snubh.hirc.pghd.api.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.Type;
import org.hl7.fhir.r4.model.Observation;
import org.snubh.hirc.pghd.api.dao.i.BaseDao;
import org.snubh.hirc.pghd.api.dao.i.IDao;
import org.snubh.hirc.pghd.api.dao.i.IParamToCriteria;
import org.snubh.hirc.pghd.api.dto.ObservationDto;
import org.snubh.hirc.pghd.api.dto.ObservationMaxDto;
import org.snubh.hirc.pghd.api.util.ValueSet;
import org.snubh.hirc.pghd.api.util.convert.ObservationComponentConvert;
import org.snubh.hirc.pghd.api.util.convert.ObservationConvert;
import org.snubh.hirc.pghd.api.util.criteria.ObservationParamToCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;

@Repository("observationDao")
@Transactional(readOnly = true)
public class ObservationDao extends BaseDao<ObservationDto> implements IDao {

	@Autowired
	private ObservationConvert resourceConvertor;

	@Autowired
	private ObservationComponentConvert resourceComponentConvertor;

	public ObservationDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public List<Observation> findBySearchParameter(SearchParameterMap searchParameterMap, int count, int offset) throws InstantiationException, IllegalAccessException {

		IParamToCriteria paramToCriteria = new ObservationParamToCriteria();

		DetachedCriteria criteria = getCriteria();
		String containedString = extractContainedString(searchParameterMap);
		paramToCriteria.addParamToCriteria(searchParameterMap, criteria);

		List<ObservationDto> dtoList = findByCriteriaList(criteria, offset, count);
		if (dtoList == null) {
			dtoList = new ArrayList<ObservationDto>();
		}

		List<Observation> resultList = new ArrayList<Observation>();

		for (ObservationDto dto : dtoList) {
			Observation observation = resourceConvertor.convert(dto, containedString);
			resultList.add(observation);
		}

		return resultList;
	}

	public List<Observation> findBySearchParameterComposition(SearchParameterMap searchParameterMap, int count, int offset) throws InstantiationException, IllegalAccessException {

		IParamToCriteria paramToCriteria = new ObservationParamToCriteria();

		DetachedCriteria criteria = getCriteria();

		setComponentCriteria(criteria);
		String containedString = extractContainedString(searchParameterMap);

		paramToCriteria.addAlias("obvConcept.Composition");

		paramToCriteria.addParamToCriteria(searchParameterMap, criteria);

		criteria.setResultTransformer(Transformers.aliasToBean(ObservationMaxDto.class));

		List<ObservationMaxDto> dtoList = findByCriteriaList(criteria, offset, count);
		if (dtoList == null) {
			dtoList = new ArrayList<ObservationMaxDto>();
		}

		List<Observation> resultList = new ArrayList<Observation>();

		for (ObservationMaxDto dto : dtoList) {
			ObservationMaxDto maxDto = getHibernateTemplate().get(ObservationMaxDto.class, dto.getIdentifier());
			resultList.add(resourceComponentConvertor.convert(maxDto, containedString));
		}

		return resultList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Observation findById(String id) throws InstantiationException, IllegalAccessException, ParseException {

		DetachedCriteria criteria = getCriteria();
		Observation result = null;

		if (id.contains("-")) {
			result = resourceComponentConvertor.convert(findByComposition(criteria, id));
		} else {
			Long identifier = Long.parseLong(id);

			criteria.add(Restrictions.eq("identifier", identifier));

			result = resourceConvertor.convert(findByCriteria(criteria));
		}

		return result;
	}

	public Long countComposition(SearchParameterMap searchParameterMap) throws InstantiationException, IllegalAccessException, ParseException {

		IParamToCriteria paramToCriteria = new ObservationParamToCriteria();

		DetachedCriteria subCriteria = getCriteria();

		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.sqlGroupProjection("max(OBSERVATION_ID)", "this_.PERSON_ID, this_.OBSERVATION_CONCEPT_ID, this_.OBSERVATION_DATE", new String[] { "identifier" }, new Type[] { org.hibernate.type.LongType.INSTANCE }));
		subCriteria.setProjection(projections);
		convertComponentIdToParam(searchParameterMap);

		paramToCriteria.addParamToCriteria(searchParameterMap, subCriteria);

		DetachedCriteria criteria = getCriteria("obvCount");
		criteria.add(Subqueries.propertyIn("identifier", subCriteria));
		return findByCriteriaCount(criteria);
	}

	@Override
	public Long count(SearchParameterMap searchParameterMap) throws InstantiationException, IllegalAccessException {

		IParamToCriteria paramToCriteria = new ObservationParamToCriteria();

		DetachedCriteria criteria = getCriteria();
		paramToCriteria.addParamToCriteria(searchParameterMap, criteria);

		return findByCriteriaCount(criteria);
	}

	@SuppressWarnings("unchecked")
	private ObservationMaxDto findByComposition(DetachedCriteria criteria, String id) throws ParseException {
		criteria.createAlias("obvConcept", "obvConcept", JoinType.INNER_JOIN);

		setComponentCriteria(criteria);

		String[] compositionKey = id.split("-");
		if (compositionKey.length != 3) {
			return null;
		}
		Date date = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).parse(compositionKey[2]);
		criteria.add(Restrictions.eq("subject.identifier", Long.parseLong(compositionKey[0])));
		criteria.add(Restrictions.eq("obvConcept.conceptId", Long.parseLong(compositionKey[1])));
		criteria.add(Restrictions.eq("effectiveDate", date));

		criteria.setResultTransformer(Transformers.aliasToBean(ObservationMaxDto.class));

		List<ObservationMaxDto> list = (List<ObservationMaxDto>) getHibernateTemplate().findByCriteria(criteria);
		ObservationMaxDto resultDto = null;
		for (ObservationMaxDto observationMaxDto : list) {
			resultDto = getHibernateTemplate().get(ObservationMaxDto.class, observationMaxDto.getIdentifier());
		}

		return resultDto;
	}

	private void setComponentCriteria(DetachedCriteria criteria) {

		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.groupProperty("subject.identifier").as("subject"));
		projections.add(Projections.groupProperty("obvConcept.conceptId").as("obsConceptId"));
		projections.add(Projections.groupProperty("effectiveDate").as("effectiveDate"));
		projections.add(Projections.max("identifier").as("identifier"));

		criteria.setProjection(projections);
	}

	private String extractContainedString(SearchParameterMap searchParameterMap) {
		List<List<IQueryParameterType>> param = searchParameterMap.get("_contained");
		String containedParamValue = "";
		if (param != null) {
			StringParam stringParam = (StringParam) param.get(0).get(0);
			containedParamValue = stringParam.getValue();
		}

		return containedParamValue;
	}

	private void convertComponentIdToParam(SearchParameterMap searchParameterMap) throws ParseException {
		List<List<IQueryParameterType>> param = searchParameterMap.get("_id");
		if (param == null) {
			return;
		}

		TokenParam tokenParam = (TokenParam) param.get(0).get(0);
		String componentId = tokenParam.getValue();

		if (!componentId.contains("-")) {
			return;
		}

		String[] compId = componentId.split("-");
		if (compId.length != 3) {
			return;
		}

		searchParameterMap.remove("_id");
		String date = compId[2];
		SimpleDateFormat searchFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date searchDate = searchFormat.parse(date);
		String dateValue = dbFormat.format(searchDate);
		searchParameterMap.add("patient", new TokenParam(compId[0]));
		searchParameterMap.add("code", new TokenParam(ValueSet.Concept.CONCEPT_ID_SYSTEM, compId[1]));
		searchParameterMap.add("date", new DateParam(ParamPrefixEnum.EQUAL, dateValue));
	}

}