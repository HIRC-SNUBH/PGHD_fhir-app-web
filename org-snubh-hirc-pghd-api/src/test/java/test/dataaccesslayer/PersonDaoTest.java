package test.dataaccesslayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hl7.fhir.r4.model.Patient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snubh.hirc.pghd.api.dao.i.IDao;
import org.snubh.hirc.pghd.api.dto.DeathDto;
import org.snubh.hirc.pghd.api.dto.PersonDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/context-spring.xml", "/context-hibernate.xml" })
public class PersonDaoTest extends BaseDaoTest {

	@Resource(name = "personDao")
	public IDao dao;

	@Autowired
	private SessionFactory sessionFactory;

	private Session session;

	@Before
	public void setup() {

		session = sessionFactory.openSession();
	}

	@SuppressWarnings("unchecked")
	@Test
	@Ignore
	public void joinWithAssociationTest() throws InstantiationException, IllegalAccessException {
		session.beginTransaction();

		DetachedCriteria crit = DetachedCriteria.forClass(PersonDto.class);

		DetachedCriteria criteria = crit.createCriteria("address");

		criteria.add(Restrictions.eq("state", "WY"));

		List<PersonDto> list = crit.getExecutableCriteria(session).list();

		Assert.assertEquals(list.size(), 2378);
	}

	@SuppressWarnings("unchecked")
	@Test
	@Ignore
	public void joinWithoutAssociationTest() throws ParseException {
		session.beginTransaction();

		DetachedCriteria crit = DetachedCriteria.forClass(PersonDto.class);

		DetachedCriteria deathCriteria = DetachedCriteria.forClass(DeathDto.class);
		deathCriteria.setProjection(Property.forName("identifier"));
		Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("2010-10-01");
		deathCriteria.add(Restrictions.eq("deathDate", date));

		crit.add(Property.forName("identifier").in(deathCriteria));

		List<PersonDto> list = crit.getExecutableCriteria(session).list();
		Assert.assertEquals(list.size(), 1);
	}

	@Test
	public void daoReadTest() throws InstantiationException, IllegalAccessException, ParseException {
//		Patient patient = dao.findById("4");
		Patient patient = dao.findById("16");

		showResource(patient);
		org.junit.Assert.assertNotNull(patient);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoIdSearchTest() throws InstantiationException, IllegalAccessException {
//		SearchParameterMap searchParameterMap = createTokenParam("_id", null, "1");
		SearchParameterMap searchParameterMap = createTokenParam("_id", setTokenAndParam(setTokenOrParam(setTokenParam(null, "1"), setTokenParam(null, "2"))));

		List<Patient> list = (List<Patient>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		showResource(list);
		Assert.assertEquals(list.size(), 2);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoIdentifierSearchTest() throws InstantiationException, IllegalAccessException {
//		SearchParameterMap searchParameterMap = createTokenParam("identifier", "", "1");
		SearchParameterMap searchParameterMap = createTokenParam("identifier", setTokenAndParam(setTokenOrParam(setTokenParam(null, "1"), setTokenParam(null, "2"))));

		List<Patient> list = (List<Patient>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		showResource(list);
		Assert.assertEquals(list.size(), 2);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoOrganizationSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createReferenceParam("organization", "1");

		List<Patient> list = (List<Patient>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		showResource(list);
		Assert.assertEquals(list.size(), 2);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoFhirConceptGenderSearchTest() throws InstantiationException, IllegalAccessException {
//		SearchParameterMap searchParameterMap = createTokenParam("gender", "http://hl7.org/fhir/administrative-gender", "female");
		SearchParameterMap searchParameterMap = createTokenParam("gender", "", "female");

		List<Patient> list = (List<Patient>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		for (Patient patient : list) {
			Assert.assertEquals(patient.getGender().toCode(), "female");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoConceptIdGenderSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("gender", "https://athena.ohdsi.org/search-terms/terms", "8532");

		List<Patient> list = (List<Patient>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		for (Patient patient : list) {
			Assert.assertEquals(patient.getGender().toCode(), "female");
		}
	}

	@SuppressWarnings("unchecked")
	@Test(timeout = 3 * 1000)
	public void daoBirthDateSearchTest() throws InstantiationException, IllegalAccessException, ParseException {
		String end = "1985-08-01";
		String start = "1983-12-01";
		Date startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(start);
		Date endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(end);
		HashMap<ParamPrefixEnum, String> dates = new HashMap<>();
		dates.put(ParamPrefixEnum.LESSTHAN_OR_EQUALS, end);
		dates.put(ParamPrefixEnum.GREATERTHAN_OR_EQUALS, start);

		SearchParameterMap searchMap = createDateParam("birthdate", dates);

		List<Patient> list = (List<Patient>) dao.findBySearchParameter(searchMap, 10, 0);
		for (Patient patient : list) {
			Date resourceDate = patient.getBirthDate();
			Assert.assertTrue(resourceDate.getTime() >= startDate.getTime() && resourceDate.getTime() <= endDate.getTime());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoAddressSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createStringParam("address", true, "MO");

		List<Patient> list = (List<Patient>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
		for (Patient patient : list) {
			Assert.assertEquals(patient.getAddressFirstRep().getState(), "MO");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoAddressCitySearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createStringParam("address-city", false, "par");

		List<Patient> list = (List<Patient>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		for (Patient patient : list) {
			Assert.assertEquals(patient.getAddressFirstRep().getCity(), "paris");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoAddressZipSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createStringParam("address-postalcode", false, "00");

		List<Patient> list = (List<Patient>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
		for (Patient patient : list) {
			Assert.assertEquals(patient.getAddressFirstRep().getPostalCode(), "00000");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoAddressStateSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createStringParam("address-state", false, "W");

		List<Patient> list = (List<Patient>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
		for (Patient patient : list) {
//			Assert.assertEquals(patient.getAddressFirstRep().getState(), "WY");
			Assert.assertTrue(patient.getAddressFirstRep().getState().contains("W"));
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoDeathDateSearchTest() throws ParseException, InstantiationException, IllegalAccessException {
		String end = "2010-12-11";
		String start = "2010-12-01";
		Date startDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(start);
		Date endDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(end);
		HashMap<ParamPrefixEnum, String> dates = new HashMap<>();
		dates.put(ParamPrefixEnum.LESSTHAN_OR_EQUALS, end);
		dates.put(ParamPrefixEnum.GREATERTHAN_OR_EQUALS, start);

		SearchParameterMap searchParameterMap = createDateParam("death-date", dates);
		List<Patient> list = (List<Patient>) dao.findBySearchParameter(searchParameterMap, 10, 0);
//		showResource(list);
		for (Patient patient : list) {
			Date resourceDate = patient.getDeceasedDateTimeType().getValue();
			Assert.assertTrue(resourceDate.getTime() >= startDate.getTime() && resourceDate.getTime() <= endDate.getTime());
		}
	}

	@Test
	@Ignore
	public void daoNonSearchParamTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = new SearchParameterMap();

		long count = dao.count(searchParameterMap);

		Assert.assertEquals(count, 116352L);
	}
}