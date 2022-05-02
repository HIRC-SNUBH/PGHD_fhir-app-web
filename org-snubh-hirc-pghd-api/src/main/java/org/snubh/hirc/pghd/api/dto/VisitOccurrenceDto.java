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
@Table(name = "Visit_occurrence")
//@Table(name = "VISIT_OCCURRENCE", schema="cdm_2019")
public class VisitOccurrenceDto {

	@Id
	@Column(name = "VISIT_OCCURRENCE_ID")
	private Long identifier;

	@Column(name = "CARE_SITE_ID")
	private Long serviceProvider;

	@OneToOne
	@JoinColumn(name = "ADMITTING_SOURCE_CONCEPT_ID")
	private ConceptDto hospitalizationAdmitConcept;

	@OneToOne
	@JoinColumn(name = "DISCHARGE_TO_CONCEPT_ID")
	private ConceptDto hospitalizationDischargeConcept;

	@Column(name = "PERSON_ID")
	private Long subject;

	@Column(name = "VISIT_CONCEPT_ID")
	private Long visitClass;

	@Column(name = "VISIT_START_DATE")
	private Date periodStart;

	@Column(name = "VISIT_END_DATE")
	private Date periodEnd;

	@Column(name = "VISIT_TYPE_CONCEPT_ID")
	private Long typeConcept;

	public Long getIdentifier() {
		return identifier;
	}

	public String getIdentifierAsString() {
		return Long.toString(identifier);
	}

	public Long getServiceProvider() {
		return serviceProvider;
	}

	public ConceptDto getHospitalizationAdmitConcept() {
		return hospitalizationAdmitConcept;
	}

	public ConceptDto getHospitalizationDischargeConcept() {
		return hospitalizationDischargeConcept;
	}

	public Long getSubject() {
		return subject;
	}

	public Long getVisitClass() {
		return visitClass;
	}

	public Date getPeriodStart() {
		return periodStart;
	}

	public Date getPeriodEnd() {
		return periodEnd;
	}

	public Long getTypeConcept() {
		return typeConcept;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}