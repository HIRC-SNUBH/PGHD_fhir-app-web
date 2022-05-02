package org.snubh.hirc.pghd.api.util.convert;

import java.net.UnknownHostException;

import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Encounter.EncounterHospitalizationComponent;
import org.hl7.fhir.r4.model.Encounter.EncounterStatus;
import org.hl7.fhir.r4.model.Enumerations.ResourceType;
import org.hl7.fhir.r4.model.Period;
import org.snubh.hirc.pghd.api.dao.i.BaseConvert;
import org.snubh.hirc.pghd.api.dto.ConceptDto;
import org.snubh.hirc.pghd.api.dto.VisitOccurrenceDto;
import org.snubh.hirc.pghd.api.util.ValueSet;
import org.springframework.stereotype.Component;

@Component
public class EncounterConvert extends BaseConvert<VisitOccurrenceDto, Encounter> {

	public EncounterConvert() throws UnknownHostException {
		super();
	}

	@Override
	public Encounter convert(VisitOccurrenceDto dto) {

		if (dto == null)
			return null;

		Encounter encounter = new Encounter();
		encounter.setId(dto.getIdentifierAsString());
		encounter.setServiceProvider(generateReference(ResourceType.ORGANIZATION, dto.getServiceProvider()));
		encounter.setHospitalization(generateHospitalizationComponent(dto.getHospitalizationAdmitConcept(), dto.getHospitalizationDischargeConcept()));
		encounter.setSubject(generateReference(ResourceType.PATIENT, dto.getSubject()));
		encounter.setClass_(generateClassCoding(dto.getVisitClass()));
		encounter.setStatus(generateEncounterStatus(dto.getVisitClass()));
		String sourceDataTypeExtensionURL = String.format("%spghd-cdm-visittype", profileUrl);
		encounter.addExtension(generateNewExtension(sourceDataTypeExtensionURL, generateVisitTypeCoding(dto.getTypeConcept())));

		Period period = new Period();
		if (dto.getPeriodStart() != null)
			period.setStart(dto.getPeriodStart());
		if (dto.getPeriodEnd() != null)
			period.setEnd(dto.getPeriodEnd());
		encounter.setPeriod(period);

		String metaURL = String.format("%spghd-cdm-encounter", profileUrl);
		encounter.setMeta(generateNewMeta(metaURL, null));

		return encounter;
	}

	private EncounterStatus generateEncounterStatus(Long classConceptDto) {
		if (classConceptDto != null) {
			if (classConceptDto == ValueSet.EncounterType.STILL_PATIENT)
				return EncounterStatus.INPROGRESS;
			else
				return EncounterStatus.FINISHED;
		} else {
			return EncounterStatus.FINISHED;
		}
	}

	@SuppressWarnings("static-access")
	private Coding generateClassCoding(Long classConceptDto) {
		String encounterClassSystem = "http://terminology.hl7.org/CodeSystem/v3-ActCode";
		if (classConceptDto != null) {
			Coding classCoding = new Coding();
			if (classConceptDto == 9201L) {
				classCoding = new Coding(encounterClassSystem, "IMP", "inpatient encounter");
			} else if (classConceptDto == 9202L) {
				classCoding = new Coding(encounterClassSystem, "AMB", "ambulatory");
			} else if (classConceptDto == 9203L) {
				classCoding = new Coding(encounterClassSystem, "EMER", "emergency");
			} else if (classConceptDto == 32220L) {
				classCoding = new Coding(encounterClassSystem, "IMP", "inpatient encounter");
			} else if (classConceptDto == 32036L) {
				classCoding = new Coding(super.OMOPGENERATEDURL, "OMOP4822461", "Laboratory Visit");
			}
			return classCoding;
		} else {
			return new Coding();
		}
	}

	private EncounterHospitalizationComponent generateHospitalizationComponent(ConceptDto admitCode, ConceptDto dischargeCode) {
		EncounterHospitalizationComponent hospitalizationComponent = new EncounterHospitalizationComponent();
		if (admitCode != null) {
			hospitalizationComponent.setAdmitSource(generateCodeableConcept(admitCode));
		}
		if (dischargeCode != null) {
			hospitalizationComponent.setDischargeDisposition(generateCodeableConcept(dischargeCode));
		}
		return hospitalizationComponent;
	}

	private Coding generateVisitTypeCoding(Long sourceTypeConcept) {
		Coding coding = new Coding();
		if (sourceTypeConcept != null) {
			if (ValueSet.EncounterSourceType.CLINICAL_STUDY_VISIT == sourceTypeConcept) {
				coding = new Coding(ValueSet.EncounterSourceType.CLINICAL_STUDY_VISIT_SYSTEM, ValueSet.EncounterSourceType.CLINICAL_STUDY_VISIT_CODE, ValueSet.EncounterSourceType.CLINICAL_STUDY_VISIT_DISPLAY);
			} else if (ValueSet.EncounterSourceType.VISIT_DERIVED_FROM_EHR_RECORD == sourceTypeConcept) {
				coding = new Coding(ValueSet.EncounterSourceType.VISIT_DERIVED_FROM_EHR_RECORD_SYSTEM, ValueSet.EncounterSourceType.VISIT_DERIVED_FROM_EHR_RECORD_CODE, ValueSet.EncounterSourceType.VISIT_DERIVED_FROM_EHR_RECORD_DISPLAY);
			}
		}

		return coding;
	}
}
