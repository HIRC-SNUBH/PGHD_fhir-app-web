package org.snubh.hirc.pghd.api.controller;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Organization;
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
import ca.uhn.fhir.rest.param.UriAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Controller
public class OrganizationController implements IResourceProvider {

	@Autowired
	@Qualifier("OrganizationService")
	private IService service;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Organization.class;
	}

	@Autowired
	private LogUtil logUtil;

	@Search()
	public Bundle searchOrganization(@OptionalParam(name = Organization.SP_RES_ID) TokenAndListParam theId, @OptionalParam(name = Organization.SP_NAME) StringAndListParam theName,
			@OptionalParam(name = Organization.SP_TYPE) TokenAndListParam theType, @OptionalParam(name = Organization.SP_ADDRESS) StringAndListParam theAddress,
			@OptionalParam(name = Organization.SP_ADDRESS_CITY) StringAndListParam theAddressCity, @OptionalParam(name = Organization.SP_ADDRESS_STATE) StringAndListParam theAddressState,
			@OptionalParam(name = Organization.SP_ADDRESS_POSTALCODE) StringAndListParam theAddressPostalCode, @Offset Integer theOffset, @Count Integer theCount, @Sort SortSpec theSort,
			@OptionalParam(name = "_summary") SummaryEnum summaryEnum, @OptionalParam(name = "_profile") UriAndListParam theProfile, @OptionalParam(name = "_total") SearchTotalModeEnum searchTotalEnum,
			@OptionalParam(name = "_lastUpdated") DateRangeParam theLastUpdated, HttpServletRequest request) throws InstantiationException, IllegalAccessException, ParseException {

		Bundle bundle = null;

		try {
			SearchParameterMap searchParam = setSearchParameterMap(theId, theName, theType, theAddress, theAddressCity, theAddressState, theAddressPostalCode, theOffset, theCount, theSort, summaryEnum, theProfile, searchTotalEnum,
					theLastUpdated);
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

	private SearchParameterMap setSearchParameterMap(TokenAndListParam theId, StringAndListParam theName, TokenAndListParam theType, StringAndListParam theAddress, StringAndListParam theAddressCity, StringAndListParam theAddressState,
			StringAndListParam theAddressPostalCode, Integer theOffset, Integer theCount, SortSpec theSort, SummaryEnum summaryEnum, UriAndListParam theProfile, SearchTotalModeEnum searchTotalEnum, DateRangeParam theLastUpdated) {

		SearchParameterMap searchParam = new SearchParameterMap();

		searchParam.add(Organization.SP_RES_ID, theId);
		searchParam.add(Organization.SP_NAME, theName);
		searchParam.add(Organization.SP_TYPE, theType);
		searchParam.add(Organization.SP_ADDRESS, theAddress);
		searchParam.add(Organization.SP_ADDRESS_CITY, theAddressCity);
		searchParam.add(Organization.SP_ADDRESS_STATE, theAddressState);
		searchParam.add(Organization.SP_ADDRESS_POSTALCODE, theAddressPostalCode);
		searchParam.add("_profile", theProfile);

		searchParam.setLastUpdated(theLastUpdated);

		searchParam.setCount(theCount);// Integer
		searchParam.setOffset(theOffset);// Integer
		searchParam.setSort(theSort);
		searchParam.setSummaryMode(summaryEnum);
		searchParam.setSearchTotalMode(searchTotalEnum);

		return searchParam;

	}
}
