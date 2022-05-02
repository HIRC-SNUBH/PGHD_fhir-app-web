package test.api;

import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snubh.hirc.pghd.api.controller.ObservationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ca.uhn.fhir.rest.param.DateAndListParam;
import ca.uhn.fhir.rest.param.DateOrListParam;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.ReferenceOrListParam;
import ca.uhn.fhir.rest.param.ReferenceParam;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/context-spring.xml", "/context-hibernate.xml" })
@WebAppConfiguration
public class ObservationControllerTest {

	@Autowired
	public ObservationController observationController;

	@Autowired
	private MockMvc mockMvc;

	@Before
	public void setUpMockMvc() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(observationController).build();
	}

	@Test
	public void observationReadApiTest() throws Exception {
		IdType value = new IdType();
		value.setValueAsString("1");
		Observation observation = (Observation) observationController.read(value);
		Assert.assertEquals(observation.getId(), "1");
	}

	@Test
	public void observationCompositionReadApiTest() throws Exception {
		IdType value = new IdType();
		value.setValueAsString("3_37399654_20101231");
		Observation observation = (Observation) observationController.read(value);
		Assert.assertEquals(observation.getId(), "3_37399654_20101231");
		Assert.assertNotNull(observation.getComponent());
	}

	@Test
	public void observationApiProfileTest() throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		mockMvc.perform(MockMvcRequestBuilders.get("/org-snubh-hirc-pghd-api/fhir/Observation?code=997671000000106&_profile=http://localhost:8080/fhir/StructureDefinition/compositionObservation")).andReturn();
	}

	@Test
	public void observationApiSearchDateTest() throws Exception {
		DateAndListParam theDateParam = new DateAndListParam();
		DateOrListParam theValue = new DateOrListParam();
		DateParam theParameter = new DateParam();
		Date startDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.set(2010, 2, 12, 0, 0, 0);
		startDate = cal.getTime();
		theParameter.setValue(startDate);
		theParameter.setPrefix(ParamPrefixEnum.GREATERTHAN_OR_EQUALS);
		theValue.add(theParameter);
		DateOrListParam theValue2 = new DateOrListParam();
		DateParam theParameter2 = new DateParam();
		Date endDate = new Date();
		cal.set(2010, 2, 13, 0, 0, 0);
		endDate = cal.getTime();
		theParameter2.setValue(endDate);
		theParameter2.setPrefix(ParamPrefixEnum.LESSTHAN);
		theValue2.add(theParameter2);
		theDateParam.addValue(theValue);
		theDateParam.addValue(theValue2);
		Bundle patientSearchBundle = observationController.searchObservation(null, null, null, null, null, null, theDateParam, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		for (BundleEntryComponent bundleEntryComponent : patientSearchBundle.getEntry()) {
			Resource resource = bundleEntryComponent.getResource();
			Assert.assertEquals((resource instanceof Observation), true);
			Observation observation = (Observation) resource;
			DateTimeType dateTime = observation.getEffectiveDateTimeType();
			Assert.assertEquals((dateTime.getValue().equals(startDate) || dateTime.getValue().after(startDate)) && dateTime.getValue().before(endDate), true);
		}
	}

	@Test
	public void observationApiSearchPatientTest() throws Exception {
		ReferenceAndListParam thePatient = new ReferenceAndListParam();
		ReferenceOrListParam theValue = new ReferenceOrListParam();
		ReferenceParam theParameter = new ReferenceParam();
		theParameter.setValue("Patient/1");
		theValue.add(theParameter);
		thePatient.addValue(theValue);
		Bundle patientSearchBundle = observationController.searchObservation(null, null, null, null, thePatient, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		for (BundleEntryComponent bundleEntryComponent : patientSearchBundle.getEntry()) {
			Resource resource = bundleEntryComponent.getResource();
			Assert.assertEquals((resource instanceof Observation), true);
			Observation observation = (Observation) resource;
			Reference ref = observation.getSubject();
			Assert.assertEquals(ref.getReference().contains(theParameter.getValue()), true);
		}
	}

}
