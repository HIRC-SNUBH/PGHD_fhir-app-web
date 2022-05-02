package test.api;

import java.util.Calendar;
import java.util.Date;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snubh.hirc.pghd.api.controller.EncounterController;
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
public class EncounterControllerTest {

	@Autowired
	public EncounterController encounterController;

	@Test
	public void encounterApiReadTest() throws Exception {
		IdType value = new IdType();
		value.setValueAsString("1");
		Encounter encounter = (Encounter) encounterController.read(value);
		Assert.assertEquals(encounter.getId(), "1");
	}

	@Test
	public void encounterApiSearchServiceProviderTest() throws Exception {
		ReferenceAndListParam theServiceProvider = new ReferenceAndListParam();
		ReferenceOrListParam theValue = new ReferenceOrListParam();
		ReferenceParam theParameter = new ReferenceParam();
		theParameter.setValue("Organization/1");
		theValue.add(theParameter);
		theServiceProvider.addValue(theValue);
		Bundle encounterSearchBundle = encounterController.search(null, theServiceProvider, null, null, null, null, null, null, null, null, null, null, null, null, null);
		for (BundleEntryComponent bundleEntryComponent : encounterSearchBundle.getEntry()) {
			Resource resource = bundleEntryComponent.getResource();
			Assert.assertEquals((resource instanceof Encounter), true);
			Encounter encounter = (Encounter) resource;
			Reference ref = encounter.getServiceProvider();
			Assert.assertEquals(ref.getReference().contains(theParameter.getValue()), true);
		}
	}

	@Test
	public void encounterApiSearchClassTest() throws Exception {
		TokenAndListParam theClass = new TokenAndListParam();
		TokenOrListParam theValue = new TokenOrListParam();
		TokenParam theParameter = new TokenParam();
		theParameter.setValue("IMP");
		theValue.add(theParameter);
		theClass.addValue(theValue);
		Bundle encounterSearchBundle = encounterController.search(null, null, null, null, theClass, null, null, null, null, null, null, null, null, null, null);
		for (BundleEntryComponent bundleEntryComponent : encounterSearchBundle.getEntry()) {
			Resource resource = bundleEntryComponent.getResource();
			Assert.assertEquals((resource instanceof Encounter), true);
			Encounter encounter = (Encounter) resource;
			Coding coding = encounter.getClass_();
			Assert.assertEquals(coding.getCode().equals("IMP"), true);
		}
	}

	@Test
	public void encounterApiSearchPeriodTest() throws Exception {
		DateAndListParam thePeriod = new DateAndListParam();
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
		theValue.add(theParameter);
		theValue2.add(theParameter2);
		thePeriod.addValue(theValue);
		thePeriod.addValue(theValue2);
		Bundle encounterSearchBundle = encounterController.search(null, null, null, null, null, null, null, thePeriod, null, null, null, null, null, null, null);
		for (BundleEntryComponent bundleEntryComponent : encounterSearchBundle.getEntry()) {
			Resource resource = bundleEntryComponent.getResource();
			Assert.assertEquals((resource instanceof Encounter), true);
			Encounter encounter = (Encounter) resource;
			Period period = encounter.getPeriod();
		}
	}

}
