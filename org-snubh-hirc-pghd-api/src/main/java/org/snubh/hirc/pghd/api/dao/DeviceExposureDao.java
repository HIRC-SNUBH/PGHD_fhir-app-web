package org.snubh.hirc.pghd.api.dao;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hl7.fhir.r4.model.DeviceUseStatement;
import org.snubh.hirc.pghd.api.dao.i.BaseDao;
import org.snubh.hirc.pghd.api.dao.i.IDao;
import org.snubh.hirc.pghd.api.dao.i.IParamToCriteria;
import org.snubh.hirc.pghd.api.dto.DeviceExposureDto;
import org.snubh.hirc.pghd.api.util.convert.DeviceUseStatementConvert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;

@Repository("deviceExposureDao")
@Transactional(readOnly = true)
public class DeviceExposureDao extends BaseDao<DeviceExposureDto> implements IDao {

	@Resource(name = "deviceParamToCriteria")
	private IParamToCriteria parameterToCriteria;

	@Autowired
	private DeviceUseStatementConvert resourceConvertor;

	public DeviceExposureDao(SessionFactory sessionFactory) {
		super(sessionFactory);
	}

	@Override
	public List<DeviceUseStatement> findBySearchParameter(SearchParameterMap searchParameterMap, int count, int offset)
			throws InstantiationException, IllegalAccessException {

		DetachedCriteria criteria = getCriteria();
		parameterToCriteria.addParamToCriteria(searchParameterMap, criteria);

		List<DeviceExposureDto> resultList = findByCriteriaList(criteria, offset, count);

		return resourceConvertor.convertList(resultList);
	}

	@SuppressWarnings("unchecked")
	@Override
	public DeviceUseStatement findById(String id) throws InstantiationException, IllegalAccessException {

		DetachedCriteria criteria = getCriteria();

		Long identifier = Long.parseLong(id);

		criteria.add(Restrictions.eq("identifier", identifier));

		return resourceConvertor.convert(findByCriteria(criteria));
	}

	@Override
	public Long count(SearchParameterMap searchParameterMap) throws InstantiationException, IllegalAccessException {

		DetachedCriteria criteria = getCriteria();
		parameterToCriteria.addParamToCriteria(searchParameterMap, criteria);

		return findByCriteriaCount(criteria);

	}

}