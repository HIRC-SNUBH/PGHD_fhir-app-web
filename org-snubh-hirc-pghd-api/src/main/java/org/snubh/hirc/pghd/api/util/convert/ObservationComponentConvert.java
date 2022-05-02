package org.snubh.hirc.pghd.api.util.convert;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.Enumerations.ResourceType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Patient;
import org.snubh.hirc.pghd.api.dao.i.BaseConvert;
import org.snubh.hirc.pghd.api.dto.ConceptDto;
import org.snubh.hirc.pghd.api.dto.DeviceExposureDto;
import org.snubh.hirc.pghd.api.dto.ObservationDto;
import org.snubh.hirc.pghd.api.dto.ObservationMaxDto;
import org.snubh.hirc.pghd.api.dto.PersonSummaryDto;
import org.snubh.hirc.pghd.api.util.ValueSet;
import org.springframework.stereotype.Component;

@Component
public class ObservationComponentConvert extends BaseConvert<ObservationMaxDto, Observation> {

	public ObservationComponentConvert() throws UnknownHostException {
		super();
	}

	public Observation convert(ObservationMaxDto dto, String contained) {
		if (dto == null) {
			return null;
		}

		Observation observation = convert(dto);
		PersonSummaryDto patientSummaryDto = dto.getList().get(0).getSubject();
		if (patientSummaryDto != null) {
			if (contained.equals("both")) {
				observation.addContained(generatePerson(patientSummaryDto));
				observation.setSubject(generateConatainedResourceReference(patientSummaryDto.getIdentifier()));
			} else {
				observation.setSubject(generateReference(ResourceType.PATIENT, patientSummaryDto.getIdentifier()));
			}			
		}
		return observation;
	}

	@Override
	public Observation convert(ObservationMaxDto dto) {

		if (dto == null)
			return null;
		ConceptDto categoryDto = null;
		ConceptDto codingDto = null;
		String uniqueKey = null;

		Observation observation = new Observation();
		observation.setStatus(ObservationStatus.FINAL);
		observation.setEffective(generateDateTime(dto.getEffectiveDate()));
		if (dto.getList() != null) {
			for (ObservationDto obsDto : dto.getList()) {
				if (uniqueKey == null) {
					uniqueKey = generateUniqueResourceKey(obsDto.getSubject().getIdentifier(), obsDto.getObvConcept().getConceptId(), obsDto.getEffectiveDate());
				}
				if (categoryDto == null) {
					categoryDto = obsDto.getObvCategory();
				}
				if (codingDto == null) {
					codingDto = obsDto.getObvConcept();
				}
				observation.addComponent(generateObsComponent(obsDto));
			}
		}

		observation.setCode(generateCodeableConcept(codingDto));
		observation.addCategory(generateCodeableConcept(categoryDto));
		observation.setStatus(ObservationStatus.FINAL);
		observation.setId(uniqueKey);

		observation.setMeta(generateNewMeta(String.format("%spghd-cdm-observationcomponent", profileUrl), null));

		DeviceExposureDto deviceExposureDto = dto.getDeviceExposure();

		if(deviceExposureDto != null) {
			observation.addExtension(generateDeviceUseStartExtension(deviceExposureDto.getDeviceExposureStartDate()));
			observation.addExtension(generateDeviceUseEndExtension(deviceExposureDto.getDeviceExposureEndDate()));
			observation.addContained(generateDevice(deviceExposureDto));
			observation.setDevice(generateConatainedResourceReference(dto.getDeviceExposure().getIdentifier()));
		}

		return observation;
	}

	private String generateUniqueResourceKey(Long subject, Long obsConceptId, Date effectiveDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
		String effectiveDateString = sdf.format(effectiveDate);
		return String.format("%d-%d-%s", subject, obsConceptId, effectiveDateString);
	}

	private ObservationComponentComponent generateObsComponent(ObservationDto obsDto) {
		ObservationComponentComponent obsComponent = new ObservationComponentComponent();
		obsComponent.setCode(generateCodeableConcept(obsDto.getComponentConcept()));
		String codeConceptCode = obsDto.getObvConcept().getConceptCode();
		switch (codeConceptCode) {
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
			obsComponent.setValue(generateQuantity(obsDto));
			break;
		case ValueSet.ObservationCodeType.EXERCISE_INTENSITY_CODE:
			obsComponent.setValue(generateCodeableConcept(obsDto.getValueAsConcept()));
			break;
		default:
			break;
		}
		return obsComponent;
	}

	private Device generateDevice(DeviceExposureDto dto) {
		Device device = new Device();
		device.setId(dto.getIdentifierAsString());
		device.setType(generateCodeableConcept(dto.getDeviceType()));
		return device;
	}

	private Patient generatePerson(PersonSummaryDto dto) {
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
		return patient;
	}

}
