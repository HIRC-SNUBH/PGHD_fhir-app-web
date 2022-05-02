package org.snubh.hirc.pghd.api.service;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationDefinition;
import org.hl7.fhir.r4.model.Resource;
import org.snubh.hirc.pghd.api.config.RestfulServlet;
import org.snubh.hirc.pghd.api.service.i.BaseService;
import org.snubh.hirc.pghd.api.service.i.IService;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;

@Service("OperationDefinitionService")
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
public class OperationDefinitionService extends BaseService implements IService {

	private final FhirContext ctx;

	private final IParser parser;

	public OperationDefinitionService() {
		ctx = RestfulServlet.getFhirContextR4();
		parser = ctx.newJsonParser();
	}

	@Override
	public Bundle searchService(HttpServletRequest theRequest, SearchParameterMap searchParamMap) throws InstantiationException, IllegalAccessException {
		return null;
	}

	@Override
	public Resource readService(IdType idType) throws InstantiationException, IllegalAccessException, ParseException, ConfigurationException, DataFormatException, IOException {

		OperationDefinition resource;
		String path = String.format("resource/operationdefinition/%s.json", idType.getIdPart());
		ClassPathResource classPathResource = new ClassPathResource(path);
		try {
			resource = (OperationDefinition) parser.parseResource(classPathResource.getInputStream());
		} catch (java.io.FileNotFoundException e) {
			resource = null;
		}
		return resource;

	}
}
