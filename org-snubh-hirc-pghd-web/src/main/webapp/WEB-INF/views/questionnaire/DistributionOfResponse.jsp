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

	<title>Distribution Of Response by Questionnaire</title>

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
				<label class="col-md-12" style="font-size: 30px; text-align: center;">Distribution Of Response by Questionnaire</label>
			</div>
		</div>		

		<div class="row"
			style="margin-top: 20px; border-top: 1px solid skyblue; border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1" style="margin-top: 20px">
						<label id="chartPlotLabel0" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot0" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel1" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot1" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel2" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot2" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel3" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot3" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel4" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot4" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel5" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot5" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel6" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot6" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel7" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot7" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel8" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot8" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel9" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot9" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel10" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot10" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel11" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot11" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel12" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot12" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel13" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot13" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel14" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot14" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel15" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot15" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel16" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot16" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel17" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot17" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel18" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot18" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel19" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot19" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel20" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot20" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel21" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot21" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel22" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot22" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel23" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot23" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel24" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot24" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel25" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot25" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel26" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot26" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel27" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot27" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel28" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot28" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel29" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot29" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel30" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot30" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel31" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot31" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel32" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot32" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel33" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot33" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel34" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot34" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel35" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot35" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel36" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot36" class="col-md-12" style="height: 180px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row"
			style="border-left: 1px solid skyblue; border-right: 1px solid skyblue; border-bottom: 1px solid skyblue; background-color: white; height: 220px;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1">
						<label id="chartPlotLabel37" class="col-md-12"></label>
					</div>
					<div class="graphRow1">
						<canvas id="chartPlot37" class="col-md-12" style="height: 180px;"></canvas>
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
		//var sankeyChart;

		var plotChartArray = [];

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
			//setSelectQuestionList('selectQuestion');

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
						filteringResourceForPlot(responseObjArray);
					}
				}).catch(e => alert(e));
			}
		}

		function setChart() {

			Chart.defaults.font.size = 11;			

			var barOptions_stacked = {
				indexAxis: 'y',
				plugins: {
					datalabels: {
						color: '#fff'
					},
					tooltip: {
							callbacks: {
								title: function(tooltipItem){
									var answerText = tooltipItem[0].dataset.label;
									return answerText;
								},
								label: function (tooltipItem) {
									var ratioVal = tooltipItem.dataset.data1[tooltipItem.dataIndex];
									var countVal = tooltipItem.dataset.data2[tooltipItem.dataIndex];
									return '[Record Count : ' + countVal + '] [Ratio : ' + ratioVal + '%]';
								}
							},
						}
				},
				scales: {
					x: {						
					    min: 0,
					    max: 100,
						stacked: true,
						ticks:{
							callback: function (value) {
								return (value / this.max * 100).toFixed(0) + '%'; // convert it to percentage
							}
						}
					},
					y: {
						display: false,
						stacked: true
					}
				}
			};

			for (let i = 0; i < questionList.length; i++) {
				$('#chartPlotLabel' + i.toString()).text(questionList[i].text);

				var ctxChart = document.getElementById('chartPlot' + i.toString());
				var plotChart = new Chart(ctxChart, {
					type: 'bar',
					data: {
						labels: [],
						datasets: []
					},
					options: barOptions_stacked,
				});
				plotChartArray.push(plotChart);
			}
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

		function filteringResourceForPlot(responseObjArray) {
			if (responseObjArray.length > 0) {
				for (let i = 0; i < questionList.length; i++) {
					showPlotChart(i.toString(), plotChartArray[i], questionList[i], responseObjArray);
				}
			}
		}

		function showPlotChart(id, chart, question, responseObjArray) {
			var datasetObjArray = [];

			chart.data.labels = [question.text];

			if (responseObjArray.length > 0) {

				var totalCount = 0;
				responseObjArray.forEach(r => {
					var filtered = r.answerList.filter(f => f.linkId == question.linkId);
					if (filtered != undefined && filtered != null && filtered.length > 0) {
						totalCount += filtered.length;
					}
				});

				for (let i = 0; i < question.answerList.length; i++) { //data Set 생성					
					var datasetObj = new Object();
					datasetObj.data = [];
					datasetObj.data1 = [];
					datasetObj.data2 = [];
					datasetObj.backgroundColor = backgroundColorValues[i % 11];
					datasetObj.hoverBackgroundColor = backgroundColorValues[i % 11];
					datasetObj.label = question.answerList[i].text;

					var count = 0;

					responseObjArray.forEach(f => {
						var filterd = f.answerList.filter(a => a.linkId == question.linkId && a.answerCode == question.answerList[i].code);
						if (filterd != undefined && filterd != null && filterd.length > 0) {
							count += filterd.length;
						}
					});

					if (totalCount < 1) {
						datasetObj.data.push(0);
					} else {
						var value = (count / totalCount * 100).toFixed(2);
						//datasetObj.data.push(count)
						//datasetObj.data1.push(value)
						datasetObj.data.push(value);
						datasetObj.data1.push(value);
						datasetObj.data2.push(count);
					}

					datasetObjArray.push(datasetObj);
				}

				chart.data.datasets = datasetObjArray;
				chart.update();
			}
			return datasetObjArray;

		}		

		//차트 초기화
		function initChart() {		

			plotChartArray.forEach(f => {
				f.data = null;
				f.update();
			});
		}		

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