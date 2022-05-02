package org.snubh.hirc.pghd.api.dto;

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
@Table(name = "Concept")
//@Table(name = "CONCEPT", schema="cdm_voca")
public class ConceptDto {

	@Id
	@Column(name = "CONCEPT_ID")
	private Long conceptId;

	@Column(name = "CONCEPT_NAME")
	private String conceptName;

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne
	@JoinColumn(name = "VOCABULARY_ID")
	private VocabularyDto codeSystemId;

	@Column(name = "CONCEPT_CODE")
	private String conceptCode;

	public Long getConceptId() {
		return conceptId;
	}

	public String getConceptIdAsString() {
		return Long.toString(conceptId);
	}

	public String getConceptName() {
		return conceptName;
	}

	public VocabularyDto getCodeSystemId() {
		return codeSystemId;
	}

	public String getConceptCode() {
		return conceptCode;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}