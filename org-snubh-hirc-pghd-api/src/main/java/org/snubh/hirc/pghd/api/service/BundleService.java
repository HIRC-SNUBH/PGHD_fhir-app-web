package org.snubh.hirc.pghd.api.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.snubh.hirc.pghd.api.service.i.BaseService;
import org.snubh.hirc.pghd.api.service.i.IService;
import org.snubh.hirc.pghd.api.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.parser.DataFormatException;

@Service("BundleService")
@Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
public class BundleService extends BaseService implements IService {

	public BundleService() {
	}

	@Autowired
	private LogUtil logUtil;

	@Override
	public Bundle searchService(HttpServletRequest theRequest, SearchParameterMap searchParamMap) throws InstantiationException, IllegalAccessException, ParseException {
		setSearchParameterMap(theRequest, searchParamMap);
		Bundle searchSetBundle = generateSearchSetBundleFromFile(getSnapshotPath());
		return searchSetBundle;
	}

	@Override
	public void readService(IdType idType, HttpServletResponse theServletResponse) {

		OutputStream os = null;

		try {

			String realPath = String.format("%s/%s.json", new ClassPathResource(getSnapshotPath()).getFile(), idType.getIdPart());
			Path path = Paths.get(realPath);

			byte[] bytes = Files.readAllBytes(path);

			theServletResponse.setContentType("application/fhir+json");
			theServletResponse.setContentLength(bytes.length);

			os = theServletResponse.getOutputStream();
			os.write(bytes, 0, bytes.length);

			theServletResponse.setStatus(HttpServletResponse.SC_OK);
			bytes = null;

		} catch (IOException e1) {
			theServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			logUtil.write(e);
			theServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			throw e;
		} finally {
			try {
				os.close();
			} catch (IOException e) {
			}
		}

	}

	@Override
	public <T> T readService(IdType theId) throws InstantiationException, IllegalAccessException, ParseException, ConfigurationException, DataFormatException, IOException {
		return null;
	}

}
