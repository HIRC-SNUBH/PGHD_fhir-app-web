package test.dataaccesslayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snubh.hirc.pghd.api.dao.i.IDao;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;
import ca.uhn.fhir.rest.param.ParamPrefixEnum;
import ca.uhn.fhir.rest.param.StringParam;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/context-spring.xml", "/context-hibernate.xml" })
public class SurveyConductResponseDaoTest extends BaseDaoTest {

	@Resource(name = "surveyConductResponseDao")
	public IDao dao;

	@SuppressWarnings("unchecked")
	@Test
	public void daoObvConceptIdSearchTest() throws InstantiationException, IllegalAccessException {
//		SearchParameterMap searchParameterMap = createTokenParam("questionnaire.code", "https://athena.ohdsi.org/search-terms/terms", "4076229");
		SearchParameterMap searchParameterMap = createTokenParam("questionnaire.code", "http://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html", "224293004");
//		SearchParameterMap searchParameterMap = createTokenParam("questionnaire.code", "", "224293004");

		List<QuestionnaireResponse> list = (List<QuestionnaireResponse>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoObvCodeSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("questionnaire.code", "", "224293004");

		List<QuestionnaireResponse> list = (List<QuestionnaireResponse>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
	}

	@Test
	public void daoReadTest() throws InstantiationException, IllegalAccessException, ParseException {
		QuestionnaireResponse questionnaireResponse = dao.findById("1");
		showResource(questionnaireResponse);
		Assert.assertEquals(questionnaireResponse.getId(), "1");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoQuestionnaireIdSearchTest() throws InstantiationException, IllegalAccessException {
//		SearchParameterMap searchParameterMap = createTokenParam("questionnaire._id", "", "1");
		SearchParameterMap searchParameterMap = createTokenParam("_id", "", "1");

		List<QuestionnaireResponse> list = (List<QuestionnaireResponse>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		for (QuestionnaireResponse questionnaireResponse : list) {
			String id = questionnaireResponse.getId();
			Assert.assertEquals(id, "1");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoSubjectSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createReferenceParam("subject", "1");

		List<QuestionnaireResponse> list = (List<QuestionnaireResponse>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
		for (QuestionnaireResponse questionnaireResponse : list) {
			String[] ref = questionnaireResponse.getSubject().getReference().split("/");
			Assert.assertEquals(ref[1], "1");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoAuthoredSearchTest() throws ParseException, InstantiationException, IllegalAccessException {
		HashMap<ParamPrefixEnum, String> dates = new HashMap<ParamPrefixEnum, String>();
		dates.put(ParamPrefixEnum.EQUAL, "2021-03-01");
		SearchParameterMap searchParameterMap = createDateParam("authored", dates);
		Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("2021-03-01");
		List<QuestionnaireResponse> list = (List<QuestionnaireResponse>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		for (QuestionnaireResponse questionnaireResponse : list) {
			Date qDate = questionnaireResponse.getAuthored();
			Assert.assertEquals(qDate, date);
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoQuestionnaireTitleSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createStringParam("questionnaire.title", true, "설문지");

		List<QuestionnaireResponse> list = (List<QuestionnaireResponse>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		for (QuestionnaireResponse questionnaireResponse : list) {
			Questionnaire questionnaire = (Questionnaire) questionnaireResponse.getContained().get(0);
			Assert.assertEquals(questionnaire.getTitle(), "설문지");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoEncounterSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createReferenceParam("encounter", "1");

		List<QuestionnaireResponse> list = (List<QuestionnaireResponse>) dao.findBySearchParameter(searchParameterMap, 10, 0);

		for (QuestionnaireResponse questionnaireResponse : list) {
			String[] ref = questionnaireResponse.getEncounter().getReference().split("/");
			Assert.assertEquals(ref[1], "1");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void containedPatientTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("_id", "", "1");
		searchParameterMap.add("_contained", new StringParam("both"));
		
		List<QuestionnaireResponse> list = (List<QuestionnaireResponse>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
		for (QuestionnaireResponse questionnaireResponse : list) {
			String id = questionnaireResponse.getId();
			Assert.assertEquals(id, "1");
		}
	}
}