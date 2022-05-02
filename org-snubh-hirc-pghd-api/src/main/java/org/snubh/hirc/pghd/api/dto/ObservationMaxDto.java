package org.snubh.hirc.pghd.api.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "Observation")
//@Table(name = "OBSERVATION", schema = "cdm_2019")
public class ObservationMaxDto implements Serializable {

	private static final long serialVersionUID = -8419950065758397416L;

	@Id
	@Column(name = "OBSERVATION_ID")
	private Long identifier;

	@Column(name = "OBSERVATION_CONCEPT_ID")
	private Long obsConceptId;

	@Column(name = "OBSERVATION_SOURCE_VALUE")
	private String conceptText;

	@Column(name = "PERSON_ID")
	private Long subject;

	@Column(name = "OBSERVATION_DATE")
	private Date effectiveDate;

	@Column(name = "OBSERVATION_DATETIME")
	private Date effectiveDateTime;

	@Column(name = "VALUE_AS_NUMBER")
	private Float valueAsNumber;

	@OneToMany
	@JoinColumns({ @JoinColumn(referencedColumnName = "PERSON_ID", name = "PERSON_ID"), @JoinColumn(referencedColumnName = "OBSERVATION_CONCEPT_ID", name = "OBSERVATION_CONCEPT_ID"),
			@JoinColumn(referencedColumnName = "OBSERVATION_DATE", name = "OBSERVATION_DATE") })
	private List<ObservationDto> list;

	@OneToOne
	@JoinColumn(name = "OBSERVATION_EVENT_ID")
	private DeviceExposureDto deviceExposure;

	public Long getIdentifier() {
		return identifier;
	}

	public String getIdentifierAsString() {
		return Long.toString(identifier);
	}

	public Long getObsConceptId() {
		return obsConceptId;
	}

	public String getConceptText() {
		return conceptText;
	}

	public Long getSubject() {
		return subject;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public Date getEffectiveDateTime() {
		return effectiveDateTime;
	}

	public Float getValueAsNumber() {
		return valueAsNumber;
	}

	public List<ObservationDto> getList() {
		return list;
	}

	public DeviceExposureDto getDeviceExposure() {
		return deviceExposure;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}