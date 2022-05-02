package org.snubh.hirc.pghd.api.service.i;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.parser.DataFormatException;

public interface IService {

	Bundle searchService(HttpServletRequest theRequest, SearchParameterMap searchParamMap) throws InstantiationException, IllegalAccessException, ParseException;

	<T> T readService(IdType theId) throws InstantiationException, IllegalAccessException, ParseException, ConfigurationException, DataFormatException, IOException;

	default void snapshot(String theId, HttpServletRequest theRequest, SearchParameterMap searchParamMap) throws InstantiationException, IllegalAccessException, ParseException, IOException {

	}

	default void readService(IdType idType, HttpServletResponse theServletResponse) {

	}
}
