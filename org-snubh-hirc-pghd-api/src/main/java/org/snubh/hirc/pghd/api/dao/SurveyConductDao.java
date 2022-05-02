package org.snubh.hirc.pghd.api.dao;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hl7.fhir.r4.model.Questionnaire;
import org.snubh.hirc.pghd.api.dao.i.BaseDao;
import org.snubh.hirc.pghd.api.dao.i.IDao;
import org.snubh.hirc.pghd.api.dao.i.IParamToCriteria;
import org.snubh.hirc.pghd.api.dto.QuestionnaireResponseDto;
import org.snubh.hirc.pghd.api.util.convert.QuestionnaireConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;

@Repository("surveyConductDao")
@Transactional(readOnly = true)
public class SurveyConductDao extends BaseDao<QuestionnaireResponseDto> implements IDao {

	@Resource(name = "questionnaireParamToCriteria")
	private IParamToCriteria paramToCriteria;

	@Autowired
	private QuestionnaireConvert resourceConvertor;

	public SurveyConductDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public List<Questionnaire> findBySearchParameter(SearchParameterMap searchParameterMap, int count, int offset)
			throws InstantiationException, IllegalAccessException {

		DetachedCriteria criteria = getCriteria();
		paramToCriteria.addParamToCriteria(searchParameterMap, criteria);

		List<QuestionnaireResponseDto> resultList = findByCriteriaList(criteria, offset, count);

		return resourceConvertor.convertList(resultList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Questionnaire findById(String id) throws InstantiationException, IllegalAccessException, ParseException {

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