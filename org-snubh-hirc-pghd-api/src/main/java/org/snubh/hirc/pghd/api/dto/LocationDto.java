package org.snubh.hirc.pghd.api.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "LOCATION")
//@Table(name = "LOCATION", schema="cdm_2019")
public class LocationDto {

	@Id
	@Column(name = "LOCATION_ID")
	private Long locationId;

	@Column(name = "ADDRESS_1")
	private String addressLine;

	@Column(name = "CITY")
	private String city;

	@Column(name = "STATE")
	private String state;

	@Column(name = "ZIP")
	private String zip;

	public Long getLocationId() {
		return locationId;
	}

	public String getAddressLine() {
		return addressLine;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getZip() {
		return zip;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}