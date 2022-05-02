package org.snubh.hirc.pghd.api.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "CARE_SITE")
//@Table(name = "CARE_SITE", schema="cdm_2019")
public class CareSiteDto {

	@Id
	@Column(name = "CARE_SITE_ID")
	private Long identifier;

	@Column(name = "CARE_SITE_NAME")
	private String name;

	@OneToOne
	@JoinColumn(name = "PLACE_OF_SERVICE_CONCEPT_ID")
	private ConceptDto typeConcept;

	@OneToOne
	@JoinColumn(name = "LOCATION_ID")
	private LocationDto address;

	public Long getIdentifier() {
		return identifier;
	}

	public String getIdentifierAsString() {
		return Long.toString(identifier);
	}

	public String getName() {
		return name;
	}

	public ConceptDto getTypeConcept() {
		return typeConcept;
	}

	public LocationDto getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}