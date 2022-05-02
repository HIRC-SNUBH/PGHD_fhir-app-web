package org.snubh.hirc.pghd.api.util.convert;

import java.net.UnknownHostException;

import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.Enumerations.ResourceType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.snubh.hirc.pghd.api.dao.i.BaseConvert;
import org.snubh.hirc.pghd.api.dto.DeviceExposureDto;
import org.snubh.hirc.pghd.api.dto.ObservationDto;
import org.snubh.hirc.pghd.api.dto.PersonSummaryDto;
import org.snubh.hirc.pghd.api.util.ValueSet;
import org.springframework.stereotype.Component;

@Component
public class ObservationConvert extends BaseConvert<ObservationDto, Observation> {

	public ObservationConvert() throws UnknownHostException {
		super();
	}

	public Observation convert(ObservationDto dto, String contained) {

		if (dto == null) {
			return null;
		}

		if (contained.equals("both"))
			containedFlag = true;
		else
			containedFlag = false;
		Observation observation = convert(dto);

		return observation;
	}

	@Override
	public Observation convert(ObservationDto dto) {

		if (dto == null) {
			return null;
		}

		Observation observation = new Observation();
		observation.setId(dto.getIdentifierAsString());
		DateTimeType effective = new DateTimeType();
		if (dto.getEffectiveDate() != null) {
			effective.setValue(dto.getEffectiveDate());
		} else {
			if (dto.getEffectiveDateTime() != null) {
				effective.setValue(dto.getEffectiveDateTime());
			} else {
				effective.setValue(dto.getEffectiveDate());
			}
		}

		observation.setEffective(effective);
		observation.addCategory(generateCodeableConcept(dto.getObvCategory()));
		observation.setStatus(ObservationStatus.FINAL);
		observation.setCode(generateCodeableConcept(dto.getObvConcept(), dto.getConceptText()));

		if (dto.getObvConcept() != null) {
			String code = dto.getObvConcept().getConceptCode();
			switch (code) {
			case ValueSet.ObservationCodeType.BODY_WEIGHT_CODE:
			case ValueSet.ObservationCodeType.LENGTH_BODY_CODE:
			case ValueSet.ObservationCodeType.HEART_BEAT_CODE:
			case ValueSet.ObservationCodeType.NUMBER_STEP_PEDOMETER_CODE:
			case ValueSet.ObservationCodeType.DURATION_SLEEP_CODE:
			case ValueSet.ObservationCodeType.SATISFACTION_SLEEP_CODE:
			case ValueSet.ObservationCodeType.LEVEL_STRESS_CODE:
			case ValueSet.ObservationCodeType.BLOOD_GLUCOSE_CODE:
			case ValueSet.ObservationCodeType.BLOOD_PRESSURE_CODE:
			case ValueSet.ObservationCodeType.EXERCISE_DURATION_CODE:
				observation.setValue(generateQuantity(dto));
				break;
			case ValueSet.ObservationCodeType.EXERCISE_INTENSITY_CODE:
				observation.setValue(generateCodeableConcept(dto.getValueAsConcept()));
			default:
				break;
			}
		}

		PersonSummaryDto patientSummaryDto = dto.getSubject();
		if (patientSummaryDto != null) {
			if (containedFlag)
				observation.addContained(generateContainedPerson(patientSummaryDto));
			observation.setSubject(generateReference(ResourceType.PATIENT, patientSummaryDto.getIdentifier()));
		}
		DeviceExposureDto deviceExposureDto = dto.getDeviceExposure();

		if(deviceExposureDto != null) {
			observation.addExtension(generateDeviceUseStartExtension(deviceExposureDto.getDeviceExposureStartDate()));
			observation.addExtension(generateDeviceUseEndExtension(deviceExposureDto.getDeviceExposureEndDate()));
			if(containedFlag)
				observation.addContained(generateDevice(deviceExposureDto));
			observation.setDevice(generateReference(ResourceType.DEVICE, dto.getDeviceExposure().getIdentifier()));
		}

		observation.setMeta(generateNewMeta(String.format("%spghd-cdm-observation", profileUrl), null));

		return observation;

	}

	private Device generateDevice(DeviceExposureDto dto) {
		Device device = new Device();
		device.setId(dto.getIdentifierAsString());
		device.setType(generateCodeableConcept(dto.getDeviceType()));
		return device;
	}

}
