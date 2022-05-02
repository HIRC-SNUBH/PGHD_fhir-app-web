package test.api;

import java.util.Calendar;
import java.util.Date;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.codesystems.AdministrativeGender;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snubh.hirc.pghd.api.controller.PatientController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import ca.uhn.fhir.rest.param.DateAndListParam;
import ca.uhn.fhir.rest.param.DateOrListParam;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.ReferenceOrListParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/context-spring.xml", "/context-hibernate.xml" })
@WebAppConfiguration
public class PatientControllerTest {

	@Autowired
	public PatientController patientController;

	@Test
	public void patientApiReadTest() throws Exception {
		IdType value = new IdType();
		value.setValueAsString("1");
		Patient patient = (Patient) patientController.read(value);
		Assert.assertEquals(patient.getId(), "1");
	}

	@Test
	public void patientApiSearchPractitionerTest() throws Exception {
		ReferenceAndListParam theGeneralPractitioner = new ReferenceAndListParam();
		ReferenceOrListParam theValue = new ReferenceOrListParam();
		ReferenceParam theParameter = new ReferenceParam();
		theParameter.setValue("Practitioner/1");
		theValue.add(theParameter);
		theGeneralPractitioner.addValue(theValue);
		Bundle patientSearchBundle = patientController.search(null, null, theGeneralPractitioner, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		for (BundleEntryComponent bundleEntryComponent : patientSearchBundle.getEntry()) {
			Resource resource = bundleEntryComponent.getResource();
			Assert.assertEquals((resource instanceof Patient), true);
			Patient patient = (Patient) resource;
			for (Reference ref : patient.getGeneralPractitioner()) {
				Assert.assertEquals(ref.getReference().contains(theParameter.getValue()), true);
			}

		}
	}

	@Test
	public void patientApiSearchOrganizationTest() throws Exception {
		ReferenceAndListParam theOrganization = new ReferenceAndListParam();
		ReferenceOrListParam theValue = new ReferenceOrListParam();
		ReferenceParam theParameter = new ReferenceParam();
		theParameter.setValue("Organization/1");
		theValue.add(theParameter);
		theOrganization.addValue(theValue);
		Bundle patientSearchBundle = patientController.search(null, null, null, theOrganization, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		for (BundleEntryComponent bundleEntryComponent : patientSearchBundle.getEntry()) {
			Resource resource = bundleEntryComponent.getResource();
			Assert.assertEquals((resource instanceof Patient), true);
			Patient patient = (Patient) resource;
			Reference ref = patient.getManagingOrganization();
			Assert.assertEquals(ref.getReference().contains(theParameter.getValue()), true);
		}
	}

	@Test
	public void patientApiSearchGenderTest() throws Exception {
		TokenAndListParam theGender = new TokenAndListParam();
		TokenOrListParam theValue = new TokenOrListParam();
		TokenParam theParameter = new TokenParam();
		theParameter.setValue(AdministrativeGender.MALE.toCode());
		theValue.add(theParameter);
		theGender.addValue(theValue);
		Bundle patientSearchBundle = patientController.search(null, null, null, null, theGender, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
		for (BundleEntryComponent bundleEntryComponent : patientSearchBundle.getEntry()) {
			Resource resource = bundleEntryComponent.getResource();
			Assert.assertEquals((resource instanceof Patient), true);
			Patient patient = (Patient) resource;
			Assert.assertEquals(patient.getGender().equals(org.hl7.fhir.r4.model.Enumerations.AdministrativeGender.MALE), true);
		}
	}

	@Test
	public void patientApiSearchBirthdateTest() throws Exception {
		DateAndListParam theBirthDate = new DateAndListParam();
		DateOrListParam theValue = new DateOrListParam();
		DateParam theParameter = new DateParam();
		Date startDate = new Date();
		Calendar cal = Calendar.getInstance();
		cal.set(1922, 2, 1, 0, 0, 0);
		startDate = cal.getTime();
		theParameter.setValue(startDate);
		theParameter.setPrefix(ParamPrefixEnum.GREATERTHAN_OR_EQUALS);
		theValue.add(theParameter);
		DateOrListParam theValue2 = new DateOrListParam();
		DateParam theParameter2 = new DateParam();
		Date endDate = new Date();
		cal.set(1922, 3, 1, 0, 0, 0);
		endDate = cal.getTime();
		theParameter2.setValue(endDate);
		theParameter2.setPrefix(ParamPrefixEnum.LESSTHAN);
		theValue2.add(theParameter2);
		theBirthDate.addValue(theValue);
		theBirthDate.addValue(theValue2);
		Bundle patientSearchBundle = patientController.search(null, null, null, null, null, theBirthDate, null, null, null, null, null, null, null, null, null, null, null, null, null);
		for (BundleEntryComponent bundleEntryComponent : patientSearchBundle.getEntry()) {
			Resource resource = bundleEntryComponent.getResource();
			Assert.assertEquals((resource instanceof Patient), true);
			Patient patient = (Patient) resource;
		}
	}

}
