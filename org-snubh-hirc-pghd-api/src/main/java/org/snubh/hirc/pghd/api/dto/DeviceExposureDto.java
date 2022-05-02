package org.snubh.hirc.pghd.api.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "Device_exposure")
//@Table(name = "DEVICE_EXPOSURE", schema="cdm_2019")
public class DeviceExposureDto {

	@Id
	@Column(name = "DEVICE_EXPOSURE_ID")
	private Long identifier;

	@Column(name = "PERSON_ID")
	private Long patient;

	@OneToOne
	@JoinColumn(name = "DEVICE_CONCEPT_ID")
	private ConceptDto deviceType;

	@Column(name = "DEVICE_EXPOSURE_START_DATE")
	private Date deviceExposureStartDate;

	@Column(name = "DEVICE_EXPOSURE_END_DATE")
	private Date deviceExposureEndDate;

	@Column(name = "DEVICE_TYPE_CONCEPT_ID")
	private Long sourceType;

	public Long getIdentifier() {
		return identifier;
	}

	public String getIdentifierAsString() {
		return Long.toString(identifier);
	}

	public Long getPatient() {
		return patient;
	}

	public ConceptDto getDeviceType() {
		return deviceType;
	}

	public Date getDeviceExposureStartDate() {
		return deviceExposureStartDate;
	}

	public Date getDeviceExposureEndDate() {
		return deviceExposureEndDate;
	}

	public Long getSourceType() {
		return sourceType;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}