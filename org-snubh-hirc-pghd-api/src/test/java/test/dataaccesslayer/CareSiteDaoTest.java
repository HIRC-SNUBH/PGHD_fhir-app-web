package test.dataaccesslayer;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.hl7.fhir.r4.model.Organization;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snubh.hirc.pghd.api.dao.i.IDao;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/context-spring.xml", "/context-hibernate.xml" })
public class CareSiteDaoTest extends BaseDaoTest {

	@Resource(name = "careSiteDao")
	public IDao dao;

	@Test
	public void daoReadTest() throws InstantiationException, IllegalAccessException, ParseException {
		Organization organization = dao.findById("11");
		showResource(organization);
		Assert.assertEquals(organization.getId(), "11");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoIdSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("_id", null, "1");

		List<Organization> list = (List<Organization>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		showResource(list);

		for (Organization organization : list) {
			Assert.assertEquals(organization.getId(), "1");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoNameSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createStringParam("name", true, "snubh");

		List<Organization> list = (List<Organization>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		showResource(list);

		for (Organization organization : list) {
			Assert.assertTrue(organization.getName().equalsIgnoreCase("snubh"));
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoTypeSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("type", "https://athena.ohdsi.org/search-terms/terms", "8756");
//		SearchParameterMap searchParameterMap = createTokenParam("type", "http://www.cms.gov/Medicare/Medicare-Fee-for-Service-Payment/PhysicianFeeSched/downloads//Website_POS_database.pdf", "22");
//		SearchParameterMap searchParameterMap = createTokenParam("type", "", "22");

		List<Organization> list = (List<Organization>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		showResource(list);

		for (Organization organization : list) {
			String code = organization.getTypeFirstRep().getCodingFirstRep().getCode();
			Assert.assertEquals(code, "22");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoAddressCitySearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createStringParam("address-city", true, "paris");

		List<Organization> list = (List<Organization>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		showResource(list);
		for (Organization organization : list) {
			String city = organization.getAddressFirstRep().getCity();
			Assert.assertEquals(city, "paris");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoAddressStateSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createStringParam("address-state", true, "MO");

		List<Organization> list = (List<Organization>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		showResource(list);
		for (Organization organization : list) {
			String state = organization.getAddressFirstRep().getState();
			Assert.assertEquals(state, "MO");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoAddressPostalCodeSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createStringParam("address-postalcode", true, "00000");

		List<Organization> list = (List<Organization>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		showResource(list);
		for (Organization organization : list) {
			String postalCode = organization.getAddressFirstRep().getPostalCode();
			Assert.assertEquals(postalCode, "00000");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoAddressSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createStringParam("address", true, "1st avn");

		List<Organization> list = (List<Organization>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		showResource(list);
		for (Organization organization : list) {
			String line = organization.getAddressFirstRep().getLine().get(0).asStringValue();
			Assert.assertEquals(line, "1st avn");
		}
	}

}