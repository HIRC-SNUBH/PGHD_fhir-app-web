package org.snubh.hirc.pghd.api.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Resource;
import org.snubh.hirc.pghd.api.config.RestfulServlet;
import org.snubh.hirc.pghd.api.dao.i.IDao;
import org.snubh.hirc.pghd.api.service.i.BaseService;
import org.snubh.hirc.pghd.api.service.i.IService;
import org.snubh.hirc.pghd.api.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.param.UriParam;

@Service("ObservationService")
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
public class ObservationService extends BaseService implements IService {

	private final FhirContext ctx;

	private final IParser jsonParser;

	public ObservationService() {
		ctx = RestfulServlet.getFhirContextR4();
		jsonParser = ctx.newJsonParser();
	}

	@Autowired
	private LogUtil logUtil;

	@Autowired
	@Qualifier("observationDao")
	public IDao dao;

	@Override
	public Bundle searchService(HttpServletRequest theRequest, SearchParameterMap searchParamMap) throws InstantiationException, IllegalAccessException, ParseException {

		boolean compositionFlag = setProfileParamAndList(searchParamMap);
		setSearchParameterMap(theRequest, searchParamMap);
		setTotalCount(dao, compositionFlag);
		Bundle searchSetBundle = generateSearchSetBundle(dao, compositionFlag);
		return searchSetBundle;
	}

	@Override
	public Resource readService(IdType theId) throws InstantiationException, IllegalAccessException, ParseException {
		Resource resource = dao.findById(theId.getIdPart());
		return resource;
	}

	private boolean setProfileParamAndList(SearchParameterMap searchParamMap) {
		boolean compositionFlag = false;
		if (searchParamMap.get("_profile") != null) {
			List<List<IQueryParameterType>> profileParamAndList = searchParamMap.get("_profile");
			for (List<IQueryParameterType> profileParamOrList : profileParamAndList) {
				for (IQueryParameterType profileParam : profileParamOrList) {
					UriParam uriParam = (UriParam) profileParam;
					String idValue = uriParam.getValue();

					if (idValue.contains("composition")) {
						compositionFlag = true;
					} else if (idValue.contains("common")) {
						compositionFlag = false;
					}

				}
			}
			searchParamMap.remove("_profile");
		}

		return compositionFlag;
	}

	@Async
	@Override
	public void snapshot(String id, HttpServletRequest theRequest, SearchParameterMap searchParamMap) throws InstantiationException, IllegalAccessException, ParseException, IOException {

		try {
			boolean compositionFlag = setProfileParamAndList(searchParamMap);
			setSearchParameterMap(theRequest, searchParamMap);
			setDefaulCount(Integer.MAX_VALUE);
			setTotalCount(dao, compositionFlag);
			logUtil.writeDebug(String.format("snapshot 시작 : %s", id));
			Bundle searchSetBundle = generateSearchSetBundle(dao, compositionFlag);
			logUtil.writeDebug(String.format("snapshot 데이터 조회 완료  : %s", id));
			saveFile(searchSetBundle, id);
		} catch (Exception e) {
			logUtil.writeDebug(String.format("snapshot 실패 : %s", id));
			logUtil.write(e);
			throw e;

		}
		logUtil.writeDebug(String.format("snapshot 파일 저장 완료: %s", id));
	}

	private void saveFile(Bundle searchSetBundle, String id) throws IOException {

		String realPath = String.format("%s/%s.json", new ClassPathResource(getSnapshotPath()).getFile(), id);
		File file = new File(realPath);

		if (!file.exists())
			file.createNewFile();

		FileWriter fw = new FileWriter(file);

		try {
			jsonParser.encodeResourceToWriter(searchSetBundle, fw);
		} finally {
			fw.close();
		}
	}

}
