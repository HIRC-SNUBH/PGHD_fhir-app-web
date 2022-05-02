package org.snubh.hirc.pghd.api.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.parser.IParser;

@Service("QuestionnaireResponseService")
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
public class QuestionnaireResponseService extends BaseService implements IService {

	private final FhirContext ctx;

	private final IParser jsonParser;

	public QuestionnaireResponseService() {
		ctx = RestfulServlet.getFhirContextR4();
		jsonParser = ctx.newJsonParser();
	}

	@Autowired
	@Qualifier("surveyConductResponseDao")
	public IDao dao;

	@Override
	public Bundle searchService(HttpServletRequest theRequest, SearchParameterMap searchParamMap) throws InstantiationException, IllegalAccessException, ParseException {
		setSearchParameterMap(theRequest, searchParamMap);
		setTotalCount(dao);
		Bundle searchSetBundle = generateSearchSetBundle(dao);

		return searchSetBundle;
	}

	@Override
	public Resource readService(IdType idType) throws InstantiationException, IllegalAccessException, ParseException {
		return dao.findById(idType.getIdPart());
	}

	@Async
	@Override
	public void snapshot(String id, HttpServletRequest theRequest, SearchParameterMap searchParamMap) throws InstantiationException, IllegalAccessException, ParseException, IOException {
		setSearchParameterMap(theRequest, searchParamMap);
		setDefaulCount(Integer.MAX_VALUE);
		setTotalCount(dao);
		Bundle searchSetBundle = generateSearchSetBundle(dao);
		saveFile(searchSetBundle, id);

	}

	private void saveFile(Bundle searchSetBundle, String id) throws IOException {
		String realPath = String.format("%s/%s.json", new ClassPathResource(getSnapshotPath()).getFile(), id);
		File file = new File(realPath);
		if (!file.exists())
			file.createNewFile();
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
		bufferedWriter.write(jsonParser.encodeResourceToString(searchSetBundle));
		bufferedWriter.newLine();
		bufferedWriter.flush();
		bufferedWriter.close();
	}
}
