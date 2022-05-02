package org.snubh.hirc.pghd.api.service;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.Resource;
import org.snubh.hirc.pghd.api.config.RestfulServlet;
import org.snubh.hirc.pghd.api.dao.i.IDao;
import org.snubh.hirc.pghd.api.service.i.BaseService;
import org.snubh.hirc.pghd.api.service.i.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;

@Service("QuestionnaireService")
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
public class QuestionnaireService extends BaseService implements IService {

	private final FhirContext ctx;

	private final IParser jsonParser;

	public QuestionnaireService() {
		ctx = RestfulServlet.getFhirContextR4();
		jsonParser = ctx.newJsonParser();
	}

	@Autowired
	@Qualifier("surveyConductDao")
	public IDao dao;

	@Override
	public Bundle searchService(HttpServletRequest theRequest, SearchParameterMap searchParamMap) throws InstantiationException, IllegalAccessException, ParseException {
		setSearchParameterMap(theRequest, searchParamMap);
		setTotalCount(dao);
		Bundle searchSetBundle = generateSearchSetBundle(dao);

		return searchSetBundle;
	}

	@Override
	public Resource readService(IdType idType) throws InstantiationException, IllegalAccessException, ParseException, ConfigurationException, DataFormatException, IOException {
		if (idType.getIdPart().equals("snubh-survey-questions")) {
			String path = String.format("resource/%s.json", idType.getIdPart());
			ClassPathResource classPathResource = new ClassPathResource(path);
			Questionnaire resource = (Questionnaire) jsonParser.parseResource(classPathResource.getInputStream());
			return resource;
		}

		return dao.findById(idType.getIdPart());
	}

}
