package test.dataaccesslayer;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.hl7.fhir.r4.model.DeviceUseStatement;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snubh.hirc.pghd.api.dao.i.IDao;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/context-spring.xml", "/context-hibernate.xml" })
public class DeviceExposureDaoTest extends BaseDaoTest {

	@Resource(name = "deviceExposureDao")
	private IDao dao;

	@Test
	public void daoReadTest() throws InstantiationException, IllegalAccessException, ParseException {
		DeviceUseStatement device = dao.findById("1");
		showResource(device);
		Assert.assertNotNull(device);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoIdSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("_id", "", "1");

		List<DeviceUseStatement> list = (List<DeviceUseStatement>) dao.findBySearchParameter(searchParameterMap, 1, 0);
		showResource(list);
		Assert.assertEquals(list.size(), 1);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoPatientSearchTest() throws InstantiationException, IllegalAccessException {
		String id = "7";
		SearchParameterMap searchParameterMap = createReferenceParam("patient", id);

		List<DeviceUseStatement> list = (List<DeviceUseStatement>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		showResource(list);
		for (DeviceUseStatement dus : list) {
			String refId = String.format("Patient/%s", id);
			Assert.assertEquals(refId, dus.getSubject().getReference());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoSubjectSearchTest() throws InstantiationException, IllegalAccessException {
		String id = "7";
		SearchParameterMap searchParameterMap = createReferenceParam("subject", id);

		List<DeviceUseStatement> list = (List<DeviceUseStatement>) dao.findBySearchParameter(searchParameterMap, 10, 0);

//		showResource(list);
		for (DeviceUseStatement dus : list) {
			String refId = String.format("Patient/%s", id);
			Assert.assertEquals(refId, dus.getSubject().getReference());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoDeviceTypeSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("device.type", "http://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html", "A6207");
//		SearchParameterMap searchParameterMap = createTokenParam("device.type", "", "A6207");
//		SearchParameterMap searchParameterMap = createTokenParam("device.type", "https://athena.ohdsi.org/search-terms/terms", "2615107");

		List<DeviceUseStatement> list = (List<DeviceUseStatement>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		showResource(list);
		Assert.assertEquals(list.size(), 10);
	}

	@Test
	public void daoNonSearchParamTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = new SearchParameterMap();

		long count = dao.count(searchParameterMap);

		Assert.assertEquals(count, 224509L);
	}
}