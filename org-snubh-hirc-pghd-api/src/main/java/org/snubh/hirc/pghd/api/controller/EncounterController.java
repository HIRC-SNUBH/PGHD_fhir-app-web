package org.snubh.hirc.pghd.api.controller;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.snubh.hirc.pghd.api.service.i.IService;
import org.snubh.hirc.pghd.api.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Offset;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SearchTotalModeEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.param.DateAndListParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Controller
public class EncounterController implements IResourceProvider {

	@Autowired
	@Qualifier("EncounterService")
	private IService service;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Encounter.class;
	}

	@Autowired
	private LogUtil logUtil;

	@Search
	public Bundle search(@OptionalParam(name = Encounter.SP_RES_ID) TokenAndListParam theId, @OptionalParam(name = Encounter.SP_SERVICE_PROVIDER) ReferenceAndListParam theServiceProvider,
			@OptionalParam(name = Encounter.SP_PART_OF) ReferenceAndListParam thePartOf, @OptionalParam(name = Encounter.SP_STATUS) TokenAndListParam theStatus, @OptionalParam(name = Encounter.SP_CLASS) TokenAndListParam theClass,
			@OptionalParam(name = Encounter.SP_SUBJECT) ReferenceAndListParam theSubject, @OptionalParam(name = Encounter.SP_PATIENT) ReferenceAndListParam thePatient, @OptionalParam(name = Encounter.SP_DATE) DateAndListParam thePeriod,
			@Offset Integer theOffset, @Count Integer theCount, @Sort SortSpec theSort, @OptionalParam(name = "_summary") SummaryEnum summaryEnum, @OptionalParam(name = "_total") SearchTotalModeEnum searchTotalEnum,
			@OptionalParam(name = "_lastUpdated") DateRangeParam theLastUpdated, HttpServletRequest request) throws InstantiationException, IllegalAccessException, ParseException {

		Bundle bundle = null;

		try {
			SearchParameterMap searchParam = setSearchParameterMap(theId, theServiceProvider, thePartOf, theStatus, theClass, theSubject, thePatient, thePeriod, theOffset, theCount, theSort, summaryEnum, searchTotalEnum, theLastUpdated);
			bundle = service.searchService(request, searchParam);
		} catch (Exception e) {
			logUtil.write(e);
			throw e;
		}

		return bundle;
	}

	@Read
	public Resource read(@IdParam IdType theId) throws Exception {
		Resource resource = null;
		try {
			resource = service.readService(theId);
		} catch (Exception e) {
			logUtil.write(e);
			throw e;
		}
		return resource;

	}

	private SearchParameterMap setSearchParameterMap(TokenAndListParam theId, ReferenceAndListParam theServiceProvider, ReferenceAndListParam thePartOf, TokenAndListParam theStatus, TokenAndListParam theClass,
			ReferenceAndListParam theSubject, ReferenceAndListParam thePatient, DateAndListParam thePeriod, Integer theOffset, Integer theCount, SortSpec theSort, SummaryEnum summaryEnum, SearchTotalModeEnum searchTotalEnum,
			DateRangeParam theLastUpdated) {
		SearchParameterMap searchParam = new SearchParameterMap();
		searchParam.add(Encounter.SP_RES_ID, theId);
		searchParam.add(Encounter.SP_SERVICE_PROVIDER, theServiceProvider);
		searchParam.add(Encounter.SP_PART_OF, thePartOf);
		searchParam.add(Encounter.SP_STATUS, theStatus);
		searchParam.add(Encounter.SP_CLASS, theClass);
		searchParam.add(Encounter.SP_SUBJECT, theSubject);
		searchParam.add(Encounter.SP_PATIENT, thePatient);
		searchParam.add(Encounter.SP_DATE, thePeriod);

		searchParam.setLastUpdated(theLastUpdated);

		searchParam.setCount(theCount);// Integer
		searchParam.setOffset(theOffset);// Integer
		searchParam.setSort(theSort);
		searchParam.setSummaryMode(summaryEnum);
		searchParam.setSearchTotalMode(searchTotalEnum);

		return searchParam;
	}
}
