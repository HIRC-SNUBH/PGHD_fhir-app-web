package org.snubh.hirc.pghd.api.dao;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.snubh.hirc.pghd.api.dao.i.BaseDao;
import org.snubh.hirc.pghd.api.dao.i.IDao;
import org.snubh.hirc.pghd.api.dao.i.IParamToCriteria;
import org.snubh.hirc.pghd.api.dto.QuestionnaireResponseDto;
import org.snubh.hirc.pghd.api.util.convert.QuestionnaireResponseConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.rest.param.StringParam;

@Repository("surveyConductResponseDao")
@Transactional(readOnly = true)
public class SurveyConductResponseDao extends BaseDao<QuestionnaireResponseDto> implements IDao {

	@Resource(name = "questionnaireParamToCriteria")
	private IParamToCriteria paramToCriteria;

	@Autowired
	private QuestionnaireResponseConvert resourceConvertor;

	public SurveyConductResponseDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public List<QuestionnaireResponse> findBySearchParameter(SearchParameterMap searchParameterMap, int count,
			int offset) throws InstantiationException, IllegalAccessException {

		DetachedCriteria criteria = getCriteria();
		String containedString = extractContainedString(searchParameterMap);
		paramToCriteria.addParamToCriteria(searchParameterMap, criteria);

		List<QuestionnaireResponseDto> resultList = findByCriteriaList(criteria, offset, count);

		return resourceConvertor.convertList(resultList, containedString);
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

	@SuppressWarnings("unchecked")
	@Override
	public QuestionnaireResponse findById(String id)
			throws InstantiationException, IllegalAccessException, ParseException {

		DetachedCriteria criteria = getCriteria();

		Long identifier = Long.parseLong(id);

		criteria.add(Restrictions.eq("identifier", identifier));

		return resourceConvertor.convert(findByCriteria(criteria));
	}

	@Override
	public Long count(SearchParameterMap searchParameterMap) throws InstantiationException, IllegalAccessException {

		DetachedCriteria criteria = getCriteria();
		paramToCriteria.addParamToCriteria(searchParameterMap, criteria);

		return findByCriteriaCount(criteria);
	}

}