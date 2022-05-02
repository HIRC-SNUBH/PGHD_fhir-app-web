package org.snubh.hirc.pghd.api.service.i;

import java.io.File;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleLinkComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.UnsignedIntType;
import org.snubh.hirc.pghd.api.dao.i.IDao;
import org.springframework.beans.factory.annotation.Value;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.api.SearchTotalModeEnum;

public class BaseService {

	private SearchParameterMap searchParamMap;

	@Value("${server.defaulcount}")
	private int defaulCount;

	@Value("${server.defaultoffset}")
	private int defaultOffset;

	private final String snapshotPath = "resource/snapshot";

	private String baseUrl = null;
	private String requestUrl = null;
	private String queryString = null;

	private Long total;

	public void setDefaulCount(int defaulCount) {
		this.defaulCount = defaulCount;
	}

	public String getSnapshotPath() {
		return this.snapshotPath;
	}

	public void setSearchParameterMap(HttpServletRequest theRequest, SearchParameterMap searchParamMap) {

		if (theRequest != null) {
			this.requestUrl = theRequest.getRequestURL().toString();
			this.queryString = theRequest.getQueryString() != null ? theRequest.getQueryString() : "";
			this.baseUrl = String.format("%s/", requestUrl.substring(0, requestUrl.substring(0, requestUrl.lastIndexOf("/")).lastIndexOf("/")));
		} else {
			this.requestUrl = "";
		}

		this.searchParamMap = searchParamMap;
	}

	public <T> void setTotalCount(IDao dao) throws InstantiationException, IllegalAccessException, ParseException {
		setTotalCount(dao, false);
	}

	public <T> void setTotalCount(IDao dao, boolean isComposition) throws InstantiationException, IllegalAccessException, ParseException {
		if (this.searchParamMap.getSearchTotalMode() == null || !this.searchParamMap.getSearchTotalMode().equals(SearchTotalModeEnum.NONE)) {
			if (isComposition) {
				this.total = dao.countComposition(this.searchParamMap);
			} else {
				this.total = dao.count(this.searchParamMap);
			}
		} else {
			this.total = 0L;
		}
	}

	public <T> Bundle generateSearchSetBundle(IDao dao) throws InstantiationException, IllegalAccessException {
		return generateSearchSetBundle(dao, false);
	}

	public <T> Bundle generateSearchSetBundleFromFile(String path) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource(path);
		String folderPath = url.getPath();
		File[] fileArr = new File(folderPath).listFiles();

		Bundle searchSetBundle = new Bundle();
		searchSetBundle.setType(BundleType.SEARCHSET);
		for (int i = 0; i < fileArr.length; i++) {
			if (fileArr[i].getName().contains(".json")) {
				BundleEntryComponent addEntry = new BundleEntryComponent();
				addEntry.setResource(setSearchSetBundle(fileArr[i].getName()));
				searchSetBundle.addEntry(addEntry);
			}
		}
		searchSetBundle.setTotal(fileArr.length - 1);
		return searchSetBundle;
	}

	private Bundle setSearchSetBundle(String id) {
		Bundle bundle = new Bundle();
		bundle.setType(BundleType.SEARCHSET);
		bundle.setTimestamp(new Date());
		bundle.setId(id.replace(".json", ""));
		return bundle;
	}

	public <T> Bundle generateSearchSetBundle(IDao dao, boolean isComposition) throws InstantiationException, IllegalAccessException {

		SearchTotalModeEnum searchTotalModeEnum = this.searchParamMap.getSearchTotalMode();

		int count = defaulCount;
		int offset = defaultOffset;

		if (this.searchParamMap.getCount() != null) {
			count = this.searchParamMap.getCount().intValue();
		}

		if (this.searchParamMap.getOffset() != null) {
			offset = this.searchParamMap.getOffset().intValue();
		}

		List<DomainResource> list = null;

		if (count > 0) {
			if (isComposition) {
				list = (List<DomainResource>) dao.findBySearchParameterComposition(this.searchParamMap, count, offset);
			} else {
				list = (List<DomainResource>) dao.findBySearchParameter(this.searchParamMap, count, offset);
			}
		} else {
			list = Arrays.asList();
		}

		Bundle bundle = new Bundle();
		UnsignedIntType totalValue = new UnsignedIntType();
		totalValue.setValueAsString(this.total.toString());

		bundle.setTotalElement(totalValue);
		bundle.setType(BundleType.SEARCHSET);

		Meta metaValue = new Meta();
		metaValue.setLastUpdated(new Date());
		bundle.setMeta(metaValue);

		bundle.setLink(generatePagingLink(count, offset, this.requestUrl, this.queryString, this.total, searchTotalModeEnum));
		// BundleEntryComponent entry 입력하고 fullUrl 정보 삽입
		if (list != null) {
			for (DomainResource resource : list) {
				BundleEntryComponent entryComponent = new BundleEntryComponent();
				entryComponent.setFullUrl(String.format("%s%s/%s", this.baseUrl, resource.getResourceType(), resource.getId()));
				entryComponent.setResource(resource);
				bundle.addEntry(entryComponent);
			}
		}

		return bundle;
	}

	public Long getTotal() {
		return this.total;
	}

	private String createPageLinkString(String queryString, int count, int offset) {

		List<String> paramList = new ArrayList<String>();
		boolean offsetFlag = false;
		boolean countFlag = false;

		if (queryString != null && queryString.length() > 0) {
			String[] paramVal = queryString.split("&");

			for (String param : paramVal) {
				if (param.contains("_offset")) {
					String[] offsetSplit = param.split("=");
					param = String.format("%s=%d", offsetSplit[0], (offset + count));
					offsetFlag = true;
				}
				if (param.contains("_count")) {
					String[] countSplit = param.split("=");
					param = String.format("%s=%d", countSplit[0], count);
					countFlag = true;
				}
				paramList.add(param);
			}
		}

		if (!offsetFlag) {
			String param = String.format("_offset=%d", (offset + count));
			paramList.add(param);
		}
		if (!countFlag) {
			String param = String.format("_count=%d", count);
			paramList.add(param);
		}

		String newQueryString = "";
		for (String param : paramList) {
			newQueryString += (param + "&");
		}
		newQueryString = newQueryString.substring(0, newQueryString.length() - 1);
		return newQueryString;
	}

	private BundleLinkComponent generateNextComponentLink(String requestUrl, String queryString, int count, int offset, Long resultSize) {

		BundleLinkComponent pageLinkNextComponent = new BundleLinkComponent();
		pageLinkNextComponent.setRelation("next");
		if (resultSize == null) {
			pageLinkNextComponent.setUrl(String.format("%s?%s", requestUrl, createPageLinkString(queryString, count, offset)));
		} else {
			if ((offset + count) >= resultSize)
				return null;
			else
				pageLinkNextComponent.setUrl(String.format("%s?%s", requestUrl, createPageLinkString(queryString, count, offset)));
		}

		return pageLinkNextComponent;

	}

	private BundleLinkComponent generatePreviousComponentLink(String requestUrl, String queryString, int count, int offset) {

		BundleLinkComponent pageLinkPreviousComponent = new BundleLinkComponent();
		pageLinkPreviousComponent.setRelation("previous");
		pageLinkPreviousComponent.setUrl(String.format("%s?%s", requestUrl, createPageLinkString(queryString, count, offset)));
		return pageLinkPreviousComponent;

	}

	private BundleLinkComponent generateSelfComponentLink(String requestUrl, String queryString) {

		BundleLinkComponent pageLinkSelfComponent = new BundleLinkComponent();
		pageLinkSelfComponent.setRelation("self");
		pageLinkSelfComponent.setUrl(String.format("%s%s", requestUrl, queryString != null && queryString.length() > 0 ? "?" + queryString : ""));
		return pageLinkSelfComponent;
	}

	private List<BundleLinkComponent> generatePagingLink(int count, int offset, String requestUrl, String queryString, Long resultSize, SearchTotalModeEnum searchTotalModeEnum) {

		List<BundleLinkComponent> pageLinkComponentList = new ArrayList<BundleLinkComponent>();
		pageLinkComponentList.add(generateSelfComponentLink(requestUrl, queryString));

		if (searchTotalModeEnum == SearchTotalModeEnum.NONE) {
			pageLinkComponentList.add(generateNextComponentLink(requestUrl, queryString, count, offset, null));
		} else {
			BundleLinkComponent pageComponent = generateNextComponentLink(requestUrl, queryString, count, offset, resultSize);
			if (pageComponent != null)
				pageLinkComponentList.add(pageComponent);
		}

		if (count > 0 && offset / count >= 1) {
			pageLinkComponentList.add(generatePreviousComponentLink(requestUrl, queryString, count, offset));
		}

		return pageLinkComponentList;
	}

}