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
@Table(name = "Person")
//@Table(name = "PERSON", schema="cdm_2019")
public class PersonSummaryDto {

	@Id
	@Column(name = "PERSON_ID")
	private Long identifier;

	@Column(name = "BIRTH_DATETIME")
	private Date birthDate;

	@Column(name = "GENDER_CONCEPT_ID")
	private Long genderConceptId;

	@OneToOne
	@JoinColumn(name = "RACE_CONCEPT_ID")
	private ConceptDto raceConcept;

	public ConceptDto getRaceConcept() {
		return raceConcept;
	}

	public Long getIdentifier() {
		return identifier;
	}

	public String getIdentifierAsString() {
		return Long.toString(identifier);
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public Long getGenderConceptId() {
		return genderConceptId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}