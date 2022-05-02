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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Entity
@Table(name = "Observation")
//@Table(name = "OBSERVATION", schema = "cdm_2019")
public class ObservationDto {

	@Id
	@Column(name = "OBSERVATION_ID")
	private Long identifier;

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne
	@JoinColumn(name = "OBSERVATION_CONCEPT_ID", nullable = true)
	private ConceptDto obvConcept;

	@OneToOne
	@JoinColumn(name = "QUALIFIER_CONCEPT_ID")
	private ConceptDto componentConcept;

	@Column(name = "OBSERVATION_SOURCE_VALUE")
	private String conceptText;

	@OneToOne
	@JoinColumn(name = "PERSON_ID")
	private PersonSummaryDto subject;

	@Column(name = "OBSERVATION_DATE")
	private Date effectiveDate;

	@Column(name = "OBSERVATION_DATETIME")
	private Date effectiveDateTime;

	@OneToOne
	@JoinColumn(name = "OBSERVATION_TYPE_CONCEPT_ID")
	private ConceptDto obvCategory;

	@Column(name = "VALUE_AS_NUMBER")
	private Float valueAsNumber;

	@OneToOne
	@JoinColumn(name = "UNIT_CONCEPT_ID")
	private ConceptDto valueUnitConcept;

	@OneToOne
	@JoinColumn(name = "VALUE_AS_CONCEPT_ID")
	private ConceptDto valueAsConcept;

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne
	@JoinColumn(name = "OBSERVATION_EVENT_ID")
	private DeviceExposureDto deviceExposure;

	@Column(name = "OBS_EVENT_FIELD_CONCEPT_ID")
	private Long questionnaireConcept;

	public Long getIdentifier() {
		return identifier;
	}

	public String getIdentifierAsString() {
		return Long.toString(identifier);
	}

	public ConceptDto getObvConcept() {
		return obvConcept;
	}

	public ConceptDto getComponentConcept() {
		return componentConcept;
	}

	public String getConceptText() {
		return conceptText;
	}

	public PersonSummaryDto getSubject() {
		return subject;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public Date getEffectiveDateTime() {
		return effectiveDateTime;
	}

	public ConceptDto getObvCategory() {
		return obvCategory;
	}

	public Float getValueAsNumber() {
		return valueAsNumber;
	}

	public ConceptDto getValueUnitConcept() {
		return valueUnitConcept;
	}

	public ConceptDto getValueAsConcept() {
		return valueAsConcept;
	}

	public DeviceExposureDto getDeviceExposure() {
		return deviceExposure;
	}

	public Long getQuestionnaireConcept() {
		return questionnaireConcept;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}