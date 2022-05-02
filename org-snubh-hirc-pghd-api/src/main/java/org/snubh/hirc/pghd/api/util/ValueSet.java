package org.snubh.hirc.pghd.api.util;

public class ValueSet {

	public class Concept {
		public static final String CONCEPT_ID_SYSTEM = "https://athena.ohdsi.org/search-terms/terms";
		public static final long ERROR = -1;
		public static final String OMOP_SYSTEM = "https://athena.ohdsi.org/";
		public static final String OMOP_SYSTEM_STR = "OMOP generated";
		public static final String SNUBH_SYSTEM = "https://www.cdm.snubh.org/Atlas";
		public static final String SNUBH_SYSTEYM_STR = "SNUBH generated";
	}

	public class Gender {
		public static final long M = 8507;
		public static final long F = 8532;

		public static final String FHIR_CODE_MALE = "male";
		public static final String FHIR_CODE_FEMALE = "female";
		public static final String SYSTEM = "http://hl7.org/fhir/administrative-gender";
		public static final String MALE_CODE = "male";
		public static final String FEMALE_CODE = "female";

	}

	public class Race {
		public static final long KOREAN = 38003585L;
		public static final long UNKNOWN = 0L;

		public static final String KOREAN_CODE = "2.12";
		public static final String KOREAN_SYSTEM = "https://www.cdc.gov/nchs/data/dvs/Race_Ethnicity_CodeSet.pdf";
		public static final String KOREAN_DISPLAY = "Korean";

		public static final String UNKNOWN_CODE = "No matching concept";
		public static final String UNKNOW_SYSTEM = "https://athena.ohdsi.org/";
		public static final String UNKNOW_DISPLAY = "No matching concept";
		
		public static final String US_CORE_RACE_URL = "http://hl7.org/fhir/us/core/StructureDefinition/us-core-race";
		
		public static final String US_CDC_CODE_SYSTEM = "urn:oid:2.16.840.1.113883.6.238";
		public static final String US_CDC_ASIAN_CODE = "2028-9";
		public static final String US_CDC_ASIAN_DISPLAY = "Asian";

		public static final String US_CDC_KOREAN_CODE = "2040-4";
		public static final String US_CDC_KOREAN_DISPLAY = "Korean";

		public static final String V3_NULL_FLAVOR_CODE_SYSTEM = "http://terminology.hl7.org/CodeSystem/v3-NullFlavor";
		public static final String V3_NULL_FLAVOR_UNKNOWN_CODE = "UNK";
		public static final String V3_NULL_FLAVOR_UNKNOWN_DISPLAY = "Unknown";
	}

	public class EncounterType {
		public static final String FHIR_SYSTEM = "http://terminology.hl7.org/CodeSystem/v3-ActCode";
		public static final long IMP = 9201;
		public static final long STILL_PATIENT = 32220;
		public static final long AMB = 9202;
		public static final long EMER = 9203;
		public static final long LABORATORY_ID = 32036;

		public static final String FHIR_CODE_IMP = "IMP";
		public static final String FHIR_CODE_AMB = "AMB";
		public static final String FHIR_CODE_EMER = "EMER";

		public static final String FHIR_CODE_IN_PROGRESS = "in-progress";
		public static final String FHIR_CODE_FINISHED = "finished";

		public static final String LABORATORY_CONCEPT = "OMOP4822461";
		public static final String SYSTEM = "https://athena.ohdsi.org/";

	}

	public class EncounterSourceType {
		public static final long VISIT_DERIVED_FROM_EHR_RECORD = 44818518L;
		public static final long CLINICAL_STUDY_VISIT = 44818519L;

		public static final String VISIT_DERIVED_FROM_EHR_RECORD_CODE = "OMOP4822464";
		public static final String VISIT_DERIVED_FROM_EHR_RECORD_SYSTEM = "https://athena.ohdsi.org/";
		public static final String VISIT_DERIVED_FROM_EHR_RECORD_DISPLAY = "Visit derived from EHR record";

		public static final String CLINICAL_STUDY_VISIT_CODE = "OMOP4822463";
		public static final String CLINICAL_STUDY_VISIT_SYSTEM = "https://athena.ohdsi.org/";
		public static final String CLINICAL_STUDY_VISIT_DISPLAY = "Clinical Study visit";
	}

	public class ObservationCodeType {
		public static final long BLOOD_PRESSURE = 45770187;
		public static final long BLOOD_GLUCOSE = 37399654;
		public static final long EXERCISE_DURATION = 40758540;
		public static final long HEART_BEAT = 4088121;
		public static final long BODY_WEIGHT = 4099154;
		public static final long LENGTH_BODY = 4087492;
		public static final long NUMBER_STEP_PEDOMETER = 40758552;
		public static final long DURATION_SLEEP = 4086504;
		public static final long SATISFACTION_SLEEP = 2000000344;
		public static final long LEVEL_STRESS = 4234506;
		public static final long EXERCISE_INTENSITY = 44786666;
		public static final String BLOOD_PRESSURE_CODE = "924481000000109";
		public static final String BLOOD_GLUCOSE_CODE = "997671000000106";
		public static final String EXERCISE_DURATION_CODE = "55411-3";
		public static final String HEART_BEAT_CODE = "248646004";
		public static final String BODY_WEIGHT_CODE = "27113001";
		public static final String LENGTH_BODY_CODE = "248334005";
		public static final String NUMBER_STEP_PEDOMETER_CODE = "55423-8";
		public static final String DURATION_SLEEP_CODE = "248263006";
		public static final String SATISFACTION_SLEEP_CODE = "SNUBH0046";
		public static final String LEVEL_STRESS_CODE = "405052004";
		public static final String EXERCISE_INTENSITY_CODE = "74008-4";
		public static final String OMOP_GENERATED = "OMOP generated";
		public static final String SNUBH_GENERATED = "SNUBH generated";
	}

	public class ObservationEventFieldConcept {
		public static final long DEVICE_EXPOSURE_FIELD = 1147693;
		public static final long SURVEY_CONDUCT_FIELD = 1147832;
	}

	public class ObservationSystemType {
		public static final String OMOP_GENERATED = "OMOP generated";
		public static final String SNUBH_GENERATED = "SNUBH generated";
	}

	public class ObservationCategory {
		public static final String FINAL = "final";
		public static final String FHIR_CODE_VITAL_SIGNS = "vital-signs";
		public static final String FHIR_CODE_EXAM = "exam";
		public static final String FHIR_CODE_ACTIVITY = "activity";
		public static final String FHIR_CODE_SURVEY = "survey";
		public static final long PATIENT_REPORTED = 44814721;
	}

	public class Questionnaire {
		public static final String ACTIVE = "active";
	}

	public class QuestionnaireResponseQuantityCode {
		public static final String PROFIT_ID = "4076114";
		public static final String DRINK_PER_WEEK_ID = "35609491";
		public static final String TOBACCO_AVERAGE_ID = "40766356";
		public static final String TOBACCO_PER_DAY_ID = "40766364";
		public static final String TOBACCO_NUM_PER_DAY_ID = "40766929";
		public static final String CIGARRETE_PACK_YEARS = "4269183";
		public static final String SMOKING_AGE = "40770348";
		public static final String HEAVY_EXCERSIZE = "36203099";
		public static final String HEAVY_EXCERSIZE_SPENT = "36203100";
		public static final String MODERATE_EXCERSIZE = "36203101";
		public static final String MODERATE_EXCERSIZE_SPENT = "36203102";
		public static final String LIGHT_WALK = "36203103";
		public static final String LIGHT_WALK_SPENT = "36203104";
		public static final String SITTING = "36203105";
		public static final String SLEPPING_TIME = "40768255";

	}
}
