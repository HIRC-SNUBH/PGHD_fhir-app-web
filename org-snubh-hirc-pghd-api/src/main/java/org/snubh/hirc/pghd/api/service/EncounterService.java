package org.snubh.hirc.pghd.api.service;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.snubh.hirc.pghd.api.dao.i.IDao;
import org.snubh.hirc.pghd.api.service.i.BaseService;
import org.snubh.hirc.pghd.api.service.i.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;

@Service("EncounterService")
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
public class EncounterService extends BaseService implements IService {

	@Autowired
	@Qualifier("visitOccurrenceDao")
	public IDao dao;

	@Override
	public Bundle searchService(HttpServletRequest theRequest, SearchParameterMap searchParamMap) throws InstantiationException, IllegalAccessException, ParseException {

		setSearchParameterMap(theRequest, searchParamMap);
		setTotalCount(dao);
		Bundle searchSetBundle = generateSearchSetBundle(dao);

		return searchSetBundle;
	}

	@Override
	public Resource readService(IdType idType) throws InstantiationException, IllegalAccessException, ParseException {
		Resource resource = dao.findById(idType.getIdPart());
		return resource;
	}
}