package org.snubh.hirc.pghd.api.util.convert;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Enumerations.ResourceType;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent;
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent;
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.hl7.fhir.r4.model.StringType;
import org.snubh.hirc.pghd.api.dao.i.BaseConvert;
import org.snubh.hirc.pghd.api.dto.ObservationQuestionnaireDto;
import org.snubh.hirc.pghd.api.dto.PersonSummaryDto;
import org.snubh.hirc.pghd.api.dto.QuestionnaireResponseDto;
import org.snubh.hirc.pghd.api.util.ValueSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QuestionnaireResponseConvert extends BaseConvert<QuestionnaireResponseDto, QuestionnaireResponse> {
	
	private QuestionnaireConvert questionnaireConvert;
	
	@Autowired
	public QuestionnaireResponseConvert(QuestionnaireConvert questionnaireConvert) throws UnknownHostException {
		super();
		this.questionnaireConvert = questionnaireConvert;
	}

	public QuestionnaireResponseConvert() throws UnknownHostException {
		super();
	}
	 	 
	public List<QuestionnaireResponse> convertList(List<QuestionnaireResponseDto> dtoList, String contained) {
		List<QuestionnaireResponse> list = new ArrayList<>();

		if (dtoList != null && dtoList.size() > 0) {
			for (QuestionnaireResponseDto dto : dtoList) {
				QuestionnaireResponse resource = (QuestionnaireResponse) convert(dto, contained);
				if (resource != null) {
					list.add(resource);
				}
			}
		}

		return list;
	}

	public QuestionnaireResponse convert(QuestionnaireResponseDto dto, String contained) {

		if (dto == null) {
			return null;
		}

		if (contained.equals("both"))
			containedFlag = true;
		else
			containedFlag = false;
		QuestionnaireResponse questionnaireResponse = convert(dto);

		return questionnaireResponse;
	}

	@Override
	public QuestionnaireResponse convert(QuestionnaireResponseDto dto) {

		if (dto == null)
			return null;

		String respondentTypeExtensionURL = String.format("%spghd-cdm-respondenttype", profileUrl);
		String collectionMethodExtensionURL = String.format("%spghd-cdm-collectionmethod", profileUrl);
		String timingSourceExtensionURL = String.format("%spghd-cdm-timingsource", profileUrl);
		String collectionMethodSourceExtensionURL = String.format("%spghd-cdm-collectionmethodsource", profileUrl);
		QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();
		questionnaireResponse.setId(dto.getIdentifierAsString());
		questionnaireResponse.setAuthored(dto.getSurveyEndDate());
		questionnaireResponse.setStatus(QuestionnaireResponseStatus.COMPLETED);
		questionnaireResponse.addExtension(respondentTypeExtensionURL, generateRespondentTypeCoding(dto.getRespondentTypeConcept()));
		questionnaireResponse.addExtension(collectionMethodExtensionURL, generateCollectionMethodCoding(dto.getCollectionMethodConcept()));
		questionnaireResponse.addExtension(timingSourceExtensionURL, generateStringType(dto.getTimingSourceValue()));
		questionnaireResponse.addExtension(collectionMethodSourceExtensionURL, generateStringType(dto.getCollectionMethodSourceValue()));
		questionnaireResponse.setEncounter(generateReference(ResourceType.ENCOUNTER, dto.getEncounter()));
		questionnaireResponse.setQuestionnaire(String.format("#%d", dto.getIdentifier()));
		questionnaireResponse.addContained(questionnaireConvert.convert(dto));
		questionnaireResponse.setItem(generateResponseItemComponent(dto.getObservations()));

		PersonSummaryDto patientSummaryDto = dto.getSubject();
		if (patientSummaryDto != null) {
			if (containedFlag)
				questionnaireResponse.addContained(generateContainedPerson(patientSummaryDto));
			questionnaireResponse.setSubject(generateReference(ResourceType.PATIENT, dto.getSubject().getIdentifier()));
		}

		String metaURL = String.format("%spghd-cdm-questionnaireresponse", profileUrl);
		questionnaireResponse.setMeta(generateNewMeta(metaURL, null));
		return questionnaireResponse;

	}

	private List<QuestionnaireResponseItemComponent> generateResponseItemComponent(List<ObservationQuestionnaireDto> obsList) {
		List<QuestionnaireResponseItemComponent> qrItemComponent = new ArrayList<QuestionnaireResponseItemComponent>();
		for (ObservationQuestionnaireDto dto : obsList) {
			if (dto.getObvConcept() != null && dto.getQuestionnaireConcept() != null) {
				QuestionnaireResponseItemComponent component = new QuestionnaireResponseItemComponent();
				String itemCode = dto.getObvConcept().getConceptIdAsString();
				component.setLinkId(itemCode);
				QuestionnaireResponseItemAnswerComponent answerComponent = new QuestionnaireResponseItemAnswerComponent();

				switch (itemCode) {
				case ValueSet.QuestionnaireResponseQuantityCode.CIGARRETE_PACK_YEARS:
				case ValueSet.QuestionnaireResponseQuantityCode.DRINK_PER_WEEK_ID:
				case ValueSet.QuestionnaireResponseQuantityCode.HEAVY_EXCERSIZE:
				case ValueSet.QuestionnaireResponseQuantityCode.HEAVY_EXCERSIZE_SPENT:
				case ValueSet.QuestionnaireResponseQuantityCode.LIGHT_WALK:
				case ValueSet.QuestionnaireResponseQuantityCode.LIGHT_WALK_SPENT:
				case ValueSet.QuestionnaireResponseQuantityCode.MODERATE_EXCERSIZE:
				case ValueSet.QuestionnaireResponseQuantityCode.MODERATE_EXCERSIZE_SPENT:
				case ValueSet.QuestionnaireResponseQuantityCode.PROFIT_ID:
				case ValueSet.QuestionnaireResponseQuantityCode.SITTING:
				case ValueSet.QuestionnaireResponseQuantityCode.SLEPPING_TIME:
				case ValueSet.QuestionnaireResponseQuantityCode.SMOKING_AGE:
				case ValueSet.QuestionnaireResponseQuantityCode.TOBACCO_AVERAGE_ID:
				case ValueSet.QuestionnaireResponseQuantityCode.TOBACCO_NUM_PER_DAY_ID:
				case ValueSet.QuestionnaireResponseQuantityCode.TOBACCO_PER_DAY_ID:
					Quantity valueType = new Quantity();
					if (dto.getValueAsNumber() != null)
						valueType.setValue(dto.getValueAsNumber());
					if (dto.getValueUnitConcept() != null)
						valueType.setUnit(dto.getValueUnitConcept().getConceptCode());
					answerComponent.setValue(valueType);
					break;
				default:
					Coding valueCode = new Coding();
					if (dto.getValueAsConcept() != null)
						valueCode = generateCoding(dto.getValueAsConcept());
					answerComponent.setValue(valueCode);
					break;
				}

				component.addAnswer(answerComponent);
				if (!component.hasAnswer()) {
					QuestionnaireResponseItemAnswerComponent answerComponent2 = new QuestionnaireResponseItemAnswerComponent();
					StringType valueType2 = new StringType();
					valueType2.setValue(dto.getValueAsString());
					answerComponent2.setValue(valueType2);
					component.addAnswer(answerComponent2);
				}
				
				String extensionURL = String.format("%spghd-cdm-questionnaireitemsourcedatatype", profileUrl);
				component.addExtension(generateNewExtension(extensionURL, generateSubObvCategoryCoding(dto.getObvCategory())));
				qrItemComponent.add(component);
			}
		}
		return qrItemComponent;
	}	

	private Coding generateRespondentTypeCoding(Long respondentType) {		
		Coding coding = new Coding();
		if (respondentType != null) {
			coding = new Coding("https://athena.ohdsi.org/", "No matching concept", "No matching concept");
		}
		return coding;
	}

	private Coding generateCollectionMethodCoding(Long collectionMethodConcept) {
		Coding coding = new Coding();
		if (collectionMethodConcept != null) {
			coding = new Coding("http://loinc.org/downloads/loinc", "LA26662-9", "Paper");
		}
		return coding;
	}

	private Coding generateSubObvCategoryCoding(Long subObvCategory) {
		Coding coding = new Coding();
		if (subObvCategory != null) {
			coding = new Coding("https://athena.ohdsi.org/", "OMOP4822321", "Observation Recorded from a Survey");
		}
		return coding;
	}

}
