package org.snubh.hirc.pghd.api.dao;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hl7.fhir.r4.model.Organization;
import org.snubh.hirc.pghd.api.dao.i.BaseDao;
import org.snubh.hirc.pghd.api.dao.i.IDao;
import org.snubh.hirc.pghd.api.dao.i.IParamToCriteria;
import org.snubh.hirc.pghd.api.dto.CareSiteDto;
import org.snubh.hirc.pghd.api.util.convert.OrganizationConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;

@Repository("careSiteDao")
@Transactional(readOnly = true)
public class CareSiteDao extends BaseDao<CareSiteDto> implements IDao {

	@Resource(name = "organizationParamToCriteria")
	private IParamToCriteria paramToCriteria;

	@Autowired
	private OrganizationConvert resourceConvertor;

	public CareSiteDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public List<Organization> findBySearchParameter(SearchParameterMap searchParameterMap, int count, int offset)
			throws InstantiationException, IllegalAccessException {

		DetachedCriteria criteria = getCriteria();
		paramToCriteria.addParamToCriteria(searchParameterMap, criteria);

		List<CareSiteDto> dtoList = findByCriteriaList(criteria, offset, count);

		return resourceConvertor.convertList(dtoList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Organization findById(String id) throws InstantiationException, IllegalAccessException, ParseException {

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