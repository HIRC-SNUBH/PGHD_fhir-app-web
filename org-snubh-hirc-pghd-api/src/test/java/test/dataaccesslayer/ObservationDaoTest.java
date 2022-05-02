package test.dataaccesslayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.hibernate.type.Type;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snubh.hirc.pghd.api.dao.i.IDao;
import org.snubh.hirc.pghd.api.dto.ObservationDto;
import org.snubh.hirc.pghd.api.dto.ObservationMaxDto;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/context-spring.xml", "/context-hibernate.xml" })
public class ObservationDaoTest extends BaseDaoTest {

	@Resource(name = "observationDao")
	private IDao dao;

	@SuppressWarnings("unchecked")
	@Test
	@Ignore
	@Transactional(readOnly = true)
	public void obvComponentPkTest2() throws InstantiationException, IllegalAccessException, ParseException {

		DetachedCriteria crit = DetachedCriteria.forClass(ObservationDto.class);

		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.groupProperty("subject").as("subject"));
		projections.add(Projections.groupProperty("obvConcept.conceptId").as("obsConceptId"));
		projections.add(Projections.groupProperty("effectiveDate").as("effectiveDate"));
		projections.add(Projections.max("identifier").as("identifier"));

		crit.setProjection(projections);

		Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("2011-03-01");

		// crit.add(Restrictions.eq("subjectId", 1L));
		crit.add(Restrictions.eq("obvConcept.conceptId", 45770187L));
		crit.add(Restrictions.eq("effectiveDate", date));

		crit.setResultTransformer(Transformers.aliasToBean(ObservationMaxDto.class));

		List<ObservationMaxDto> list = (List<ObservationMaxDto>) dao.getHibernateTemplate().findByCriteria(crit);
		for (ObservationMaxDto dto : list) {
			ObservationMaxDto maxDto = (ObservationMaxDto) dao.getHibernateTemplate().get(ObservationMaxDto.class, dto.getIdentifier());
			for (ObservationDto subDto : maxDto.getList()) {
				System.out.println(subDto.getIdentifier());
			}
		}
		assertNotEquals(list.size(), 0);
	}

	@Test
	@Ignore
	@Transactional(readOnly = true)
	public void obvComponentPkCountTest() throws InstantiationException, IllegalAccessException, ParseException {

		DetachedCriteria crit = DetachedCriteria.forClass(ObservationDto.class, "obv");
		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.sqlGroupProjection("max(OBSERVATION_ID)", "PERSON_ID, OBSERVATION_CONCEPT_ID, OBSERVATION_DATE", new String[] { "identifier" }, new Type[] { org.hibernate.type.LongType.INSTANCE }));
		crit.setProjection(projections);

		crit.add(Restrictions.in("subject", 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L, 23L, 24L, 25L, 26L, 27L, 28L, 29L, 39L));
		crit.add(Restrictions.eq("obvConcept.conceptId", 45770187L));

		DetachedCriteria critCount = DetachedCriteria.forClass(ObservationDto.class, "obvCount");
		critCount.setProjection(Projections.rowCount());
		critCount.add(Subqueries.propertyIn("identifier", crit));

		Object list = (Object) dao.getHibernateTemplate().findByCriteria(critCount);

		System.out.println(list.toString());

		assertNotEquals(1, 0);
	}

	@Test
	public void daoCompositionCodeCountTest() throws InstantiationException, IllegalAccessException, ParseException {
//		SearchParameterMap searchParameterMap = createTokenParam("code", "https://athena.ohdsi.org/search-terms/terms", "45770187");
		SearchParameterMap searchParameterMap = createTokenParam("code", "http://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html", "924481000000109");
		long count = dao.countComposition(searchParameterMap);
		assertEquals(count, 18L);
	}

	@Test
	public void daoReadTest() throws InstantiationException, IllegalAccessException, ParseException {
		String id = "2000032";
		Observation observation = dao.findById(id);
		showResource(observation);
		assertEquals(observation.getId(), id);
	}

	@Test
	public void daoCompositionReadTest() throws InstantiationException, IllegalAccessException, ParseException {
		String id = "2018587611-45770187-20180912";
		Observation observation = dao.findById(id);
		showResource(observation);
		assertEquals(observation.getId(), id);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoIdSearchTest() throws InstantiationException, IllegalAccessException {
		String id = "20618";
		SearchParameterMap searchParameterMap = createTokenParam("_id", "", id);

		List<Observation> list = (List<Observation>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
		assertEquals(list.size(), 1);
		for (Observation observation : list) {
			assertEquals(observation.getId(), id);
		}
	}

	@Test
	public void daoCompositionIdSearchTest() throws InstantiationException, IllegalAccessException, ParseException {
		String id = "1-45770187-20190101";
		Observation obv = dao.findById(id);
		showResource(obv);
		assertEquals(obv.getId(), id);
	}

	@Test
	public void daoCompositionIdCountTest() throws InstantiationException, IllegalAccessException, ParseException {
		SearchParameterMap searchParameterMap = createTokenParam("_id", "", "1-45770187-20100312");
		long count = dao.countComposition(searchParameterMap);
		assertEquals(1, count);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoStatusSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("status", "", "final");

		List<Observation> list = (List<Observation>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
		for (Observation observation : list) {
			assertEquals(observation.getStatus().toCode(), ObservationStatus.FINAL.toCode());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoPatientSearchTest() throws InstantiationException, IllegalAccessException {
		String patientId = "3";
		SearchParameterMap searchParameterMap = createReferenceParam("patient", patientId);

		List<Observation> list = (List<Observation>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
		for (Observation observation : list) {
			String[] ref = observation.getSubject().getReference().split("/");
			assertEquals(ref[1], patientId);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoPatientCompositionSearchTest() throws InstantiationException, IllegalAccessException, ParseException {
		String pid = "3";
		SearchParameterMap searchParameterMap = createReferenceParam("patient", pid);
//		searchParameterMap.add("_contained", new StringParam("both"));
//		long count = dao.countComposition(searchParameterMap);

		List<Observation> list = (List<Observation>) dao.findBySearchParameterComposition(searchParameterMap, 10, 0);
		showResource(list);
		for (Observation observation : list) {
			String refId = String.format("Patient/%s", pid);
			assertEquals(observation.getSubject().getReference(), refId);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void daoPatientCompositionContainedSearchTest() throws InstantiationException, IllegalAccessException, ParseException {
		String pid = "2018587611";
		SearchParameterMap searchParameterMap = createReferenceParam("patient", pid);
		searchParameterMap.add("_contained", new StringParam("both"));

		List<Observation> list = (List<Observation>) dao.findBySearchParameterComposition(searchParameterMap, 10, 0);
		showResource(list);
		for (Observation observation : list) {
			String refId = String.format("#%s", pid);
			assertEquals(observation.getSubject().getReference(), refId);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoSubjectSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createReferenceParam("subject", "3");

		List<Observation> list = (List<Observation>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
		for (Observation observation : list) {
			String[] ref = observation.getSubject().getReference().split("/");
			assertEquals(ref[1], "3");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoDateSearchTest() throws InstantiationException, IllegalAccessException, ParseException {
		String end = "2010-12-31";
		String start = "2010-12-30";
		Date startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(start);
		Date endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(end);
		HashMap<ParamPrefixEnum, String> dates = new HashMap<>();
		dates.put(ParamPrefixEnum.LESSTHAN_OR_EQUALS, end);
		dates.put(ParamPrefixEnum.GREATERTHAN_OR_EQUALS, start);

		SearchParameterMap searchParameterMap = createDateParam("date", dates);

		List<Observation> list = (List<Observation>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		for (Observation observation : list) {
			Date resourceDate = observation.getEffectiveDateTimeType().getValue();
			assertTrue(resourceDate.getTime() >= startDate.getTime() && resourceDate.getTime() <= endDate.getTime());
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void daoCategorySearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("category", "https://athena.ohdsi.org/search-terms/terms", "44814721");

		List<Observation> list = (List<Observation>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		System.out.println(list.size());
		for (Observation observation : list) {
			assertEquals(observation.getCategory().get(0).getCoding().get(0).getCode(), "OMOP4822320");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoCodeComponentSearchTest() throws InstantiationException, IllegalAccessException {
		int count = 1;
//		SearchParameterMap searchParameterMap = createTokenParam("code", "https://athena.ohdsi.org/search-terms/terms", "45770187");
		SearchParameterMap searchParameterMap = createTokenParam("code", "http://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html", "924481000000109");
//		SearchParameterMap searchParameterMap = createTokenParam("code", "", "924481000000109");

		List<Observation> list = (List<Observation>) dao.findBySearchParameterComposition(searchParameterMap, count, 0);
		showResource(list);
		assertEquals(list.size(), count);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void daoCodeSearchTest() throws InstantiationException, IllegalAccessException {
//		SearchParameterMap searchParameterMap = createTokenParam("code", "https://athena.ohdsi.org/search-terms/terms","4076229");
//		SearchParameterMap searchParameterMap = createTokenParam("code", "http://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html", "224293004");
		SearchParameterMap searchParameterMap = createTokenParam("code", "", "224293004");
		List<Observation> list = (List<Observation>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
		for (Observation observation : list) {
			String code = observation.getCode().getCoding().get(0).getCode();
			String system = observation.getCode().getCoding().get(0).getSystem();
			assertEquals(code, "224293004");
			assertEquals(system,
					"http://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html");

		}

	}

	@Test
	@SuppressWarnings("unchecked")
	public void daoComponentCodeSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("component-code", "http://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html", "723228002");
		searchParameterMap.add("code", new TokenParam("http://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html", "924481000000109"));
		searchParameterMap.add("category", new TokenParam("https://athena.ohdsi.org/search-terms/terms", "44814721"));

//		SearchParameterMap searchParameterMap = createTokenParam("component-code", "", "723228002");
//		SearchParameterMap searchParameterMap = createTokenParam("component-code", "https://athena.ohdsi.org/search-terms/terms", "36716960");

		List<Observation> list = (List<Observation>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);

		assertEquals(list.size(), 5);
	}

	@Test
	public void daoNonSearchParamTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = new SearchParameterMap();

		long count = dao.count(searchParameterMap);
		assertEquals(count, 179L);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoContainedTest() throws InstantiationException, IllegalAccessException {
//		SearchParameterMap searchParameterMap = createTokenParam("subject", "", "1");
		SearchParameterMap searchParameterMap = createTokenParam("_id", "", "20618");
		searchParameterMap.add("_contained", new StringParam("both"));

		List<Observation> list = (List<Observation>) dao.findBySearchParameter(searchParameterMap, 100, 0);
		showResource(list);
		assertEquals(list.size(), 1);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoDeviceTypeTest() throws InstantiationException, IllegalAccessException {
//		 SearchParameterMap searchParameterMap = createTokenParam("device.type", "https://athena.ohdsi.org/search-terms/terms", "2615373");
//		 SearchParameterMap searchParameterMap = createTokenParam("device.type", "http://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html", "A9552");
//		 SearchParameterMap searchParameterMap = createTokenParam("device.type", "", "A9552");
		SearchParameterMap searchParameterMap = createTokenParam("device.type", "https://athena.ohdsi.org/search-terms/terms", "2616512");
//		 SearchParameterMap searchParameterMap = createTokenParam("device.type", "http://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html", "E0167");
//		SearchParameterMap searchParameterMap = createTokenParam("device.type", "", "E0167");

		List<Observation> list = (List<Observation>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
		assertEquals(list.size(), 1);
	}

	@Test
	public void daoSubjectGenderTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("subject.gender", "https://athena.ohdsi.org/search-terms/terms", "8532");
//		 SearchParameterMap searchParameterMap = createTokenParam("subject.gender", "http://hl7.org/fhir/administrative-gender", "female");
//		 SearchParameterMap searchParameterMap = createTokenParam("subject.gender", "", "female");
		long count = dao.count(searchParameterMap);

//		 assertEquals(count, 92L);
		assertEquals(count, 76L);
//		 List<Observation> list = (List<Observation>) dao.findBySearchParameter(searchParameterMap, 10, 0);
//		 showResource(list);
//		 assertEquals(list.size(), 10);
	}

	@SuppressWarnings("unchecked")
	@Test(timeout = 11 * 1000)
	public void daoDeviceUseStartTimingTest() throws ParseException, InstantiationException, IllegalAccessException {
		String start = "2009-12-22";
		String end = "2009-12-31";
		HashMap<ParamPrefixEnum, String> dates = new HashMap<>();
		dates.put(ParamPrefixEnum.GREATERTHAN_OR_EQUALS, start);
		dates.put(ParamPrefixEnum.LESSTHAN_OR_EQUALS, end);

//		SearchParameterMap searchParameterMap = createDateParam("deviceUseStartTiming", dates);
		SearchParameterMap searchParameterMap = createDateParam("deviceUseStartTiming", "deviceUseEndTiming", dates, dates);

		List<Observation> list = (List<Observation>) dao.findBySearchParameter(searchParameterMap, 100, 0);
		System.out.println(list.size());
		for (Observation observation : list) {
			System.out.println(observation.getId());
		}
	}
}