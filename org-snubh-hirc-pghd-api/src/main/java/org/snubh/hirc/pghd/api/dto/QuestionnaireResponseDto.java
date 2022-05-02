package org.snubh.hirc.pghd.api.dto;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "SURVEY_CONDUCT")
//@Table(name = "SURVEY_CONDUCT", schema="cdm_2019")
public class QuestionnaireResponseDto {

	@Id
	@Column(name = "SURVEY_CONDUCT_ID")
	private Long identifier;

	@OneToOne
	@JoinColumn(name = "PERSON_ID")
	private PersonSummaryDto subject;

	@Column(name = "SURVEY_CONCEPT_ID")
	private Long surveyConcept;

	@Column(name = "SURVEY_START_DATE")
	private Date surveyStartDate;

	@Column(name = "SURVEY_END_DATE")
	private Date surveyEndDate;

	@Column(name = "RESPONDENT_TYPE_CONCEPT_ID")
	private Long respondentTypeConcept;

	@Column(name = "COLLECTION_METHOD_CONCEPT_ID")
	private Long collectionMethodConcept;

	@Column(name = "TIMING_SOURCE_VALUE")
	private String timingSourceValue;

	@Column(name = "COLLECTION_METHOD_SOURCE_VALUE")
	private String collectionMethodSourceValue;

	@Column(name = "SURVEY_SOURCE_VALUE")
	private String surveySourceValue;

	@Column(name = "VISIT_OCCURRENCE_ID")
	private Long encounter;

	@OneToMany
	@JoinColumn(name = "OBSERVATION_EVENT_ID")
	@Where(clause = "OBS_EVENT_FIELD_CONCEPT_ID = 1147832")
	private List<ObservationQuestionnaireDto> observations;

	public Long getIdentifier() {
		return identifier;
	}

	public String getIdentifierAsString() {
		return Long.toString(identifier);
	}

	public PersonSummaryDto getSubject() {
		return subject;
	}

	public Long getSurveyConcept() {
		return surveyConcept;
	}

	public Date getSurveyStartDate() {
		return surveyStartDate;
	}

	public Date getSurveyEndDate() {
		return surveyEndDate;
	}

	public Long getRespondentTypeConcept() {
		return respondentTypeConcept;
	}

	public Long getCollectionMethodConcept() {
		return collectionMethodConcept;
	}

	public String getTimingSourceValue() {
		return timingSourceValue;
	}

	public String getCollectionMethodSourceValue() {
		return collectionMethodSourceValue;
	}

	public String getSurveySourceValue() {
		return surveySourceValue;
	}

	public Long getEncounter() {
		return encounter;
	}

	public List<ObservationQuestionnaireDto> getObservations() {
		return observations;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}