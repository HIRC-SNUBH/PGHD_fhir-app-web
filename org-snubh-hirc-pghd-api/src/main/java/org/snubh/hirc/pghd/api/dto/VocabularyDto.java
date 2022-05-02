package org.snubh.hirc.pghd.api.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "Vocabulary")
//@Table(name = "VOCABULARY", schema="cdm_voca")
public class VocabularyDto {

	@Id
	@Column(name = "VOCABULARY_ID")
	private String codeSystemId;

	@Column(name = "VOCABULARY_REFERENCE")
	private String codeSystem;

	@Column(name = "VOCABULARY_VERSION")
	private String codeSystemVersion;

	public String getCodeSystemId() {
		return codeSystemId;
	}

	public String getCodeSystem() {
		return codeSystem;
	}

	public String getCodeSystemVersion() {
		return codeSystemVersion;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}