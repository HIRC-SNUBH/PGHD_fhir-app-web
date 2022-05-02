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

	<title>ETC</title>

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
	<script src="<c:url value='/static/js/plugin/chartjs-plugin-datalabels.js' />"></script>

	<!-- Loading Spinner-->
	<link href="<c:url value='/static/css/slick-loader.min.css' />" rel="stylesheet" />
	<script src="<c:url value='/static/js/slick-loader.min.js' />"></script>
</head>

<body>
	<div class="container">
		<div class="row">
			<div class="col-md-12">
				<label class="col-md-12" style="font-size: 30px; text-align: center;">Number of Users</label>
			</div>
		</div>
		<div class="row" style="border: 1px solid skyblue; background-color: white; width: 452px; margin-top: 20px">
			<div class="pageContainer">
				<div>
					<div class="graphRow1" style="margin-top: 20px;">
						<label class="col-md-12" style="font-size: 20px; text-align: center;">Number of Users by Gender</label>
					</div>
					<div class="graphRow1" style="width: 400px; height: 450px;">
						<canvas id="chartGender"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row" style="margin-top: 20px; border: 1px solid skyblue; background-color: white">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1" style="margin-top: 20px;">
						<label class="col-md-12" style="font-size: 20px; text-align: center;">Number of Users by Life-log Items</label>
					</div>
					<div class="graphRow1">
						<canvas id="chartObservation" class="col-md-12" style="margin-bottom: 20px"></canvas>						
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

		//chart 변수
		var genderChart;
		var observationChart;
		var backgroundColorValues = [
			'rgba(255, 99, 132, 0.2)',
			'rgba(255, 159, 64, 0.2)',
			'rgba(255, 205, 86, 0.2)',
			'rgba(75, 192, 192, 0.2)',
			'rgba(54, 162, 235, 0.2)',
			'rgba(153, 102, 255, 0.2)',
			'rgba(201, 203, 207, 0.2)',
			'rgba(153, 204, 255, 0.2)',
			'rgba(153, 153, 255, 0.2)',
			'rgba(204, 153, 255, 0.2)',
			'rgba(255, 153, 255, 0.2)'
		];
		var borderColorValues = [
			'rgb(255, 99, 132)',
			'rgb(255, 159, 64)',
			'rgb(255, 205, 86)',
			'rgb(75, 192, 192)',
			'rgb(54, 162, 235)',
			'rgb(153, 102, 255)',
			'rgb(201, 203, 207)',
			'rgb(153, 204, 255)',
			'rgb(153, 153, 255)',
			'rgb(204, 153, 255)',
			'rgb(255, 153, 255)'
		];

		//query
		var observationCount = null;
		var observationObjArray = []; // original		

		var groupBy = function (xs, key) {
			return xs.reduce(function (rv, x) {
				(rv[x[key]] = rv[x[key]] || []).push(x);
				return rv;
			}, {});
		};

		window.onload = function () {
			// Register the plugin to all charts:
			Chart.register(ChartDataLabels);

			this.client = parent.client;
			this.patient = parent.patient;
			this.observation = parent.observation;
			this.device = parent.device;
			this.defalutResourceCount = parent.defalutResourceCount;
			this.bundle = parent.bundle;

			setChart();
			search(patient, observation, device, questionnaireResponse, bundle);
		}

		// chart 초기설정
		function setChart() {

			var ctxGenderChart = document.getElementById("chartGender").getContext('2d');

			var genderChartDatasets = [{
				label: '',
				data: null,
				backgroundColor: [colors.red, colors.blue],
				hoverOffset: 4
			}];

			var genderChartoptions = {
				tooltips: {
					enabled: false
				},
				plugins: {
					datalabels: {
						formatter: (value, ctx) => {
							let sum = 0;
							let dataArr = ctx.chart.data.datasets[0].data;
							dataArr.map(data => {
								sum += data;
							});
							let percentage = (value * 100 / sum).toFixed(2) + "%";
							return percentage;
						},
						color: '#fff',
					}
				}
			};

			genderChart = new Chart(ctxGenderChart, {
				type: 'pie',
				data: {
					labels: ['Male', 'Female'],
					datasets: genderChartDatasets
				},
				options: genderChartoptions
			});

			var observationChartScale = {
				x: {
					beginAtZero: true,
					display: true,
					title: {
						display: true,
						text: 'Life-log Items',
						font: {
							size: 20
						},
						padding: { top: 10, left: 0, right: 0, bottom: 0 }
					}
				},
				y: {
					beginAtZero: true,
					display: true,
					title: {
						display: true,
						text: 'Number of Users',
						font: {
							size: 20
						},
						padding: { top: 10, left: 0, right: 0, bottom: 0 }
					}
				}
			};

			var ctxObservationChart = document.getElementById("chartObservation").getContext('2d');
			observationChart = new Chart(ctxObservationChart, {
				type: 'bar',
				data: {
				},
				options: {
					plugins: {
						legend: {
							display: false,
						}
					},
					scales: observationChartScale
				}
			});
		}

		// Data Criteria 변경 시
		function search(patient, observation, device, questionnaireResponse, bundle) {

			this.observationObjArray = [];
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
					setObservationObjArray(results);
					showPieChart(observationObjArray);
					showBarChart(observationObjArray);
				}).catch(error => alert(error));
			}
		}

		function setObservationObjArray(observations) {

			if (observations != undefined && observations != null && observations.length > 0) {

				observations.forEach(observation => {
					var observationObj = new Object();

					observationObj.date = null;
					observationObj.patientId = null;
					observationObj.gender = null;
					observationObj.birthDate = null;
					observationObj.age = null;
					observationObj.code = null;
					observationObj.display = null;

					observationObj.date = getFormatDate(new Date(observation.effectiveDateTime));
					observationObj.patientId = observation.subject.reference;

					if (observation.contained != undefined && observation.contained != null && observation.contained.length > 0) {

						var patientResource = observation.contained.find(f => f.resourceType == 'Patient');

						if (patientResource != undefined && patientResource != null) {
							observationObj.gender = patientResource.gender;
							//observationObj.patientId = patientResource.id;
							observationObj.birthDate = patientResource.birthDate;
							observationObj.age = new Date(observation.effectiveDateTime).getFullYear() - new Date(patientResource.birthDate).getFullYear() + 1
						}
					}

					if (observation.code != null && observation.code.coding != null && observation.code.coding.length > 0) {
						observationObj.code = observation.code.coding[0].code;
						observationObj.display = observation.code.coding[0].display
					}
					observationObjArray.push(observationObj);
				});
				observationObjArray = filteredByAge(observationObjArray)
			}
		}

		function filteredByAge(observationObjArray) {
			// age filter
			if (patient != null && (patient.birthdateStart != null || patient.birthdateEnd != null)) {
				var returnArray = null;

				if (patient.birthdateStart != null && patient.birthdateEnd != null) {
					returnArray = observationObjArray.filter(f => f.age >= parseInt(patient.birthdateStart) && f.age <= parseInt(patient.birthdateEnd));
				} else if (patient.birthdateStart != null && patient.birthdateEnd === null) {
					returnArray = observationObjArray.filter(f => f.age >= parseInt(patient.birthdateStart));
				} else {
					returnArray = observationObjArray.filter(f => f.age <= parseInt(patient.birthdateEnd));
				}

				if (returnArray === null) {
					return null;
				} else {
					return returnArray;
				}
			} else {
				return observationObjArray;
			}
		}

		function createQueryString() {

			var queryString = null;

			if (bundle != null && bundle.id != null && bundle.id.length > 0) {
				queryString = '/Bundle/' + bundle.id + '/$readsnapshot';
				return queryString;
			}

			var obsCode = observation.code.filter(i => getObservationCategory(i) != undefined).map(i => getObservationCategory(i).code).join(',');
			var obsDateGe = getString(observation.effectiveDateTimeStart) != null ? 'ge' + observation.effectiveDateTimeStart : '';
			var obsDateLe = getString(observation.effectiveDateTimeEnd) != null ? 'le' + observation.effectiveDateTimeEnd : '';
			var parentGender = patient.gender.filter(i => getGender(i) != undefined).map(i => getGender(i).code).join(',');
			var deviceType = device.type.filter(i => getDeviceType(i) != undefined).map(i => getDeviceType(i).code).join(',');
			var deviceUseStartStartTiming = getString(device.timingPeriodStartStart) != null ? 'ge' + device.timingPeriodStartStart : '';
			var deviceUseStartEndTiming = getString(device.timingPeriodStartEnd) != null ? 'le' + device.timingPeriodStartEnd : '';
			var deviceUseEndStartTiming = getString(device.timingPeriodEndStart) != null ? 'ge' + device.timingPeriodEndStart : '';
			var deviceUseEndEndTiming = getString(device.timingPeriodEndEnd) != null ? 'le' + device.timingPeriodEndEnd : '';

			var queryList = new Array();

			queryList.push('category=OMOP4822320'); // observation category

			if (getString(obsCode)) queryList.push('code=' + obsCode);
			if (getString(obsDateGe)) queryList.push('date=' + obsDateGe);
			if (getString(obsDateLe)) queryList.push('date=' + obsDateLe);
			if (getString(parentGender)) queryList.push('patient.gender=' + parentGender);
			if (getString(deviceType)) queryList.push('device.type=' + deviceType);
			if (getString(deviceUseStartStartTiming)) queryList.push('deviceUseStartTiming=' + deviceUseStartStartTiming);
			if (getString(deviceUseStartEndTiming)) queryList.push('deviceUseStartTiming=' + deviceUseStartEndTiming);
			if (getString(deviceUseEndStartTiming)) queryList.push('deviceUseEndTiming=' + deviceUseEndStartTiming);
			if (getString(deviceUseEndEndTiming)) queryList.push('deviceUseEndTiming=' + deviceUseEndEndTiming);

			queryList.push('_count=' + defalutResourceCount);
			queryList.push('_contained=both');

			queryString = '/Observation?' + queryList.join('&');

			return queryString;
		}

		// Pie Chart 드로잉
		function showPieChart(observationObjArray) {

			var datasets = null;

			if (observationObjArray != null && observationObjArray.length > 0) {

				var maleCount = 0;
				var femaleCount = 0;
				var maleList = [];
				var femaleList = [];
				var labelarray = [];

				observationObjArray.forEach(obs => {
					switch (obs.gender) {
						default:
							break;
						case "male":
							maleList.push(obs.patientId);
							break;
						case "female":
							femaleList.push(obs.patientId);
							break;
					}
				});

				maleCount = new Set(maleList).size;
				femaleCount = new Set(femaleList).size;

				labelarray.push("Male");
				labelarray.push("Female");

				datasets = [{
					label: 'Male / Female',
					data: [maleCount, femaleCount],
					backgroundColor: [colors.red, colors.blue],
					hoverOffset: 4
				}];
			}

			genderChart.data = {
				labels: ['Male', 'Female'],
				datasets: datasets
			};

			genderChart.update();
		}

		// Bar Chart 드로잉
		function showBarChart(observationObjArray) {

			var countArray = [];
			var backgroundColorArray = [];
			var borderColorArray = [];
			var labelArray = [];

			if (observationObjArray != null && observationObjArray.length > 0) {
				if (observation.code != undefined && observation.code != null && observation.code.length > 0) {

					for (let i = 0; i < observation.code.length; i++) {

						var category = getObservationCategory(observation.code[i]);
						var filtered = observationObjArray.filter(f => f.code == category.code);

						if (filtered != undefined && filtered != null && filtered.length > 0) {
							countArray.push(filtered.length);
						} else {
							countArray.push(0);
						}

						labelArray.push(category.name);
						backgroundColorArray.push(backgroundColorValues[i]);
						borderColorArray.push(borderColorValues[i]);
					}
				}
			}

			observationChart.data = {
				labels: labelArray,
				datasets: [{
					data: countArray,
					backgroundColor: backgroundColorArray,
					borderColor: borderColorArray,
					borderWidth: 1
				}]
			};

			observationChart.update();
		}

		// 차트 초기화
		function initChart() {

			genderChart.data = null;
			genderChart.update();

			observationChart.data = null;
			observationChart.update();
		}

		function checkDataCriteria() {
			var retVal = true;

			if (patient.birthdateStart == null || patient.birthdateEnd == null) {
				retVal = false;
			}
			if (patient.gender.length < 1) {
				retVal = false;
			}
			if (observation.effectiveDateTimeStart == null || observation.effectiveDateTimeEnd == null) {
				retVal = false;
			}
			if (observation.code.length < 1) {
				retVal = false;
			}
			if (device.type.length < 1) {
				retVal = false;
			}

			return retVal;
		}
	</script>

</body>

</html>