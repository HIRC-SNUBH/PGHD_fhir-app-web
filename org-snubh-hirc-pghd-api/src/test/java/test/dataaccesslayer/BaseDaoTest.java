package test.dataaccesslayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.param.DateAndListParam;
import ca.uhn.fhir.rest.param.DateOrListParam;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.ReferenceOrListParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.StringOrListParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/context-spring.xml", "/context-hibernate.xml" })
public class BaseDaoTest {

	FhirContext ctx = FhirContext.forR4();

	IParser parser = ctx.newJsonParser().setPrettyPrint(true);

	protected SearchParameterMap createTokenParam(String searchParamName, String system, String value) {
		SearchParameterMap searchParameterMap = new SearchParameterMap();

		TokenAndListParam tokenParam = new TokenAndListParam();
		TokenOrListParam orParam = new TokenOrListParam();
		TokenParam param = new TokenParam();

		if (system != null && !"".equals(system)) {
			param.setSystem(system);
		}

		param.setValue(value);

		orParam.add(param);
		tokenParam.addAnd(orParam);

		searchParameterMap.add(searchParamName, tokenParam);

		return searchParameterMap;
	}

	protected SearchParameterMap createTokenParam(String searchParamName, TokenAndListParam andParam) {
		SearchParameterMap searchParameterMap = new SearchParameterMap();

		searchParameterMap.add(searchParamName, andParam);

		return searchParameterMap;
	}

	protected TokenAndListParam setTokenAndParam(TokenOrListParam orParam) {
		TokenAndListParam andParam = new TokenAndListParam();
		andParam.addAnd(orParam);
		return andParam;
	}

	protected TokenOrListParam setTokenOrParam(TokenParam... params) {
		TokenOrListParam orParam = new TokenOrListParam();
		for (TokenParam tokenParam : params) {
			orParam.add(tokenParam);
		}

		return orParam;
	}

	protected TokenParam setTokenParam(String system, String value) {
		TokenParam param = new TokenParam();

		if (system != null && !"".equals(system)) {
			param.setSystem(system);
		}

		param.setValue(value);

		return param;
	}

	protected SearchParameterMap createReferenceParam(String searchParamName, String value) {
		SearchParameterMap searchParameterMap = new SearchParameterMap();

		ReferenceAndListParam referenceAndListParam = new ReferenceAndListParam();
		ReferenceOrListParam referenceOrListParam = new ReferenceOrListParam();
		ReferenceParam referenceParam = new ReferenceParam();

		referenceParam.setValue(value);
		referenceOrListParam.add(referenceParam);
		referenceAndListParam.addAnd(referenceOrListParam);

		searchParameterMap.add(searchParamName, referenceAndListParam);

		return searchParameterMap;
	}

	protected SearchParameterMap createDateParam(String searchParamName, HashMap<ParamPrefixEnum, String> dates) throws ParseException {
		SearchParameterMap searchParameterMap = new SearchParameterMap();

		DateAndListParam dateAndListParam = new DateAndListParam();

		for (Entry<ParamPrefixEnum, String> date : dates.entrySet()) {
			DateOrListParam dateOrList = new DateOrListParam();
			DateParam dateParam = new DateParam();

			String strDate = date.getValue();
			dateParam.setValue(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(strDate));
			dateParam.setPrefix(date.getKey());
			dateOrList.add(dateParam);
			dateAndListParam.addAnd(dateOrList);
		}

		searchParameterMap.add(searchParamName, dateAndListParam);

		return searchParameterMap;
	}

	protected SearchParameterMap createDateParam(String searchParamName1, String searchParamName2, HashMap<ParamPrefixEnum, String> dates1, HashMap<ParamPrefixEnum, String> dates2) throws ParseException {
		SearchParameterMap searchParameterMap = new SearchParameterMap();

		DateAndListParam dateAndListParam1 = new DateAndListParam();

		for (Entry<ParamPrefixEnum, String> date : dates1.entrySet()) {
			DateOrListParam dateOrList = new DateOrListParam();
			DateParam dateParam = new DateParam();

			String strDate = date.getValue();
			dateParam.setValue(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(strDate));
			dateParam.setPrefix(date.getKey());
			dateOrList.add(dateParam);
			dateAndListParam1.addAnd(dateOrList);
		}

		searchParameterMap.add(searchParamName1, dateAndListParam1);

		DateAndListParam dateAndListParam2 = new DateAndListParam();

		for (Entry<ParamPrefixEnum, String> date : dates2.entrySet()) {
			DateOrListParam dateOrList = new DateOrListParam();
			DateParam dateParam = new DateParam();

			String strDate = date.getValue();
			dateParam.setValue(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(strDate));
			dateParam.setPrefix(date.getKey());
			dateOrList.add(dateParam);
			dateAndListParam2.addAnd(dateOrList);
		}

		searchParameterMap.add(searchParamName2, dateAndListParam2);

		return searchParameterMap;
	}

	protected SearchParameterMap createStringParam(String searchParamName, boolean isExact, String value) {
		SearchParameterMap searchParameterMap = new SearchParameterMap();

		StringAndListParam stringAndListParam = new StringAndListParam();
		StringOrListParam stringOrListParam = new StringOrListParam();
		StringParam stringParam = new StringParam();

		stringParam.setExact(isExact);
		stringParam.setValue(value);
		stringOrListParam.add(stringParam);
		stringAndListParam.addAnd(stringOrListParam);

		searchParameterMap.add(searchParamName, stringAndListParam);

		return searchParameterMap;
	}

	protected <T extends IBaseResource> void showResource(T resource) {
		if (resource != null) {
			System.out.println(parser.encodeResourceToString(resource));
		}
	}

	protected <T extends IBaseResource> void showResource(List<T> list) {
		if (list != null && list.size() > 0) {
			for (IBaseResource iBaseResource : list) {
				System.out.println(parser.encodeResourceToString(iBaseResource));
			}
		}
	}
}