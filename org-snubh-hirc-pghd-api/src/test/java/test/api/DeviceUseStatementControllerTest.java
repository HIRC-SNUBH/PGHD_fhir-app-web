package test.api;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.DeviceUseStatement;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snubh.hirc.pghd.api.controller.DeviceUseStatementController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import ca.uhn.fhir.rest.param.ReferenceAndListParam;
import ca.uhn.fhir.rest.param.ReferenceOrListParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/context-spring.xml", "/context-hibernate.xml" })
@WebAppConfiguration
public class DeviceUseStatementControllerTest {

	@Autowired
	public DeviceUseStatementController deviceUseStatementController;

	@Test
	public void deviceUseStatementApiReadTest() throws Exception {

		IdType value = new IdType();
		value.setValueAsString("1");
		DeviceUseStatement deviceUseStatement = (DeviceUseStatement) deviceUseStatementController.read(value);
		Assert.assertEquals(deviceUseStatement.getId(), "1");

	}

//	Bundle patientSearchBundle = deviceUseStatementController.searchDevice(theId, theIdentifier, thePatient, theSubject,
//			theType, theOffset, theCount, theSort,
//			summaryEnum, searchTotalEnum, theLastUpdated, request);

	@Test
	public void deviceUseStatementApiSearchDeviceTypeTest() throws Exception {
		TokenAndListParam theType = new TokenAndListParam();
		TokenOrListParam theValue = new TokenOrListParam();
		TokenParam theParameter = new TokenParam();
		theParameter.setValue("2615373");
		theValue.add(theParameter);
		theType.addValue(theValue);
		Bundle patientSearchBundle = deviceUseStatementController.search(null, null, null, theType, null, null, null, null, null, null, null);
		for (BundleEntryComponent bundleEntryComponent : patientSearchBundle.getEntry()) {
			Resource resource = bundleEntryComponent.getResource();
			Assert.assertEquals((resource instanceof DeviceUseStatement), true);
		}
	}

	@Test
	public void deviceUseStatementApiSearchPatientTest() throws Exception {
		ReferenceAndListParam thePatient = new ReferenceAndListParam();
		ReferenceOrListParam theValue = new ReferenceOrListParam();
		ReferenceParam theParameter = new ReferenceParam();
		theParameter.setValue("Patient/1");
		theValue.add(theParameter);
		thePatient.addValue(theValue);
		Bundle patientSearchBundle = deviceUseStatementController.search(null, thePatient, null, null, null, null, null, null, null, null, null);
		for (BundleEntryComponent bundleEntryComponent : patientSearchBundle.getEntry()) {
			Resource resource = bundleEntryComponent.getResource();
			Assert.assertEquals((resource instanceof Patient), true);
			Patient patient = (Patient) resource;
			for (Reference ref : patient.getGeneralPractitioner()) {
				Assert.assertEquals(ref.getReference().contains(theParameter.getValue()), true);
			}

		}
	}

}
