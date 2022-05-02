package org.snubh.hirc.pghd.api.controller;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
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
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Controller
public class PatientController implements IResourceProvider {

	@Autowired
	@Qualifier("PatientService")
	private IService service;

	@Autowired
	private LogUtil logUtil;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Patient.class;
	}

	@Search()
	public Bundle search(@OptionalParam(name = Resource.SP_RES_ID) TokenAndListParam theId, @OptionalParam(name = Patient.SP_IDENTIFIER) TokenAndListParam theIdentifier,
			@OptionalParam(name = Patient.SP_GENERAL_PRACTITIONER) ReferenceAndListParam theGeneralPractitioner, @OptionalParam(name = Patient.SP_ORGANIZATION) ReferenceAndListParam theOrganization,
			@OptionalParam(name = Patient.SP_GENDER) TokenAndListParam theGender, @OptionalParam(name = Patient.SP_BIRTHDATE) DateAndListParam theBirthDate, @OptionalParam(name = Patient.SP_DEATH_DATE) DateAndListParam theDeathDate,
			@OptionalParam(name = Patient.SP_ADDRESS_CITY) StringAndListParam theCity, @OptionalParam(name = Patient.SP_ADDRESS) StringAndListParam theAddress, @OptionalParam(name = Patient.SP_ADDRESS_STATE) StringAndListParam theState,
			@OptionalParam(name = Patient.SP_ADDRESS_POSTALCODE) StringAndListParam thePostalCode, @OptionalParam(name = Patient.SP_ADDRESS_COUNTRY) StringAndListParam theCountry, @Offset Integer theOffset, @Count Integer theCount,
			@Sort SortSpec theSort, @OptionalParam(name = "_summary") SummaryEnum summaryEnum, @OptionalParam(name = "_total") SearchTotalModeEnum searchTotalEnum, @OptionalParam(name = "_lastUpdated") DateRangeParam theLastUpdated,
			HttpServletRequest request) throws InstantiationException, IllegalAccessException, ParseException {

		Bundle bundle = null;

		try {
			SearchParameterMap searchParam = setSearchParameterMap(theId, theIdentifier, theGeneralPractitioner, theOrganization, theGender, theBirthDate, theDeathDate, theCity, theAddress, theState, thePostalCode, theCountry, theOffset,
					theCount, theSort, summaryEnum, searchTotalEnum, theLastUpdated);
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

	private SearchParameterMap setSearchParameterMap(TokenAndListParam theId, TokenAndListParam theIdentifier, ReferenceAndListParam theGeneralPractitioner, ReferenceAndListParam theOrganization, TokenAndListParam theGender,
			DateAndListParam theBirthDate, DateAndListParam theDeathDate, StringAndListParam theCity, StringAndListParam theAddress, StringAndListParam theState, StringAndListParam thePostalCode, StringAndListParam theCountry,
			Integer theOffset, Integer theCount, SortSpec theSort, SummaryEnum summaryEnum, SearchTotalModeEnum searchTotalEnum, DateRangeParam theLastUpdated) {

		SearchParameterMap searchParam = new SearchParameterMap();
		searchParam.add(Resource.SP_RES_ID, theId);
		searchParam.add(Patient.SP_IDENTIFIER, theIdentifier);
		searchParam.add(Patient.SP_GENERAL_PRACTITIONER, theGeneralPractitioner);
		searchParam.add(Patient.SP_ORGANIZATION, theOrganization);
		searchParam.add(Patient.SP_GENDER, theGender);
		searchParam.add(Patient.SP_BIRTHDATE, theBirthDate);
		searchParam.add(Patient.SP_DEATH_DATE, theDeathDate);
		searchParam.add(Patient.SP_ADDRESS_CITY, theCity);
		searchParam.add(Patient.SP_ADDRESS, theAddress);
		searchParam.add(Patient.SP_ADDRESS_STATE, theState);
		searchParam.add(Patient.SP_ADDRESS_POSTALCODE, thePostalCode);
		searchParam.add(Patient.SP_ADDRESS_COUNTRY, theCountry);

		searchParam.setLastUpdated(theLastUpdated);

		searchParam.setCount(theCount);// Integer
		searchParam.setOffset(theOffset);// Integer

		searchParam.setSort(theSort);
		searchParam.setSummaryMode(summaryEnum);
		searchParam.setSearchTotalMode(searchTotalEnum);

		return searchParam;

	}
}
