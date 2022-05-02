package org.snubh.hirc.pghd.api.controller;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Questionnaire;
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
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Controller
public class QuestionnaireController implements IResourceProvider {

	@Autowired
	@Qualifier("QuestionnaireService")
	private IService service;

	@Autowired
	private LogUtil logUtil;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Questionnaire.class;
	}

	@Search()
	public Bundle search(@OptionalParam(name = Questionnaire.SP_RES_ID) TokenAndListParam theId, @OptionalParam(name = Questionnaire.SP_STATUS) TokenAndListParam theStatus,
			@OptionalParam(name = Questionnaire.SP_CODE) TokenAndListParam theCode, @OptionalParam(name = Questionnaire.SP_TITLE) StringAndListParam theTitle, @Offset Integer theOffset, @Count Integer theCount, @Sort SortSpec theSort,
			@OptionalParam(name = "_summary") SummaryEnum summaryEnum, @OptionalParam(name = "_total") SearchTotalModeEnum searchTotalEnum, @OptionalParam(name = "_lastUpdated") DateRangeParam theLastUpdated, HttpServletRequest request)
			throws InstantiationException, IllegalAccessException, ParseException {

		Bundle bundle = null;
		try {
			SearchParameterMap searchParam = setSearchParameterMap(theId, theStatus, theCode, theTitle, theOffset, theCount, theSort, summaryEnum, searchTotalEnum, theLastUpdated);
			bundle = service.searchService(request, searchParam);
		} catch (Exception e) {
			logUtil.write(e);
			throw e;
		}
		return bundle;

	}

	@Read()
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

	private SearchParameterMap setSearchParameterMap(TokenAndListParam theId, TokenAndListParam theStatus, TokenAndListParam theCode, StringAndListParam theTitle, Integer theOffset, Integer theCount, SortSpec theSort,
			SummaryEnum summaryEnum, SearchTotalModeEnum searchTotalEnum, DateRangeParam theLastUpdated) {
		SearchParameterMap searchParam = new SearchParameterMap();
		searchParam.add(Resource.SP_RES_ID, theId);
		searchParam.add(Questionnaire.SP_STATUS, theStatus);
		searchParam.add(Questionnaire.SP_CODE, theCode);
		searchParam.add(Questionnaire.SP_TITLE, theTitle);

		searchParam.setLastUpdated(theLastUpdated);

		searchParam.setCount(theCount);// Integer
		searchParam.setOffset(theOffset);// Integer
		searchParam.setSort(theSort);
		searchParam.setSummaryMode(summaryEnum);
		searchParam.setSearchTotalMode(searchTotalEnum);

		return searchParam;
	}
}
