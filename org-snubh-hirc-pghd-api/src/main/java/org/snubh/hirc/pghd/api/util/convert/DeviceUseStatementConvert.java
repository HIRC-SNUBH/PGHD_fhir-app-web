package org.snubh.hirc.pghd.api.util.convert;

import java.net.UnknownHostException;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Device;
import org.hl7.fhir.r4.model.DeviceUseStatement;
import org.hl7.fhir.r4.model.DeviceUseStatement.DeviceUseStatementStatus;
import org.hl7.fhir.r4.model.Enumerations.ResourceType;
import org.hl7.fhir.r4.model.Period;
import org.hl7.fhir.r4.model.Resource;
import org.snubh.hirc.pghd.api.dao.i.BaseConvert;
import org.snubh.hirc.pghd.api.dto.ConceptDto;
import org.snubh.hirc.pghd.api.dto.DeviceExposureDto;
import org.springframework.stereotype.Component;

@Component
public class DeviceUseStatementConvert extends BaseConvert<DeviceExposureDto, DeviceUseStatement> {

	public DeviceUseStatementConvert() throws UnknownHostException {
		super();
	}

	@Override
	public DeviceUseStatement convert(DeviceExposureDto dto) {

		if (dto == null)
			return null;

		DeviceUseStatement deviceUseStatement = new DeviceUseStatement();
		deviceUseStatement.setId(dto.getIdentifierAsString());
		deviceUseStatement.setStatus(DeviceUseStatementStatus.ACTIVE);
		deviceUseStatement.setSubject(generateReference(ResourceType.PATIENT, dto.getPatient()));
		deviceUseStatement.addContained(generateContainedDeviceResource(dto.getDeviceType()));
		deviceUseStatement.setDevice(generateConatainedResourceReference(dto.getDeviceType().getConceptId()));
		deviceUseStatement.setTiming(generatePeriod(new Period(), dto.getDeviceExposureStartDate(), dto.getDeviceExposureEndDate()));
		String sourceTypeExtensionURL = String.format("%spghd-cdm-devicetype", profileUrl);
		deviceUseStatement.addExtension(generateNewExtension(sourceTypeExtensionURL, generateSourceTypeCoding(dto.getSourceType())));
		String metaURL = String.format("%spghd-cdm-deviceusestatement", profileUrl);
		deviceUseStatement.setMeta(generateNewMeta(metaURL, null));

		return deviceUseStatement;

	}

	private Resource generateContainedDeviceResource(ConceptDto conceptDto) {
		Device device = new Device();
		device.setType(generateCodeableConcept(conceptDto));
		device.setId(conceptDto.getConceptIdAsString());
		return device;
	}

	private Coding generateSourceTypeCoding(Long sourceTypeConcept) {
		Coding coding = new Coding();
		if (sourceTypeConcept != null) {
			coding = new Coding("https://athena.ohdsi.org/", "OMOP4822236", "Patient reported device");
		}
		return coding;
	}
}