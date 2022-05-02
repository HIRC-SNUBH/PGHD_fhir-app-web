<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html>

<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="Content-Script-Type" content="text/javascript" />
	<meta http-equiv="Content-Style-Type" content="text/css" />
	<meta name="viewport" content="width=device-width, initial-scale=1.0">

	<title>Sankey Diagram</title>

	<!-- Bootstrap core CSS -->
	<link href="<c:url value='/static/css/bootstrap.min.css' />" rel="stylesheet" />

	<!-- Custom styles for this template -->
	<link href="<c:url value='/static/css/common.css' />" rel="stylesheet" />

	<script src="<c:url value='/static/js/jquery.min.js' />"></script>
	<script src="<c:url value='/static/js/jquery-ui.min.js' />"></script>
	<script src="<c:url value='/static/js/bootstrap.min.js' />"></script>

	<script src="<c:url value='/static/js/func.js' />"></script>
	<script src="<c:url value='/static/js/fhirfunc.js' />"></script>

	<!-- fhir Client -->
	<script src="<c:url value='/static/js/fhirclient/build/fhir-client.js' />"></script>

	<!-- Chart -->
	<script src="<c:url value='/static/js/chart.js-3.5.1/package/dist/chart.js' />"></script>
	<script src="<c:url value='/static/js/chart.js-3.5.1/chartjs-chart-sankey.js' />"></script>
	<script src="<c:url value='/static/js/plugin/chartjs-plugin-datalabels.js' />"></script>

	<script src="<c:url value='/static/js/questionList.js' />"></script>

	<!-- questionnaireresponse-sample -->
	<script src="<c:url value='/static/js/qrsample.js' />"></script>

	<!-- Loading Spinner-->
	<link href="<c:url value='/static/css/slick-loader.min.css' />" rel="stylesheet" />
	<script src="<c:url value='/static/js/slick-loader.min.js' />"></script>

</head>

<body>

	<div class="container">

		<div class="row">
			<div class="col-md-12">
				<label class="col-md-12" style="font-size: 30px; text-align: center;">Sankey Diagram</label>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12" style="border: 1px solid skyblue; background-color: white">
				<div class="graphRow1" style="margin-top: 10px;">
					<label class="col-md-12" style="font-size: 20px; text-align: center;">Questionnaire</label>
				</div>
				<div class="form-group row-space">
					<select id="selectQuestion" class="customSelect" name="Question">
					</select>
				</div>
			</div>
		</div>
		<div class="row" style="margin-top: 20px; border: 1px solid skyblue; background-color: white;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<!-- <div class="graphRow1">
						<label class="col-md-12" style="font-size: 20px; text-align: center;">Sankey diagram</label>
					</div> -->
					<div id="recordValueGraph" class="graphRow1">
						<canvas id="chartSankey" class="col-md-12" style="margin-top: 10px; margin-bottom: 10px"></canvas>
					</div>
				</div>
			</div>
		</div>

	</div>

	<script type="text/javascript">
		// parent
		var client = null;
		var patient = null;
		var observation = null;
		var device = null;
		var questionnaireResponse = null;
		var bundle = null;
		var defalutResourceCount;

		// QuestionnaireResponse Array
		var qrArray = [];

		// response List
		var responseObjArray = [];

		// Question List
		var questionArray = [];

		// Answer List		
		var answerObj = null;
		var answerObjArray = [];

		// chart
		var sankeyChart;

		//var plotChartArray = [];

		var selectQuestionLinkId = null;

		var groupBy = function (xs, key) {
			return xs.reduce(function (rv, x) {
				(rv[x[key]] = rv[x[key]] || []).push(x);
				return rv;
			}, {});
		};

		var backgroundColorValues = [
			'rgba(255, 99, 132, 1)',
			'rgba(255, 159, 64, 1)',
			'rgba(255, 205, 86, 1)',
			'rgba(75, 192, 192, 1)',
			'rgba(54, 162, 235, 1)',
			'rgba(153, 102, 255, 1)',
			'rgba(201, 203, 207, 1)',
			'rgba(153, 204, 255, 1)',
			'rgba(153, 153, 255, 1)',
			'rgba(204, 153, 255, 1)',
			'rgba(255, 153, 255, 1)'
		];

		$(document).ready(function () {

			client = parent.client;
			patient = parent.patient;
			observation = parent.observation;
			device = parent.device;
			questionnaireResponse = parent.questionnaireResponse;
			defalutResourceCount = parent.defalutResourceCount;
			bundle = parent.bundle;

			setChart();
			setSelectQuestionList('selectQuestion');

			search(patient, observation, device, questionnaireResponse, bundle);
		});

		function search(patient, observation, device, questionnaireResponse, bundle) {

			this.responseObjArray = [];
			this.patient = patient;
			this.observation = observation;
			this.device = device;
			this.questionnaireResponse = questionnaireResponse;
			this.bundle = bundle;

			if (!checkDataCriteria()) {
				initChart();
			} else {
				var queryString = createQueryString();
				var query = client.request(queryString, { flat: true });
				SlickLoader.enable();
				query.then(function (results) {
					SlickLoader.disable();
					if (results != undefined && results != null && results.length > 0) {
						results.forEach(r => responseObjArray.push(setResponse(r)));
						filteringResourceForSankey(responseObjArray);						
					}
				}).catch(e => alert(e));
			}
		}

		function setChart() {

			Chart.defaults.font.size = 11;

			var ctxSankeyChart = document.getElementById('chartSankey').getContext('2d');
			sankeyChart = new Chart(ctxSankeyChart, {
				type: 'sankey',
				data: {
					datasets: [{
						label: '',
						data: null,
						colorMode: 'gradient', // or 'from' or 'to'
						/* optional labels */
						labels: {},
						/* optional priority */
						priority: {
							a: 0,
							b: 1,
							c: 2
						},
						size: 'max', // or 'min' if flow overlap is preferred
					}]
				},
			});
			
		}

		function setSelectQuestionList(id) {
			var select = $('#' + id)[0];
			select.options.length = 0;
			select.style.width = "100%";

			for (let i = 0; i < questionList.length; i++) {
				var name = questionList[i].text;
				var linkId = questionList[i].linkId;

				if (i == 0) {
					select.append(new Option(name, linkId, true, true));
				} else {
					select.append(new Option(name, linkId, false, false));
				}
			}
			selectQuestionLinkId = $('#selectQuestion option:selected').val();
		}

		function createQueryString() {
			var queryString = null;

			if (bundle != null && bundle.id != null && bundle.id.length > 0) {
				queryString = '/Bundle/' + bundle.id + '/$readsnapshot';
				return queryString;
			}

			var queDateGe = getString(questionnaireResponse.authoredStart) != null ? 'ge' + questionnaireResponse.authoredStart : '';
			var queDateLe = getString(questionnaireResponse.authoredEnd) != null ? 'le' + questionnaireResponse.authoredEnd : '';

			var queryList = new Array();

			if (getString(queDateGe)) queryList.push('authored=' + queDateGe);
			if (getString(queDateLe)) queryList.push('authored=' + queDateLe);

			queryList.push('_count=' + defalutResourceCount);
			queryList.push('_contained=both');

			queryString = '/QuestionnaireResponse?' + queryList.join('&');

			return queryString;
		}

		function filteringResourceForSankey(responseObjArray) {
			if (responseObjArray.length > 0) {

				var countFemaleToMinor = 0;
				var countFemaleTo20s = 0;
				var countFemaleTo30s = 0;
				var countFemaleTo40s = 0;
				var countFemaleTo50s = 0;
				var countFemaleTo60s = 0;
				var countFemaleToElderly = 0;

				var countMaleToMinor = 0;
				var countMaleTo20s = 0;
				var countMaleTo30s = 0;
				var countMaleTo40s = 0;
				var countMaleTo50s = 0;
				var countMaleTo60s = 0;
				var countMaleToElderly = 0;

				var answerListArray = [];

				var questionObj = questionList.find(i => i.linkId == selectQuestionLinkId);

				questionObj.answerList.forEach(f => {
					var answerObj = new Object();
					answerObj.answerCode = f.code;
					answerObj.answerText = f.text;
					answerObj.countMinorToAnswer = 0;
					answerObj.count20sToAnswer = 0;
					answerObj.count30sToAnswer = 0;
					answerObj.count40sToAnswer = 0;
					answerObj.count50sToAnswer = 0;
					answerObj.count60sToAnswer = 0;
					answerObj.countElderyToAnswer = 0;
					answerListArray.push(answerObj);
				});

				responseObjArray.forEach(responseObj => {  // q: QuestionnaireResponse

					//var responseObj = setResponse(q, selectQuestionLinkId);					

					if (responseObj.gender == 'female') {
						switch (responseObj.ageGroup) {
							case "Minor":
								countFemaleToMinor++;
								break;
							case "20S":
								countFemaleTo20s++;
								break;
							case "30S":
								countFemaleTo30s++;
								break;
							case "40S":
								countFemaleTo40s++;
								break;
							case "50S":
								countFemaleTo50s++;
								break;
							case "60S":
								countFemaleTo60s++;
								break;
							case "Elderly":
								countFemaleToElderly++;
								break;
						}
					} else { //male -> korean						
						switch (responseObj.ageGroup) {
							case "Minor":
								countMaleToMinor++;
								break;
							case "20S":
								countMaleTo20s++;
								break;
							case "30S":
								countMaleTo30s++;
								break;
							case "40S":
								countMaleTo40s++;
								break;
							case "50S":
								countMaleTo50s++;
								break;
							case "60S":
								countMaleTo60s++;
								break;
							case "Elderly":
								countMaleToElderly++;
								break;
						}
					}

					//answer List별 카운트 계산
					answerListArray.forEach(i => {
						var filtered = responseObj.answerList.filter(f => f.linkId == selectQuestionLinkId && f.answerCode == i.answerCode);
						if (filtered != undefined && filtered != null && filtered.length > 0) {
							switch (responseObj.ageGroup) {
								case "Minor":
									i.countMinorToAnswerCount += filtered.length;
									break;
								case "20S":
									i.count20sToAnswer += filtered.length;
									break;
								case "30S":
									i.count30sToAnswer += filtered.length;
									break;
								case "40S":
									i.count40sToAnswer += filtered.length;
									break;
								case "50S":
									i.count50sToAnswer += filtered.length;
									break;
								case "60S":
									i.count60sToAnswer += filtered.length;
									break;
								case "Elderly":
									i.countElderyToAnswer += filtered.length;
									break;
							}
						}
					});
				});

				var datasets = [];

				datasets.push(setSankeyDataSet('Female', 'Minor', countFemaleToMinor));
				datasets.push(setSankeyDataSet('Female', '20S', countFemaleTo20s));
				datasets.push(setSankeyDataSet('Female', '30S', countFemaleTo30s));
				datasets.push(setSankeyDataSet('Female', '40S', countFemaleTo40s));
				datasets.push(setSankeyDataSet('Female', '50S', countFemaleTo50s));
				datasets.push(setSankeyDataSet('Female', '60S', countFemaleTo60s));
				datasets.push(setSankeyDataSet('Female', 'Elderly', countFemaleToElderly));

				datasets.push(setSankeyDataSet('Male', 'Minor', countMaleToMinor));
				datasets.push(setSankeyDataSet('Male', '20S', countMaleTo20s));
				datasets.push(setSankeyDataSet('Male', '30S', countMaleTo30s));
				datasets.push(setSankeyDataSet('Male', '40S', countMaleTo40s));
				datasets.push(setSankeyDataSet('Male', '50S', countMaleTo50s));
				datasets.push(setSankeyDataSet('Male', '60S', countMaleTo60s));
				datasets.push(setSankeyDataSet('Male', 'Elderly', countMaleToElderly));

				answerListArray.forEach(i => {
					datasets.push(setSankeyDataSet('Minor', i.answerText, i.countMinorToAnswer));
					datasets.push(setSankeyDataSet('20S', i.answerText, i.count20sToAnswer));
					datasets.push(setSankeyDataSet('30S', i.answerText, i.count30sToAnswer));
					datasets.push(setSankeyDataSet('40S', i.answerText, i.count40sToAnswer));
					datasets.push(setSankeyDataSet('50S', i.answerText, i.count50sToAnswer));
					datasets.push(setSankeyDataSet('60S', i.answerText, i.count60sToAnswer));
					datasets.push(setSankeyDataSet('Elderly', i.answerText, i.countElderyToAnswer));
				});

				var category = setCategory(answerListArray);

				showSankeyChart(category, datasets);
			}
		}

		function setResponse(questionnaireReponse) {

			var responseObj = new Object();

			responseObj.patientId = null;
			responseObj.gender = null;
			responseObj.age = null;
			responseObj.ageGroup = null;
			responseObj.answerList = [];

			var patient = questionnaireReponse.contained.find(i => i.resourceType == "Patient");

			if (patient != undefined && patient != null) {

				responseObj.patientId = patient.id;
				responseObj.gender = patient.gender;
				responseObj.age = new Date(questionnaireReponse.authored).getFullYear() - new Date(patient.birthDate).getFullYear() + 1;

				responseObj.ageGroup = getAgeGroup(responseObj.age);
			}

			var itemArray = questionnaireReponse.item;

			if (itemArray != undefined && itemArray != null && itemArray.length > 0) {

				itemArray.forEach(item => {

					var answerObj = new Object();
					answerObj.linkId = item.linkId;

					if (item.answer != null && item.answer.length > 0 && item.answer[0] != undefined && item.answer[0].valueCoding != undefined) {
						answerObj.answerCode = item.answer[0].valueCoding.code;
						answerObj.answerText = item.answer[0].valueCoding.display;
					}

					responseObj.answerList.push(answerObj);
				});
			}
			return responseObj;
		}

		function setCategory(answerListArray) {
			var category = {};

			category['Female'] = 'Female';
			category['Male'] = 'Male';
			category['Minor'] = 'Minor';
			category['20S'] = '20S';
			category['30S'] = '30S';
			category['40S'] = '40S';
			category['50S'] = '50S';
			category['60S'] = '60S';
			category['Elderly'] = 'Elderly';

			for (let i = 0; i < answerListArray.length; i++) {
				category[answerListArray[i].answerText] = answerListArray[i].answerText;
			}

			return category;
		}


		function setSankeyDataSet(from, to, flow) {
			var dataset = new Object();

			dataset.from = from;
			dataset.to = to;
			dataset.flow = flow;

			return dataset;
		}		

		function showSankeyChart(category, datasets) {

			const colors = [
				'#1F77B4',
				'#FF7F0E',
				'#2CA02C',
				'#D62728',
				'#9467BD',
				'#8C564B',
				'#E377C2',
				'#7F7F7F',
				'#BCBD22',
				'#FF7F0E',
				'#2CA02C',
				'#D62728',
				'#9467BD',
				'#8C564B',
				'#E377C2',
				'#7F7F7F',
				'#BCBD22'
			];

			const catColors = {};
			let colorIndex = 0;

			const getColor = (item) => {
				const cat = category[item];
				if (!catColors[cat]) {
					catColors[cat] = colors[colorIndex % 17];
					colorIndex++;
				}
				return catColors[cat];
			};

			sankeyChart.data.datasets = [{
				label: '',
				data: datasets,
				colorFrom: (c) => getColor(c.dataset.data[c.dataIndex].from),
				colorTo: (c) => getColor(c.dataset.data[c.dataIndex].to),
				colorMode: 'gradient', // or 'from' or 'to'
				/* optional labels */
				labels: {},
				/* optional priority */
				priority: {
					a: 0,
					b: 1,
					c: 2
				},
				size: 'max', // or 'min' if flow overlap is preferred
			}];

			sankeyChart.update();

		}

		//차트 초기화
		function initChart() {
			sankeyChart.data = null;
			sankeyChart.update();			
		}

		$("#selectQuestion").change(function () {
			selectQuestionLinkId = $('#selectQuestion option:selected').val();
			filteringResourceForSankey(responseObjArray);
		});

		function checkDataCriteria() {
			var retVal = true;

			if (questionnaireResponse.authoredStart == null || questionnaireResponse.authoredEnd == null) {
				retVal = false;
			}

			return retVal;
		}
	</script>
</body>

</html>