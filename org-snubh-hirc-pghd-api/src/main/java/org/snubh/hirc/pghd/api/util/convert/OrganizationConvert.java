package org.snubh.hirc.pghd.api.util.convert;

import java.net.UnknownHostException;

import org.hl7.fhir.r4.model.Organization;
import org.snubh.hirc.pghd.api.dao.i.BaseConvert;
import org.snubh.hirc.pghd.api.dto.CareSiteDto;
import org.snubh.hirc.pghd.api.dto.LocationDto;
import org.springframework.stereotype.Component;

@Component
public class OrganizationConvert extends BaseConvert<CareSiteDto, Organization> {

	public OrganizationConvert() throws UnknownHostException {
		super();
	}

	@Override
	public Organization convert(CareSiteDto dto) {

		if (dto == null) {
			return null;
		}

		Organization organization = new Organization();
		organization.setId(dto.getIdentifierAsString());

		organization.setName(dto.getName());
		organization.addType(generateCodeableConcept(dto.getTypeConcept()));

		LocationDto locationDto = dto.getAddress();
		if (locationDto != null) {
			organization.addAddress(generateAddress(locationDto));
		}
		organization.setMeta(generateNewMeta(String.format("%spghd-cdm-organization", profileUrl), null));
		return organization;
	}
}
