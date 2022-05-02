package org.snubh.hirc.pghd.api.util.convert;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations.PublicationStatus;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemType;
import org.snubh.hirc.pghd.api.dao.i.BaseConvert;
import org.snubh.hirc.pghd.api.dto.ObservationQuestionnaireDto;
import org.snubh.hirc.pghd.api.dto.QuestionnaireResponseDto;
import org.springframework.stereotype.Component;

@Component
public class QuestionnaireConvert extends BaseConvert<QuestionnaireResponseDto, Questionnaire> {

	public QuestionnaireConvert() throws UnknownHostException {
		super();
	}

	@Override
	public Questionnaire convert(QuestionnaireResponseDto dto) {

		if (dto == null)
			return null;

		Questionnaire questionnaire = new Questionnaire();
		questionnaire.setId(dto.getIdentifierAsString());
		questionnaire.setStatus(PublicationStatus.ACTIVE);
		questionnaire.setTitle(dto.getSurveySourceValue());
		questionnaire.addCode(generateSurveyCoding(dto.getSurveyConcept()));
		questionnaire.setItem(generateItemComponent(dto.getObservations()));
		questionnaire.setMeta(generateNewMeta(String.format("%spghd-cdm-questionnaire", profileUrl), null));
		return questionnaire;

	}

	private List<QuestionnaireItemComponent> generateItemComponent(List<ObservationQuestionnaireDto> obsList) {
		List<QuestionnaireItemComponent> qItemComponent = new ArrayList<QuestionnaireItemComponent>();
		for (ObservationQuestionnaireDto dto : obsList) {
			if (dto.getObvConcept() != null) {
				QuestionnaireItemComponent component = new QuestionnaireItemComponent();
				component.setLinkId(dto.getObvConcept().getConceptIdAsString());
				component.addCode(generateCoding(dto.getObvConcept()));
				component.setText(dto.getConceptText());
				component.setType(QuestionnaireItemType.OPENCHOICE);
				qItemComponent.add(component);
			}
		}
		return qItemComponent;
	}

	private Coding generateSurveyCoding(Long surveyCoding) {
		Coding coding = new Coding();
		if (surveyCoding != null && surveyCoding == 2000000348L) {
			coding.setCode("SNUBH0050");
			coding.setDisplay("Health Questionnaire");
			coding.setSystem("https://www.cdm.snubh.org/Atlas");
		}
		return coding;
	}

}
