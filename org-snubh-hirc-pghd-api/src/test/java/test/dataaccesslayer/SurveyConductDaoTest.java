package test.dataaccesslayer;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Resource;

import org.hl7.fhir.r4.model.Questionnaire;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.snubh.hirc.pghd.api.dao.i.IDao;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.uhn.fhir.jpa.searchparam.SearchParameterMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/context-spring.xml", "/context-hibernate.xml" })
public class SurveyConductDaoTest extends BaseDaoTest {
	@Resource(name = "surveyConductDao")
	public IDao dao;

	@SuppressWarnings("unchecked")
	@Test
	public void daoObvConceptIdSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("code", "https://athena.ohdsi.org/search-terms/terms", "4076229");

		List<Questionnaire> list = (List<Questionnaire>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoObvCodeSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("code", "", "224293004");

		List<Questionnaire> list = (List<Questionnaire>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
	}

	@Test
	public void daoReadTest() throws InstantiationException, IllegalAccessException, ParseException {

		Questionnaire questionnaire = dao.findById("1");
		showResource(questionnaire);

		Assert.assertEquals(questionnaire.getId(), "1");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoQuestionnaireIdSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("_id", "", "1");

		List<Questionnaire> list = (List<Questionnaire>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
		for (Questionnaire questionnaire : list) {
			String id = questionnaire.getId();
			Assert.assertEquals(id, "1");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoQuestionnaireTitleSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createStringParam("title", true, "설문지");

		List<Questionnaire> list = (List<Questionnaire>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		for (Questionnaire questionnaire : list) {
			Assert.assertEquals(questionnaire.getTitle(), "설문지");
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void daoStatusSearchTest() throws InstantiationException, IllegalAccessException {
		SearchParameterMap searchParameterMap = createTokenParam("status", "", "active");

		List<Questionnaire> list = (List<Questionnaire>) dao.findBySearchParameter(searchParameterMap, 10, 0);
		showResource(list);
		for (Questionnaire questionnaire : list) {
			Assert.assertEquals(questionnaire.getStatus().toCode(), "active");
		}
	}
}