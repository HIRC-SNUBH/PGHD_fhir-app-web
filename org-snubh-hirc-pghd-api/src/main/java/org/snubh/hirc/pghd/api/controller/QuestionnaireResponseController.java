package org.snubh.hirc.pghd.api.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.OperationOutcome.IssueSeverity;
import org.hl7.fhir.r4.model.OperationOutcome.IssueType;
import org.hl7.fhir.r4.model.OperationOutcome.OperationOutcomeIssueComponent;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
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
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;

@Controller
public class QuestionnaireResponseController implements IResourceProvider {

	@Autowired
	@Qualifier("QuestionnaireResponseService")
	private IService service;

	@Autowired
	private LogUtil logUtil;

	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return QuestionnaireResponse.class;
	}

	@Search()
	public Bundle search(@OptionalParam(name = QuestionnaireResponse.SP_RES_ID) TokenAndListParam theId, @OptionalParam(name = QuestionnaireResponse.SP_QUESTIONNAIRE + "." + Questionnaire.SP_STATUS) TokenAndListParam theStatus,
			@OptionalParam(name = QuestionnaireResponse.SP_QUESTIONNAIRE + "." + Questionnaire.SP_CODE) TokenAndListParam theCode, @OptionalParam(name = QuestionnaireResponse.SP_AUTHORED) DateAndListParam theAuthored,
			@OptionalParam(name = QuestionnaireResponse.SP_ENCOUNTER) ReferenceAndListParam theEncounter, @OptionalParam(name = QuestionnaireResponse.SP_PATIENT) ReferenceAndListParam thePatient,
			@OptionalParam(name = QuestionnaireResponse.SP_SUBJECT) ReferenceAndListParam theSubject, @OptionalParam(name = QuestionnaireResponse.SP_QUESTIONNAIRE + "." + Questionnaire.SP_TITLE) StringAndListParam theTitle,
			@Offset Integer theOffset, @Count Integer theCount, @Sort SortSpec theSort, @OptionalParam(name = "_summary") SummaryEnum summaryEnum, @OptionalParam(name = "_total") SearchTotalModeEnum searchTotalEnum,
			@OptionalParam(name = "_lastUpdated") DateRangeParam theLastUpdated, @OptionalParam(name = "_contained") StringAndListParam theContained, HttpServletRequest request)
			throws InstantiationException, IllegalAccessException, ParseException {

		Bundle bundle = null;
		try {
			SearchParameterMap searchParam = setSearchParameterMap(theId, theStatus, theCode, theAuthored, theEncounter, thePatient, theSubject, theTitle, theOffset, theCount, theSort, summaryEnum, searchTotalEnum, theLastUpdated,
					theContained);
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

	private SearchParameterMap setSearchParameterMap(TokenAndListParam theId, TokenAndListParam theStatus, TokenAndListParam theCode, DateAndListParam theAuthored, ReferenceAndListParam theEncounter, ReferenceAndListParam thePatient,
			ReferenceAndListParam theSubject, StringAndListParam theTitle, Integer theOffset, Integer theCount, SortSpec theSort, SummaryEnum summaryEnum, SearchTotalModeEnum searchTotalEnum, DateRangeParam theLastUpdated,
			StringAndListParam theContained) {
		SearchParameterMap searchParam = new SearchParameterMap();
		searchParam.add(Resource.SP_RES_ID, theId);
		searchParam.add(QuestionnaireResponse.SP_STATUS, theStatus);
		searchParam.add(QuestionnaireResponse.SP_QUESTIONNAIRE + "." + Questionnaire.SP_CODE, theCode);
		searchParam.add(QuestionnaireResponse.SP_AUTHORED, theAuthored);
		searchParam.add(QuestionnaireResponse.SP_ENCOUNTER, theEncounter);
		searchParam.add(QuestionnaireResponse.SP_PATIENT, thePatient);
		searchParam.add(QuestionnaireResponse.SP_SUBJECT, theSubject);
		searchParam.add(QuestionnaireResponse.SP_QUESTIONNAIRE + "." + Questionnaire.SP_TITLE, theTitle);
		searchParam.add("_contained", theContained);

		searchParam.setLastUpdated(theLastUpdated);

		searchParam.setCount(theCount);// Integer
		searchParam.setOffset(theOffset);// Integer
		searchParam.setSort(theSort);
		searchParam.setSummaryMode(summaryEnum);
		searchParam.setSearchTotalMode(searchTotalEnum);

		return searchParam;
	}

	@Operation(name = "$snapshot", type = QuestionnaireResponse.class)
	public OperationOutcome snapshotOperation(@OperationParam(name = QuestionnaireResponse.SP_RES_ID) TokenAndListParam theId,
			@OperationParam(name = QuestionnaireResponse.SP_QUESTIONNAIRE + "." + Questionnaire.SP_STATUS) TokenAndListParam theStatus,
			@OperationParam(name = QuestionnaireResponse.SP_QUESTIONNAIRE + "." + Questionnaire.SP_CODE) TokenAndListParam theCode, @OperationParam(name = QuestionnaireResponse.SP_AUTHORED) DateAndListParam theAuthored,
			@OperationParam(name = QuestionnaireResponse.SP_ENCOUNTER) ReferenceAndListParam theEncounter, @OperationParam(name = QuestionnaireResponse.SP_PATIENT) ReferenceAndListParam thePatient,
			@OperationParam(name = QuestionnaireResponse.SP_SUBJECT) ReferenceAndListParam theSubject, @OperationParam(name = QuestionnaireResponse.SP_QUESTIONNAIRE + "." + Questionnaire.SP_TITLE) StringAndListParam theTitle,
			@Offset Integer theOffset, @Count Integer theCount, @Sort SortSpec theSort, @OperationParam(name = "_summary") SummaryEnum summaryEnum, @OperationParam(name = "_total") SearchTotalModeEnum searchTotalEnum,
			@OperationParam(name = "_lastUpdated") DateRangeParam theLastUpdated, @OperationParam(name = "_contained") StringAndListParam theContained, HttpServletRequest request)
			throws InstantiationException, IllegalAccessException, ParseException, IOException {

		OperationOutcome operationOutcome = null;

		try {
			String id = UUID.randomUUID().toString();

			SearchParameterMap searchParam = setSearchParameterMap(theId, theStatus, theCode, theAuthored, theEncounter, thePatient, theSubject, theTitle, theOffset, theCount, theSort, summaryEnum, searchTotalEnum, theLastUpdated,
					theContained);
			service.snapshot(id, request, searchParam);
			operationOutcome = getOperationOutcome(id);
		} catch (Exception e) {
			logUtil.write(e);
			throw e;
		}
		return operationOutcome;
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
