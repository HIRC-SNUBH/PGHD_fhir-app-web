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
public class PersonDto {

	@Id
	@Column(name = "PERSON_ID")
	private Long identifier;

	@Column(name = "CARE_SITE_ID")
	private Long organizationId;

	@Column(name = "GENDER_CONCEPT_ID")
	private Long genderConcept;

	@Column(name = "RACE_CONCEPT_ID")
	private Long raceConcept;

	@OneToOne
	@JoinColumn(name = "LOCATION_ID")
	private LocationDto address;

	@Column(name = "BIRTH_DATETIME")
	private Date birthDate;

	@OneToOne
	@JoinColumn(name = "PERSON_ID")
	private DeathDto death;

	public Long getIdentifier() {
		return identifier;
	}

	public String getIdentifierAsString() {
		return Long.toString(identifier);
	}

	public Long getOrganizationId() {
		return organizationId;
	}

	public Long getGenderConcept() {
		return genderConcept;
	}

	public Long getRaceConcept() {
		return raceConcept;
	}

	public LocationDto getAddress() {
		return address;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public DeathDto getDeath() {
		return death;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}