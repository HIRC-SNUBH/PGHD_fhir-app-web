package org.snubh.hirc.pghd.api.controller;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.snubh.hirc.pghd.api.service.i.IService;
import org.snubh.hirc.pghd.api.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Offset;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.api.SearchTotalModeEnum;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.param.DateAndListParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.param.UriAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Controller
public class BundleController implements IResourceProvider {

	@Autowired
	@Qualifier("BundleService")
	private IService service;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Bundle.class;
	}

	@Autowired
	private LogUtil logUtil;

	@Search()
	public Bundle searchBundle(@OptionalParam(name = Observation.SP_RES_ID) TokenAndListParam theId, @OptionalParam(name = "composition") ReferenceAndListParam theComposition,
			@OptionalParam(name = "identifier") TokenAndListParam theIdentifier, @OptionalParam(name = "message") ReferenceAndListParam theMessage, @OptionalParam(name = "timestamp") DateAndListParam theTimestamp,
			@OptionalParam(name = "type") TokenAndListParam theType, @Offset Integer theOffset, @Count Integer theCount, @Sort SortSpec theSort, @OptionalParam(name = "_summary") SummaryEnum summaryEnum,
			@OptionalParam(name = "_profile") UriAndListParam theProfile, @OptionalParam(name = "_total") SearchTotalModeEnum searchTotalEnum, @OptionalParam(name = "_lastUpdated") DateRangeParam theLastUpdated, HttpServletRequest request)
			throws InstantiationException, IllegalAccessException, ParseException {

		Bundle bundle = null;

		try {
			SearchParameterMap searchParam = setSearchParameterMap(theId, theComposition, theIdentifier, theMessage, theTimestamp, theType, theOffset, theCount, theSort, summaryEnum, theProfile, searchTotalEnum, theLastUpdated);
			bundle = service.searchService(request, searchParam);
		} catch (Exception e) {
			logUtil.write(e);
			throw e;
		}
		return bundle;
	}

	@Operation(name = "$readsnapshot", idempotent = true, type = Bundle.class, manualResponse = true)
	public void snapshotOperation(@IdParam IdType theId, final HttpServletResponse theServletResponse) {
		service.readService(theId, theServletResponse);
	}

	private SearchParameterMap setSearchParameterMap(TokenAndListParam theId, ReferenceAndListParam theComposition, TokenAndListParam theIdentifier, ReferenceAndListParam theMessage, DateAndListParam theTimestamp, TokenAndListParam theType,
			Integer theOffset, Integer theCount, SortSpec theSort, SummaryEnum summaryEnum, UriAndListParam theProfile, SearchTotalModeEnum searchTotalEnum, DateRangeParam theLastUpdated) {

		SearchParameterMap searchParam = new SearchParameterMap();

		searchParam.add(Bundle.SP_RES_ID, theId);
		searchParam.add(Bundle.SP_COMPOSITION, theComposition);
		searchParam.add(Bundle.SP_IDENTIFIER, theIdentifier);
		searchParam.add(Bundle.SP_MESSAGE, theMessage);
		searchParam.add(Bundle.SP_TIMESTAMP, theTimestamp);
		searchParam.add(Bundle.SP_TYPE, theType);
		searchParam.add("_profile", theProfile);
		searchParam.setLastUpdated(theLastUpdated);
		searchParam.setSort(theSort);
		searchParam.setSummaryMode(summaryEnum);
		searchParam.setSearchTotalMode(searchTotalEnum);

		return searchParam;
	}
}
