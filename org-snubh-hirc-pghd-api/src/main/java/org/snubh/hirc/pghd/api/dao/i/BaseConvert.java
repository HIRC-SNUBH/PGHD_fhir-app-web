package org.snubh.hirc.pghd.api.dao.i;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.CanonicalType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations.ResourceType;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Meta;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;
import org.snubh.hirc.pghd.api.dto.ConceptDto;
import org.snubh.hirc.pghd.api.dto.LocationDto;
import org.snubh.hirc.pghd.api.dto.ObservationDto;
import org.snubh.hirc.pghd.api.dto.PersonSummaryDto;
import org.snubh.hirc.pghd.api.util.ValueSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BaseConvert<T, T2> {

	protected static final String OMOPGENERATEDURL = "https://athena.ohdsi.org/";

	protected static final String SNUBHGENERATEDURL = "https://www.cdm.snubh.org/Atlas";

	protected boolean containedFlag;

	protected String profileUrl;

	@Value("${server.profileurl}")
	protected String url;

	public BaseConvert() {

	}

	@PostConstruct
	public void init() {
		profileUrl = url + "/StructureDefinition/";
	}

	public List<T2> convertList(List<T> dtoList) {

		List<T2> list = new ArrayList<>();

		if (dtoList != null && dtoList.size() > 0) {
			for (T dto : dtoList) {
				T2 resource = (T2) convert(dto);
				if (resource != null) {
					list.add(resource);
				}
			}
		}
		return list;
	}

	public T2 convert(T d) {
		return null;
	}

	protected Patient generateContainedPerson(PersonSummaryDto dto) {
		Patient patient = new Patient();
		patient.setId(dto.getIdentifierAsString());
		patient.setBirthDate(dto.getBirthDate());
		Long genderCode = dto.getGenderConceptId();
		if (genderCode != null) {
			if (genderCode == ValueSet.Gender.M)
				patient.setGender(org.hl7.fhir.r4.model.Enumerations.AdministrativeGender.MALE);
			if (genderCode == ValueSet.Gender.F)
				patient.setGender(org.hl7.fhir.r4.model.Enumerations.AdministrativeGender.FEMALE);
		}
		if (dto.getRaceConcept() != null) {			
			patient.addExtension(generateRaceExtension(dto.getRaceConcept().getConceptId()));
		}
		return patient;
	}
	
	protected Extension generateRaceExtension(Long raceConcept) {
		if (Objects.isNull(raceConcept)) return null;
		
		Extension race = new Extension();
		race.setUrl(ValueSet.Race.US_CORE_RACE_URL);
		if (ValueSet.Race.KOREAN == raceConcept) {
			race.addExtension("ombCategory", new Coding(ValueSet.Race.US_CDC_CODE_SYSTEM, ValueSet.Race.US_CDC_ASIAN_CODE, ValueSet.Race.US_CDC_ASIAN_DISPLAY));
			race.addExtension("detailed", new Coding(ValueSet.Race.US_CDC_CODE_SYSTEM, ValueSet.Race.US_CDC_KOREAN_CODE, ValueSet.Race.US_CDC_KOREAN_DISPLAY));
			race.addExtension("text", new StringType(ValueSet.Race.KOREAN_DISPLAY));
		} else if (ValueSet.Race.UNKNOWN == raceConcept) {
			race.addExtension("ombCategory", new Coding(ValueSet.Race.V3_NULL_FLAVOR_CODE_SYSTEM, ValueSet.Race.V3_NULL_FLAVOR_UNKNOWN_CODE, ValueSet.Race.V3_NULL_FLAVOR_UNKNOWN_DISPLAY));
			race.addExtension("text", new StringType(ValueSet.Race.V3_NULL_FLAVOR_UNKNOWN_DISPLAY));
		}
		
		return race;
	}

	protected Identifier generateIdentifier(String identifierValue) {
		Identifier identifier = new Identifier();
		if (identifierValue != null)
			identifier.setValue(identifierValue);
		return identifier;
	}

	protected CanonicalType generateReferenceCanonical(ResourceType resourceType, Long refId) {

		if (refId == null)
			return null;

		CanonicalType canonical = new CanonicalType();
		canonical.setValue(String.format("%s/%s/%d", "", resourceType.toCode(), refId));
		return canonical;
	}

	protected Reference generateReference(String resourceType, Long refId) {

		if (refId == null)
			return null;

		Reference reference = new Reference();
		reference.setReference(String.format("%s/%d", resourceType, refId));
		return reference;
	}

	protected Reference generateReference(ResourceType resourceType, Long refId) {
		return generateReference(resourceType.toCode(), refId);
	}
	
	protected Reference generateConatainedResourceReference(Long refId) {
		if (Objects.isNull(refId)) return null;
		
		Reference reference = new Reference();
		reference.setReference(String.format("#%d", refId));
		return reference;
	}

	protected Period generatePeriod(Period period, Date start, Date end) {

		period.setStart(start);
		period.setEnd(end);

		return period;
	}

	protected Period generateNewPeriod(Date start, Date end) {
		return generatePeriod(new Period(), start, end);
	}

	protected CodeableConcept generateCodeableConcept(String system, String code, String display, String text) {
		CodeableConcept codeableConcept = new CodeableConcept();
		String systemVal = "";
		if (system != null) {
			switch (system) {
			case ValueSet.ObservationSystemType.OMOP_GENERATED:
				systemVal = OMOPGENERATEDURL;
				break;
			case ValueSet.ObservationSystemType.SNUBH_GENERATED:
				systemVal = SNUBHGENERATEDURL;
				break;
			default:
				systemVal = system;
				break;
			}
		}
		codeableConcept.addCoding(new Coding(systemVal, code, display));
		if (text != null)
			codeableConcept.setText(text);
		return codeableConcept;
	}

	protected CodeableConcept generateCodeableConcept(ConceptDto conceptDto, String text) {
		CodeableConcept codeableConcept = new CodeableConcept();
		String codeValue = null;
		String displayValue = null;
		String systemValue = null;
		if (conceptDto != null) {
			codeValue = conceptDto.getConceptCode();
			displayValue = conceptDto.getConceptName();
			if (conceptDto.getCodeSystemId() != null)
				systemValue = conceptDto.getCodeSystemId().getCodeSystem();
			codeableConcept = generateCodeableConcept(systemValue, codeValue, displayValue, text);
		}

		return codeableConcept;
	}

	protected CodeableConcept generateCodeableConcept(ConceptDto conceptDto) {
		CodeableConcept codeableConcept = new CodeableConcept();
		String codeValue = null;
		String displayValue = null;
		String systemValue = null;
		if (conceptDto != null) {
			codeValue = conceptDto.getConceptCode();
			displayValue = conceptDto.getConceptName();
			if (conceptDto.getCodeSystemId() != null)
				systemValue = conceptDto.getCodeSystemId().getCodeSystem();
			codeableConcept = generateCodeableConcept(systemValue, codeValue, displayValue, null);
		}

		return codeableConcept;
	}

	protected Coding generateCoding(ConceptDto conceptDto) {
		Coding coding = new Coding();
		String codeValue = null;
		String displayValue = null;
		String systemValue = null;
		if (conceptDto != null && conceptDto.getCodeSystemId() != null) {
			codeValue = conceptDto.getConceptCode();
			displayValue = conceptDto.getConceptName();
		}
		if (conceptDto.getCodeSystemId() != null) {
			systemValue = conceptDto.getCodeSystemId().getCodeSystem();
		}
		coding.setCode(codeValue);
		coding.setDisplay(displayValue);
		coding.setSystem(systemValue);

		return coding;
	}

	protected Extension generateExtension(Extension extension, String url, Type value) {
		extension.setUrl(url);
		extension.setValue(value);
		return extension;
	}

	protected Extension generateNewExtension(String url, Type value) {
		Extension extension = new Extension();
		return generateExtension(extension, url, value);
	}

	protected Address generateAddress(LocationDto location) {
		String countryName = "South Korea";
		Address address = new Address();
		String city = location.getCity();
		String line = location.getAddressLine();
		String state = location.getState();
		String zip = location.getZip();

		if (city != null) {
			address.setCity(location.getCity());
		}
		if (line != null) {
			address.addLine(location.getAddressLine());
		}
		if (state != null) {
			address.setState(location.getState());
		}
		if (zip != null) {
			address.setPostalCode(location.getZip());
		}

		address.setCountry(countryName);
		return address;
	}

	protected DateType generateDate(Date date) {
		DateType dateType = new DateType();
		dateType.setValue(date);
		return dateType;
	}

	protected DateTimeType generateDateTime(Date date) {
		DateTimeType dateTimeType = new DateTimeType();
		dateTimeType.setValue(date);
		return dateTimeType;
	}

	protected Quantity generateQuantity(ObservationDto obsDto) {
		Quantity quantity = new Quantity();
		if (obsDto.getValueAsNumber() != null) {
			quantity.setValue(obsDto.getValueAsNumber());
			if (obsDto.getValueUnitConcept() != null) {
				quantity.setUnit(obsDto.getValueUnitConcept().getConceptCode());
				quantity.setSystem(obsDto.getValueUnitConcept().getCodeSystemId().getCodeSystem());
			}
		}
		return quantity;
	}

	protected Quantity generateQuantityFromConcept(ConceptDto conceptDto) {
		Quantity quantity = new Quantity();
		return quantity;
	}

	protected StringType generateStringType(String string) {
		StringType stringType = new StringType();
		stringType.setValue(string);
		return stringType;
	}

	protected Type generateValue(String conceptCode, ObservationDto dto) {
		switch (conceptCode) {
		case ValueSet.ObservationCodeType.BLOOD_PRESSURE_CODE:
		case ValueSet.ObservationCodeType.BLOOD_GLUCOSE_CODE:
		case ValueSet.ObservationCodeType.EXERCISE_DURATION_CODE:
		case ValueSet.ObservationCodeType.BODY_WEIGHT_CODE:
		case ValueSet.ObservationCodeType.LENGTH_BODY_CODE:
		case ValueSet.ObservationCodeType.HEART_BEAT_CODE:
		case ValueSet.ObservationCodeType.NUMBER_STEP_PEDOMETER_CODE:
		case ValueSet.ObservationCodeType.DURATION_SLEEP_CODE:
		case ValueSet.ObservationCodeType.SATISFACTION_SLEEP_CODE:
		case ValueSet.ObservationCodeType.LEVEL_STRESS_CODE:
			return generateQuantity(dto);
		case ValueSet.ObservationCodeType.EXERCISE_INTENSITY_CODE:
			return generateCodeableConcept(dto.getValueAsConcept(), null);
		default:
			return null;
		}
	}

	protected Meta generateNewMeta(String profileUrl, Coding tagCoding) {
		Meta meta = new Meta();
		meta.addProfile(profileUrl);
		meta.addTag(tagCoding);
		meta.setLastUpdated(new Date());
		return meta;
	}
	
	protected Extension generateDeviceUseStartExtension(Date date) {
		Extension extension = new Extension();
		extension.setUrl(String.format("%s%s", profileUrl, "pghd-cdm-deviceusestarttiming"));
		extension.setValue(new DateType(date));
		return extension;
	}
	
	protected Extension generateDeviceUseEndExtension(Date date) {
		Extension extension = new Extension();
		extension.setUrl(String.format("%s%s", profileUrl, "pghd-cdm-deviceuseendtiming"));
		extension.setValue(new DateType(date));
		return extension;
	}

}
