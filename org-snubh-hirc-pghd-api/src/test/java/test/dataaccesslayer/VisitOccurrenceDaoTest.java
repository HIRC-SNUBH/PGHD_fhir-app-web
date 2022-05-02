package test.dataaccesslayer;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.hl7.fhir.r4.model.Encounter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snubh.hirc.pghd.api.dao.i.IDao;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/context-spring.xml", "/context-hibernate.xml" })
public class VisitOccurrenceDaoTest extends BaseDaoTest {

	@Resource(name = "visitOccurrenceDao")
	public IDao dao;

	@Test
	public void daoReadTest() throws InstantiationException, IllegalAccessException, ParseException {
		Encounter encounter = dao.findById("1");
		showResource(encounter);
		Assert.assertNotNull(encounter);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoIdSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchMap = createTokenParam("_id", "", "1");

		List<Encounter> list = (List<Encounter>) dao.findBySearchParameter(searchMap, 10, 0);
		showResource(list);
		for (Encounter encounter : list) {
			Assert.assertEquals(encounter.getId(), "1");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoServiceProviderSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchMap = createReferenceParam("service-provider", "16");

		List<Encounter> list = (List<Encounter>) dao.findBySearchParameter(searchMap, 10, 0);
		showResource(list);
		for (Encounter encounter : list) {
			String[] ref = encounter.getServiceProvider().getReference().split("/");
			Assert.assertEquals(ref[1], "16");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoPatientSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchMap = createTokenParam("patient", "", "56");

		List<Encounter> list = (List<Encounter>) dao.findBySearchParameter(searchMap, 10, 0);
		showResource(list);
		for (Encounter encounter : list) {
			String[] ref = encounter.getSubject().getReference().split("/");
			Assert.assertEquals(ref[1], "56");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoSubjectSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchMap = createTokenParam("patient", "", "2");

		List<Encounter> list = (List<Encounter>) dao.findBySearchParameter(searchMap, 10, 0);
		showResource(list);
		for (Encounter encounter : list) {
			String[] ref = encounter.getSubject().getReference().split("/");
			Assert.assertEquals(ref[1], "2");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoClassSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("class", "http://terminology.hl7.org/CodeSystem/v3-ActCode", "IMP");
//		SearchParameterMap searchParameterMap = createTokenParam("class", "", "IMP");		

		List<Encounter> list = (List<Encounter>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
		for (Encounter encounter : list) {
			Assert.assertEquals(encounter.getClass_().getCode(), "IMP");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoClassCustomeCodeSearchTest() throws InstantiationException, IllegalAccessException {
//		SearchParameterMap searchParameterMap = createTokenParam("class", "https://athena.ohdsi.org/", "OMOP4822461");
		SearchParameterMap searchParameterMap = createTokenParam("class", "", "OMOP4822461");

		List<Encounter> list = (List<Encounter>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
		for (Encounter encounter : list) {
			Assert.assertEquals(encounter.getClass_().getCode(), "OMOP4822461");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoClassCustomeConceptIdSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("class", "https://athena.ohdsi.org/search-terms/terms", "32036");

		List<Encounter> list = (List<Encounter>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
		for (Encounter encounter : list) {
			Assert.assertEquals(encounter.getClass_().getCode(), "OMOP4822461");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoStatusSearchTest() throws InstantiationException, IllegalAccessException {
//		SearchParameterMap searchParameterMap = createTokenParam("status", "", "in-progress");
		SearchParameterMap searchParameterMap = createTokenParam("status", "", "finished");
//		SearchParameterMap searchParameterMap = createTokenParam("status", "", "planned");

		List<Encounter> list = (List<Encounter>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		System.out.println(list.size());
		showResource(list);
		for (Encounter encounter : list) {
			Assert.assertEquals(encounter.getStatus().toCode(), "finished");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoPeriodEqSearchTest() throws ParseException, InstantiationException, IllegalAccessException {
		HashMap<ParamPrefixEnum, String> dates = new HashMap<>();
		dates.put(ParamPrefixEnum.EQUAL, "2010-11-23");
		SearchParameterMap searchParameterMap = createDateParam("date", dates);

		List<Encounter> list = (List<Encounter>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		Assert.assertEquals(list.size(), 10);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoPeriodLeSearchTest() throws ParseException, InstantiationException, IllegalAccessException {
		HashMap<ParamPrefixEnum, String> dates = new HashMap<>();
		dates.put(ParamPrefixEnum.LESSTHAN_OR_EQUALS, "2008-01-01");
		SearchParameterMap searchParameterMap = createDateParam("date", dates);

		List<Encounter> list = (List<Encounter>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		Assert.assertEquals(list.size(), 10);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoPeriodGeSearchTest() throws ParseException, InstantiationException, IllegalAccessException {
		HashMap<ParamPrefixEnum, String> dates = new HashMap<>();
		dates.put(ParamPrefixEnum.GREATERTHAN_OR_EQUALS, "2010-12-30");
		SearchParameterMap searchParameterMap = createDateParam("date", dates);

		List<Encounter> list = (List<Encounter>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		Assert.assertEquals(list.size(), 10);
	}

	@Test
	public void daoPeriodRangeSearchTest() throws ParseException, InstantiationException, IllegalAccessException {
		HashMap<ParamPrefixEnum, String> dates = new HashMap<>();
		dates.put(ParamPrefixEnum.GREATERTHAN_OR_EQUALS, "2010-03-01");
		dates.put(ParamPrefixEnum.LESSTHAN_OR_EQUALS, "2010-03-03");
		SearchParameterMap searchParameterMap = createDateParam("date", dates);

		long count = dao.count(searchParameterMap);

		Assert.assertEquals(count, 17710L);
	}

	@Test
	public void daoNonSearchParamTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = new SearchParameterMap();

		long count = dao.count(searchParameterMap);

		Assert.assertEquals(count, 5579545L);
	}
}