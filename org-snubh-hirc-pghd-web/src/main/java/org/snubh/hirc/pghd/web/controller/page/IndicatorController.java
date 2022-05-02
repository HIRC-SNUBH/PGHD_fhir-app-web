package org.snubh.hirc.pghd.web.controller.page;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class IndicatorController {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getPage(final HttpServletRequest request) {
		return "index";
	}
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String getIndexPage(final HttpServletRequest request) {
		return "index";
	}

	@RequestMapping(value = "/population/NumberOfUsers", method = RequestMethod.GET)
	public String getEtcPage(final HttpServletRequest request) {
		return "population/NumberOfUsers";
	}

	@RequestMapping(value = "/population/DropOutRate", method = RequestMethod.GET)
	public String getDropOffCurvePage(final HttpServletRequest request) {
		return "population/DropOutRate";
	}

	@RequestMapping(value = "/population/UsersAndRecordByOBS", method = RequestMethod.GET)
	public String getUsersAndRecordByObsPage(final HttpServletRequest request) {
		return "population/UsersAndRecordByOBS";
	}

	@RequestMapping(value = "/population/TPRbyAgeGenderGraph", method = RequestMethod.GET)
	public String getTPRbyAgeGenderGraphPage(final HttpServletRequest request) {
		return "population/TPRbyAgeGenderGraph";
	}

	@RequestMapping(value = "/population/ObsValueDist", method = RequestMethod.GET)
	public String getObsValueDistGraphPage(final HttpServletRequest request) {
		return "population/ObsValueDist";
	}

	@RequestMapping(value = "/profile/UsageStatusByPGHDItems", method = RequestMethod.GET)
	public String getUsageStatusByPGHDItems(final HttpServletRequest request) {
		return "profile/UsageStatusByPGHDItems";
	}

	@RequestMapping(value = "/profile/EMRStatus", method = RequestMethod.GET)
	public String getEMRStatus(final HttpServletRequest request) {
		return "profile/EMRStatus";
	}

	@RequestMapping(value = "/questionnaire/SankeyDiagram", method = RequestMethod.GET)
	public String getQuestionnaire(final HttpServletRequest request) {
		return "questionnaire/SankeyDiagram";
	}

	@RequestMapping(value = "/questionnaire/DistributionOfResponse", method = RequestMethod.GET)
	public String getDistribution(final HttpServletRequest request) {
		return "questionnaire/DistributionOfResponse";
	}
}
