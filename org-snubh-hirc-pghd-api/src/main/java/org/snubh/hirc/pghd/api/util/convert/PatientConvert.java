package org.snubh.hirc.pghd.api.util.convert;

import java.net.UnknownHostException;

import org.hl7.fhir.r4.model.Enumerations.ResourceType;
import org.hl7.fhir.r4.model.Patient;
import org.snubh.hirc.pghd.api.dao.i.BaseConvert;
import org.snubh.hirc.pghd.api.dto.PersonDto;
import org.snubh.hirc.pghd.api.util.ValueSet;
import org.springframework.stereotype.Component;

@Component
public class PatientConvert extends BaseConvert<PersonDto, Patient> {

	public PatientConvert() throws UnknownHostException {
		super();
	}

	@Override
	public Patient convert(PersonDto dto) {

		if (dto == null)
			return null;

		Patient patient = new Patient();
		patient.setId(dto.getIdentifierAsString());
		patient.addIdentifier(generateIdentifier(dto.getIdentifierAsString()));
		patient.setBirthDate(dto.getBirthDate());

		Long genderCode = dto.getGenderConcept();
		if (genderCode != null) {
			if (genderCode == ValueSet.Gender.M)
				patient.setGender(org.hl7.fhir.r4.model.Enumerations.AdministrativeGender.MALE);
			if (genderCode == ValueSet.Gender.F)
				patient.setGender(org.hl7.fhir.r4.model.Enumerations.AdministrativeGender.FEMALE);
		}

		Long organizationId = dto.getOrganizationId();
		patient.setManagingOrganization(generateReference(ResourceType.ORGANIZATION.toCode(), organizationId));

		patient.addAddress(generateAddress(dto.getAddress()));

		if (dto.getDeath() != null)
			patient.setDeceased(generateDateTime(dto.getDeath().getDeathDate()));

		patient.addExtension(generateRaceExtension(dto.getRaceConcept()));

		patient.setActive(true);
		patient.setMeta(generateNewMeta(String.format("%spghd-cdm-patient", profileUrl), null));
		return patient;
	}	

}
