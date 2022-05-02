package org.snubh.hirc.pghd.api.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.DeviceUseStatement;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent;
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
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
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
import ca.uhn.fhir.rest.param.ReferenceOrListParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.param.UriAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Controller
public class ObservationController implements IResourceProvider {

	@Autowired
	@Qualifier("ObservationService")
	private IService service;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Observation.class;
	}

	@Autowired
	private LogUtil logUtil;

	@Search()
	public Bundle searchObservation(@OptionalParam(name = Observation.SP_RES_ID) TokenAndListParam theId, @OptionalParam(name = Observation.SP_STATUS) TokenAndListParam theStatus,
			@OptionalParam(name = Observation.SP_CODE) TokenAndListParam theCode, @OptionalParam(name = Observation.SP_COMPONENT_CODE) TokenAndListParam theComponentCode,
			@OptionalParam(name = Observation.SP_PATIENT) ReferenceAndListParam thePatient, @OptionalParam(name = Observation.SP_SUBJECT) ReferenceAndListParam theSubject, @OptionalParam(name = Observation.SP_DATE) DateAndListParam theDate,
			@OptionalParam(name = Observation.SP_CATEGORY) TokenAndListParam theCategory

			, @OptionalParam(name = Observation.SP_SUBJECT + "." + Patient.SP_GENDER) TokenAndListParam theSubjectGender, @OptionalParam(name = Observation.SP_PATIENT + "." + Patient.SP_GENDER) TokenAndListParam thePatientGender,
			@OptionalParam(name = DeviceUseStatement.SP_DEVICE + "." + Device.SP_TYPE) TokenAndListParam theDeviceType, @OptionalParam(name = "deviceUseStartTiming") DateAndListParam theStartTiming,
			@OptionalParam(name = "deviceUseEndTiming") DateAndListParam theEndTiming, @OptionalParam(name = "_contained") StringAndListParam theContained

			, @Offset Integer theOffset, @Count Integer theCount, @Sort SortSpec theSort, @OptionalParam(name = "_summary") SummaryEnum summaryEnum, @OptionalParam(name = "_profile") UriAndListParam theProfile,
			@OptionalParam(name = "_total") SearchTotalModeEnum searchTotalEnum, @OptionalParam(name = "_lastUpdated") DateRangeParam theLastUpdated, HttpServletRequest request)
			throws InstantiationException, IllegalAccessException, ParseException {

		Bundle bundle = null;

		try {
			SearchParameterMap searchParam = setSearchParameterMap(theId, theStatus, theCode, theComponentCode, thePatient, theSubject, theDate, theCategory, theSubjectGender, thePatientGender, theDeviceType, theStartTiming, theEndTiming,
					theContained, theOffset, theCount, theSort, summaryEnum, theProfile, searchTotalEnum, theLastUpdated);
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

	@Operation(name = "$snapshot", type = Observation.class)
	public OperationOutcome snapshotOperation(@OperationParam(name = Observation.SP_RES_ID) TokenAndListParam theId, @OperationParam(name = Observation.SP_STATUS) TokenAndListParam theStatus,
			@OperationParam(name = Observation.SP_CODE) TokenAndListParam theCode, @OperationParam(name = Observation.SP_COMPONENT_CODE) TokenAndListParam theComponentCode,
			@OperationParam(name = Observation.SP_PATIENT) ReferenceAndListParam thePatient, @OperationParam(name = Observation.SP_SUBJECT) ReferenceAndListParam theSubject,
			@OperationParam(name = Observation.SP_DATE) DateAndListParam theDate, @OperationParam(name = Observation.SP_CATEGORY) TokenAndListParam theCategory

			, @OperationParam(name = Observation.SP_SUBJECT + "." + Patient.SP_GENDER) TokenAndListParam theSubjectGender, @OperationParam(name = Observation.SP_PATIENT + "." + Patient.SP_GENDER) TokenAndListParam thePatientGender,
			@OperationParam(name = DeviceUseStatement.SP_DEVICE + "." + Device.SP_TYPE) TokenAndListParam theDeviceType, @OperationParam(name = "deviceUseStartTiming") DateAndListParam theStartTiming,
			@OperationParam(name = "deviceUseEndTiming") DateAndListParam theEndTiming, @OperationParam(name = "_contained") StringAndListParam theContained

			, @Offset Integer theOffset, @Count Integer theCount, @Sort SortSpec theSort, @OperationParam(name = "_summary") SummaryEnum summaryEnum, @OperationParam(name = "_profile") UriAndListParam theProfile,
			@OperationParam(name = "_total") SearchTotalModeEnum searchTotalEnum, @OperationParam(name = "_lastUpdated") DateRangeParam theLastUpdated, HttpServletRequest request)
			throws InstantiationException, IllegalAccessException, ParseException, IOException {

		OperationOutcome operationOutcome = null;

		try {
			String id = UUID.randomUUID().toString();

			SearchParameterMap searchParam = setSearchParameterMap(theId, theStatus, theCode, theComponentCode, thePatient, theSubject, theDate, theCategory, theSubjectGender, thePatientGender, theDeviceType, theStartTiming, theEndTiming,
					theContained, theOffset, theCount, theSort, summaryEnum, theProfile, searchTotalEnum, theLastUpdated);
			service.snapshot(id, request, searchParam);
			operationOutcome = getOperationOutcome(id);
		} catch (Exception e) {
			logUtil.write(e);
			throw e;
		}
		return operationOutcome;
	}

	private SearchParameterMap setSearchParameterMap(TokenAndListParam theId, TokenAndListParam theStatus, TokenAndListParam theCode, TokenAndListParam theComponentCode, ReferenceAndListParam thePatient, ReferenceAndListParam theSubject,
			DateAndListParam theDate, TokenAndListParam theCategory, TokenAndListParam theSubjectGender, TokenAndListParam thePatientGender, TokenAndListParam theDeviceType, DateAndListParam theStartTiming, DateAndListParam theEndTiming,
			StringAndListParam theContained, Integer theOffset, Integer theCount, SortSpec theSort, SummaryEnum summaryEnum, UriAndListParam theProfile, SearchTotalModeEnum searchTotalEnum, DateRangeParam theLastUpdated) {

		SearchParameterMap searchParam = new SearchParameterMap();

		searchParam.add(Observation.SP_RES_ID, theId);
		searchParam.add(Observation.SP_STATUS, theStatus);
		searchParam.add(Observation.SP_DATE, theDate);
		if (!isChainParameter(thePatient))
			searchParam.add(Observation.SP_PATIENT, thePatient);
		if (!isChainParameter(theSubject))
			searchParam.add(Observation.SP_SUBJECT, theSubject);
		searchParam.add(Observation.SP_CATEGORY, theCategory);
		searchParam.add(Observation.SP_CODE, theCode);
		searchParam.add(Observation.SP_COMPONENT_CODE, theComponentCode);
		searchParam.add("_profile", theProfile);
		searchParam.add(Observation.SP_SUBJECT + "." + Patient.SP_GENDER, theSubjectGender);
		searchParam.add(Observation.SP_PATIENT + "." + Patient.SP_GENDER, thePatientGender);
		searchParam.add(DeviceUseStatement.SP_DEVICE + "." + Device.SP_TYPE, theDeviceType);
		searchParam.add("deviceUseStartTiming", theStartTiming);
		searchParam.add("deviceUseEndTiming", theEndTiming);
		searchParam.add("_contained", theContained);

		searchParam.setLastUpdated(theLastUpdated);

		searchParam.setCount(theCount);// Integer
		searchParam.setOffset(theOffset);// Integer
		searchParam.setSort(theSort);
		searchParam.setSummaryMode(summaryEnum);
		searchParam.setSearchTotalMode(searchTotalEnum);

		return searchParam;

	}

	private boolean isChainParameter(ReferenceAndListParam thePatient) {
		if (thePatient != null) {
			for (ReferenceOrListParam rOrParam : thePatient.getValuesAsQueryTokens()) {
				for (ReferenceParam rParam : rOrParam.getValuesAsQueryTokens()) {
					if (rParam.getChain() != null) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private OperationOutcome getOperationOutcome(String id) {
		OperationOutcome operationOutcome = new OperationOutcome();
		OperationOutcomeIssueComponent issue = new OperationOutcomeIssueComponent();
		issue.setSeverity(IssueSeverity.INFORMATION);
		issue.setCode(IssueType.INFORMATIONAL);
		issue.setDiagnostics(id);

		operationOutcome.setIssue(Arrays.asList(issue));
		return operationOutcome;
	}
}
