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
public class ObservationQuestionnaireDto {

	@Id
	@Column(name = "OBSERVATION_ID")
	private Long identifier;

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne
	@JoinColumn(name = "OBSERVATION_CONCEPT_ID", nullable = true)
	private ConceptDto obvConcept;

	@Column(name = "VISIT_OCCURRENCE_ID")
	private Long encounter;

	@Column(name = "OBSERVATION_SOURCE_VALUE")
	private String conceptText;

	@Column(name = "PERSON_ID")
	private Long subject;

	@Column(name = "OBSERVATION_DATE")
	private Date effectiveDate;

	@Column(name = "OBSERVATION_DATETIME")
	private Date effectiveDateTime;

	@Column(name = "OBSERVATION_TYPE_CONCEPT_ID")
	private Long obvCategory;

	@Column(name = "VALUE_AS_NUMBER")
	private Float valueAsNumber;

	@Column(name = "VALUE_AS_STRING")
	private String valueAsString;

	@OneToOne
	@JoinColumn(name = "UNIT_CONCEPT_ID")
	private ConceptDto valueUnitConcept;

	@OneToOne
	@JoinColumn(name = "VALUE_AS_CONCEPT_ID")
	private ConceptDto valueAsConcept;

	@Column(name = "OBSERVATION_EVENT_ID")
	private Long questionnaireResponse;

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

	public Long getEncounter() {
		return encounter;
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

	public Long getObvCategory() {
		return obvCategory;
	}

	public Float getValueAsNumber() {
		return valueAsNumber;
	}

	public String getValueAsString() {
		return valueAsString;
	}

	public ConceptDto getValueUnitConcept() {
		return valueUnitConcept;
	}

	public ConceptDto getValueAsConcept() {
		return valueAsConcept;
	}

	public Long getQuestionnaireResponse() {
		return questionnaireResponse;
	}

	public Long getQuestionnaireConcept() {
		return questionnaireConcept;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}