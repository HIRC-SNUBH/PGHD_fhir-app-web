package org.snubh.hirc.pghd.api.dto;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "DEATH")
//@Table(name = "DEATH", schema="cdm_2019")
public class DeathDto {

	@Id
	@Column(name = "PERSON_ID")
	private Long identifier;

	@Column(name = "DEATH_DATE")
	private Date deathDate;

	public Long getIdentifier() {
		return identifier;
	}

	public Date getDeathDate() {
		return deathDate;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

}