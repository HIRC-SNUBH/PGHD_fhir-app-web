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

	<title>Drop-off Curve</title>

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

	<!-- Loading Spinner-->
	<link href="<c:url value='/static/css/slick-loader.min.css' />" rel="stylesheet" />
	<script src="<c:url value='/static/js/slick-loader.min.js' />"></script>
</head>

<body>
	<div class="container">
		<div class="row">
			<div class="col-md-12">
				<label class="col-md-12" style="font-size: 30px; text-align: center;">Drop out Rate</label>
			</div>
		</div>
		<div class="row" class="col-md-12" style="margin-top: 20px;">
			<div class="col-xs-4" style="border: 1px solid skyblue; background-color: white">
				<div class="form-group row-space" style="margin-top: 10px;">
					<label class="col-md-12" style="font-size: 20px; text-align: center;">Minimum Period of Use</label>
				</div>
				<div class="form-group row-space">
					<input type="number" min="" max="" id="obsPeriod" name="dataCriteria" style="width:100%;">
				</div>
			</div>
			<div class="col-xs-8" style="border: 1px solid skyblue; background-color: white">
				<div class="form-group row-space" style="margin-top: 10px;">
					<label class="col-md-12" style="font-size: 20px; text-align: center;">X-axis Range</label>
				</div>
				<div class="form-group row-space">
					<input type="number" min="" max="" id="inputUseTermBegin" name="dataCriteria" style="width:47%;"> To
					<input type="number" min="" max="" id="inputUseTermEnd" name="dataCriteria" style="width:47%;">
				</div>
			</div>
		</div>
		<div class="row" style="margin-top: 20px; border: 1px solid skyblue; background-color: white;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1" style="margin-top: 20px;">
						<label class="col-md-12" style="font-size: 20px; text-align: center;">Drop out Rate by Period of Use</label>
					</div>
					<div class="graphRow1">
						<canvas id="chartStrata" class="col-md-12" style="margin-bottom: 20px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12" style="margin-top: 20px; border: 1px solid skyblue; background-color: white">
				<div class="form-group row-space" style="margin-top: 10px;">
					<label class="col-md-12" style="font-size: 20px; text-align: center;">Group Category</label>
				</div>
				<div class="form-group row-space">
					<select id="selectGroup" class="customSelect">
					</select>
				</div>
			</div>
		</div>
		<div class="row" style="margin-top: 20px; border: 1px solid skyblue; background-color: white;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1" style="margin-top: 20px;">
						<label class="col-md-12" style="font-size: 20px; text-align: center;">Drop out Rate by Period of Use & Group</label>
					</div>
					<div class="graphRow1">
						<canvas id="chartGroup" class="col-md-12" style="margin-bottom: 20px;"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row" style="margin-top: 20px; border: 1px solid skyblue; background-color: white;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div class="graphRow1" style="margin-top: 20px;">
						<label class="col-md-12" style="font-size: 20px; text-align: center;">Drop out Rate by Period of Use & Life-log Items</label>
					</div>
					<div class="graphRow1">
						<canvas id="chartObservation" class="col-md-12" style="margin-bottom: 20px;"></canvas>
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

		// Combobox Selected value
		var selectedDropOffGroup = null;

		// observation period value
		var observationPeriodVal = null;

		// input value
		var useTermBeginVal = null;
		var useTermEndVal = null;

		//chart 
		var strataChart;
		var groupChart;
		var observationChart;
		var strataChartLabelArray = [];
		var groupChartLabelArray = [];
		var observationChartLabelArray = [];

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

			this.client = parent.client;
			this.patient = parent.patient;
			this.observation = parent.observation;
			this.device = parent.device;
			this.defalutResourceCount = parent.defalutResourceCount;
			this.bundle = parent.bundle;

			setSelectGroup('selectGroup');

			$('#obsPeriod').val(1);
			$('#obsPeriod').attr('min', 1);
			$('#obsPeriod').attr('max', 1);

			observationPeriodVal = $('#obsPeriod').val();
			selectedDropOffGroup = $('#selectGroup option:selected').val();

			setChart();

			search(patient, observation, device, questionnaireResponse, bundle);
		}

		//chart 초기설정
		function setChart() {

			var scales = {
				x: {
					beginAtZero: true,
					display: true,
					title: {
						display: true,
						text: 'Period of Use',
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
						text: 'User Ratio',
						font: {
							size: 20
						},
						padding: { top: 10, left: 0, right: 0, bottom: 0 }
					}
				}
			};

			var ctxStrataChart = document.getElementById("chartStrata").getContext('2d');
			var strataChartOptions = {
				responsive: true,
				scales: scales,
				plugins: {
					legend: {
						display: false
						//position: 'top',
					}
				}
			};

			strataChart = new Chart(ctxStrataChart, {
				type: 'line',
				data: {
					datasets: [{
						label: 'User Count',
						data: null,						
						borderColor: colors.red,
						pointRadius: 0
					}]
				},
				options: strataChartOptions
			});

			var ctxGroupChart = document.getElementById("chartGroup").getContext('2d');
			var groupChartOptions = {
				responsive: true,
				scales: scales,				
				plugins: {
					legend: {
						position: 'right',
					}
				},
				stepped: true
			};
			groupChart = new Chart(ctxGroupChart, {
				type: 'line',
				data: null,
				options: groupChartOptions
			});

			var ctxObservationChart = document.getElementById("chartObservation").getContext('2d');
			var observationChartOptions = {
				responsive: true,
				scales: scales,
				plugins: {
					legend: {
						position: 'right',
					}
				},
				stepped: true
			};
			observationChart = new Chart(ctxObservationChart, {
				type: 'line',
				data: null,
				options: observationChartOptions
			});

		}

		function search(patient, observation, device, questionnaireResponse, bundle) {

			this.observationObjArray = [];
			this.patient = patient;
			this.observation = observation;
			this.device = device;
			this.questionnaireResponse = questionnaireResponse;
			this.bundle = bundle;

			setUseTerm();

			if (!checkDataCriteria()) {
				initChart();
			} else {
				var queryString = createQueryString();
				var query = client.request(queryString, { flat: true });
				SlickLoader.enable();
				query.then(function (results) {
					SlickLoader.disable();
					setObservationObjArray(results);
					filteringUserCountByPeriod(observationObjArray);
					filteringObservationCount(observationObjArray);
					filteringGroupCount(observationObjArray);
				}).catch(error => alert(error));
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

		function setObservationObjArray(observations) {
			if (observations != null && observations.length > 0) {

				//observationObjArray = new Array();

				observations.forEach(observation => {
					var observationObj = new Object();

					observationObj.date = null;
					observationObj.patientId = null;
					observationObj.birthDate = null;
					observationObj.gender = null;
					observationObj.age = null;
					observationObj.code = null;
					observationObj.display = null;

					observationObj.date = getFormatDate(new Date(observation.effectiveDateTime));
					observationObj.patientId = observation.subject.reference;

					if (observation.contained != undefined && observation.contained != null) {
						var patientResource = observation.contained.find(i => i.resourceType == "Patient");
						if (patientResource != undefined && patientResource != null) {
							observationObj.birthDate = patientResource.birthDate;
							observationObj.gender = patientResource.gender;
							observationObj.age = new Date(observation.effectiveDateTime).getFullYear() - new Date(patientResource.birthDate).getFullYear() + 1
						}
					}

					if (observation.code != null && observation.code.coding != null && observation.code.coding.length > 0) {
						observationObj.code = observation.code.coding[0].code;
						observationObj.display = observation.code.coding[0].display
					}

					observationObjArray.push(observationObj);
				});

				observationObjArray = filteredByAge(observationObjArray);
				//날짜별 내림차순 정렬
				observationObjArray.sort(date_descending);
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

		function filteringUserCountByPeriod(observationObjArray) {

			if (observationObjArray != null && observationObjArray.length > 0) {
				// validate period
				if (!checkPeriodValue()) {
					return;
				}

				// group by patient ID
				var groupedObservations = groupBy(observationObjArray, 'patientId');
				var patientObj = null;
				var patientObjArray = new Array();

				Object.keys(groupedObservations).forEach(items => {

					patientObj = new Object();
					patientObj.patientId = items;
					patientObj.dateList = [];
					patientObj.period = 1;

					groupedObservations[items].sort(date_descending); // 내림차순 정렬
					groupedObservations[items].forEach(item => {
						patientObj.dateList.push(item.date);
					})

					var dateSet = new Set(patientObj.dateList);
					patientObj.dateList = Array.from(dateSet);

					if (patientObj.dateList.length > 1) {
						var dayBegin = new Date(patientObj.dateList[0]);
						var dayEnd = new Date(patientObj.dateList[patientObj.dateList.length - 1]);

						var diff = dayEnd - dayBegin;
						var cDay = 24 * 60 * 60 * 1000; // hour * minute * second * millisecond

						patientObj.period = parseInt(diff / cDay) + 1;
					}
					patientObjArray.push(patientObj);
				});

				// x-axis Label
				var xAxisArray = setXAxisLabels();
				var useTermArray = setUseTermArray();
				var wholeYValueArray = [];
				var yValueArray = [];
				strataChartLabelArray = xAxisArray;

				var xPeriod = observationPeriodVal != null && parseInt(observationPeriodVal) > 1 ? parseInt(observationPeriodVal) : 1;
				for(var k = 1 ; k < xPeriod ; k++){
					wholeYValueArray.push(1);
				}

				if (useTermArray.length > 0) {

					var maxValue = patientObjArray.filter(i => parseInt(i.period) >= useTermArray[0]).length;

					useTermArray.forEach(item => {
						var filtered = patientObjArray.filter(i => i.period >= item);
						if (filtered != undefined && filtered != null && filtered.length > 0) {
							wholeYValueArray.push(filtered.length / maxValue);
						} else {
							wholeYValueArray.push(0);
						}
					});
					for(var i = 0 ; i < xAxisArray.length ; i++){
						yValueArray.push(wholeYValueArray[parseInt(xAxisArray[i])-1]);
					}
					showUserCountGraph(strataChartLabelArray, yValueArray);
				}
			}
		}

		function filteringObservationCount(observationObjArray) {
			if (observationObjArray != null && observationObjArray.length > 0) {
				//group by Observation.code
				var groupByCode = groupBy(observationObjArray, 'code');

				var dataset = null;
				var datasets = new Array();

				observationChartLabelArray = setXAxisLabels();

				Object.keys(groupByCode).forEach(items => { // data

					var obj = getObservationChartLabelAndColor(items);

					dataset = new Object();
					dataset.label = obj.name;
					dataset.backgroundColor = obj.color; // 추가 부분
					dataset.borderColor = obj.color;
					dataset.pointRadius = 0;

					var groupByPatientId = groupBy(groupByCode[items], 'patientId');

					var filterdObj = null;
					var filterdObjArray = new Array();

					Object.keys(groupByPatientId).forEach(i => { //환자별 Period 계산
						filterdObj = new Object();
						filterdObj.patientId = i;
						filterdObj.period = 1;

						var dateList = [];

						groupByPatientId[i].sort(date_descending); // 내림차순 정렬
						groupByPatientId[i].forEach(item => {
							dateList.push(item.date);
						});

						if (dateList.length > 1) {
							var dayBegin = new Date(dateList[0]);
							var dayEnd = new Date(dateList[dateList.length - 1]);

							var diff = dayEnd - dayBegin;
							var cDay = 24 * 60 * 60 * 1000; // hour * minute * second * millisecond

							//Period 계산
							filterdObj.period = parseInt(diff / cDay) + 1;
						}
						filterdObjArray.push(filterdObj);
					});

					//Graph 데이터 생성
					var yAxisArray = [];

					if (filterdObjArray.length > 0) {

						var maxValue = filterdObjArray.filter(i => parseInt(i.period) >= parseInt(observationChartLabelArray[0])).length;

						observationChartLabelArray.forEach(item => {
							var filtered = filterdObjArray.filter(i => parseInt(i.period) >= parseInt(item));
							if (filtered != undefined && filtered != null && filtered.length > 0) {
								yAxisArray.push(filtered.length / maxValue);
							} else {
								yAxisArray.push(0);
							}
						});

						dataset.data = yAxisArray;
					}
					datasets.push(dataset);

				});
				showObservationCountGraph(observationChartLabelArray, datasets);
			}
		}

		function filteringGroupCount(observationObjArray) {
			if (observationObjArray != null && observationObjArray.length > 0) {
				var datasets = new Array();

				groupChartLabelArray = setXAxisLabels();

				// Set Combobox selected
				selectedDropOffGroup = $('#selectGroup option:selected').val();

				//diverge by Group type
				switch (selectedDropOffGroup) {
					case "age":
						datasets = filteringByAge(observationObjArray, groupChartLabelArray);
						break;
					case "gender":
						datasets = filteringByGender(observationObjArray, groupChartLabelArray);
						break;
					default:
						return;
				}
				showGroupGraph(groupChartLabelArray, datasets);
			}
		}

		function filteringByAge(observationObjArray, groupChartLabelArray) {

			var datasets = new Array();
			var dataset = null;

			var patientObj = null;
			var patientObjArray = new Array();

			var groupedByPatientId = groupBy(observationObjArray, 'patientId');

			Object.keys(groupedByPatientId).forEach(items => {

				patientObj = new Object();
				patientObj.period = 1;
				patientObj.age = null;
				patientObj.ageGroup = null;
				patientObj.birthDate = groupedByPatientId[items].find(f => f.birthDate != null).birthDate;

				groupedByPatientId[items].sort(date_descending);

				var dateList = [];
				groupedByPatientId[items].forEach(i2 => {
					dateList.push(i2.date);
				});

				var dayBegin = new Date(dateList[0]);
				var dayEnd = new Date(dateList[dateList.length - 1]);

				var cDay = 24 * 60 * 60 * 1000; // hour * minute * second * millisecond				

				var diffPeriod = dayEnd - dayBegin;

				patientObj.period = parseInt(diffPeriod / cDay) + 1;

				if (patientObj.birthDate != null) {
					var birthYear = new Date(patientObj.birthDate).getFullYear();
					patientObj.age = dayBegin.getFullYear() - birthYear + 1;
					patientObj.ageGroup = getAgeGroup(patientObj.age);
				}
				patientObjArray.push(patientObj);
			})

			var ageGroupArray = ["Minors", "20S", "30S", "40S", "50S", "60S", "Elderly"];

			ageGroupArray.forEach(ageGroup => {
				var obj = groupChartLabelAndColor(ageGroup);
				dataset = new Object();
				dataset.label = obj.name;				
				dataset.backgroundColor = obj.color; // 추가 부분
				dataset.borderColor = obj.color;
				dataset.pointRadius = 0;

				var yAxisArray = [];

				var maxValue = patientObjArray.filter(i => i.ageGroup == ageGroup && parseInt(i.period) >= parseInt(groupChartLabelArray[0])).length;

				groupChartLabelArray.forEach(item => {
					var filtered = patientObjArray.filter(i => i.ageGroup == ageGroup && parseInt(i.period) >= parseInt(item));
					if (filtered != undefined && filtered != null && filtered.length > 0) {
						yAxisArray.push(filtered.length / maxValue);
					} else {
						yAxisArray.push(0);
					}
				});

				dataset.data = yAxisArray;
				datasets.push(dataset);
			});

			return datasets;
		}

		function getAgeGroup(age) {
			var ageGroup = null;

			if (age < 20) {
				ageGroup = "Minor";
			}
			if (age >= 20 && age <= 29) {
				ageGroup = "20S";
			}
			if (age >= 30 && age <= 39) {
				ageGroup = "30S";
			}
			if (age >= 40 && age <= 49) {
				ageGroup = "40S";
			}
			if (age >= 50 && age <= 59) {
				ageGroup = "50S";
			}
			if (age >= 60 && age <= 69) {
				ageGroup = "60S";
			}
			if (age >= 70) {
				ageGroup = "Elderly";
			}

			return ageGroup;
		}

		function filteringByGender(observationObjArray, groupChartLabelArray) {

			var dataSets = new Array();
			var dataset = null;

			var groupedObservations = groupBy(observationObjArray, 'gender');

			Object.keys(groupedObservations).forEach(items => { // data set

				dataset = new Object();
				dataset.pointRadius = 0;
				switch (items) {
					case "male":
						dataset.label = "GENDER=MALE";
						dataset.backgroundColor = colors.red; //추가 부분
						dataset.borderColor = colors.red;
						break;
					case "female":
						dataset.label = "GENDER=FEMALE";
						dataset.backgroundColor = colors.blue; //추가 부분
						dataset.borderColor = colors.blue;
						break;
					default:
						break;
				}

				var groupedByPatientId = groupBy(groupedObservations[items], 'patientId'); //환자별 Group

				var patientObj = null;
				var patientObjArray = new Array();

				Object.keys(groupedByPatientId).forEach(i => {

					patientObj = new Object();
					patientObj.period = 1;

					groupedByPatientId[i].sort(date_descending);

					var dateList = [];
					groupedByPatientId[i].forEach(i2 => {
						dateList.push(i2.date);
					});

					if (dateList.length > 1) {
						var dayBegin = new Date(dateList[0]);
						var dayEnd = new Date(dateList[dateList.length - 1]);

						var diff = dayEnd - dayBegin;
						var cDay = 24 * 60 * 60 * 1000; // hour * minute * second * millisecond

						patientObj.period = parseInt(diff / cDay) + 1;
					}

					patientObjArray.push(patientObj);
				});

				var yAxisArray = [];

				var maxValue = patientObjArray.filter(i => parseInt(i.period) >= parseInt(groupChartLabelArray)).length;

				groupChartLabelArray.forEach(item => {
					var filtered = patientObjArray.filter(i => parseInt(i.period) >= parseInt(item));
					if (filtered != undefined && filtered != null && filtered.length > 0) {
						yAxisArray.push(filtered.length / maxValue);
					} else {
						yAxisArray.push(0);
					}
				});

				dataset.data = yAxisArray;
				dataSets.push(dataset);
			});

			return dataSets;
		}


		// ** Labels **
		function setXAxisLabels() {
			var labelArray = [];
			var xBegin = useTermBeginVal != null && parseInt(useTermBeginVal) > 1 ? parseInt(useTermBeginVal) : 1;
			var xEnd =  useTermEndVal != null && parseInt(useTermEndVal) > 1 ? parseInt(useTermEndVal) : 1;
			for (xBegin; xBegin <= xEnd; xBegin++) {
				labelArray.push(xBegin);
			}
			return labelArray;
		}

		function setUseTermArray() {
			var labelArray = [];
			var xBegin = observationPeriodVal != null && parseInt(observationPeriodVal) > 1 ? parseInt(observationPeriodVal) : 1;
			var xEnd =  useTermEndVal != null && parseInt(useTermEndVal) > 1 ? parseInt(useTermEndVal) : 1;
			for (xBegin; xBegin <= xEnd; xBegin++) {
				labelArray.push(xBegin);
			}
			return labelArray;
		}

		function getObservationChartLabelAndColor(code) {

			var obj = new Object();
			obj.name = '';
			obj.color = null;
			switch (code) {
				case "997671000000106":
					obj.color = colors.red;
					obj.name = 'Blood glucose level';
					break;
				case "27113001":
					obj.color = colors.orange;
					obj.name = 'Body weight';
					break;
				case "248263006":
					obj.color = colors.yellow;
					obj.name = 'Duration of sleep';
					break;
				case "55411-3":
					obj.color = colors.green;
					obj.name = 'Exercise duration';
					break;
				case "74008-4":
					obj.color = colors.blue;
					obj.name = 'Exercise intensity';
					break;
				case "248646004":
					obj.color = colors.purple;
					obj.name = 'Heart beat';
					break;
				case "248334005":
					obj.color = colors.gray;
					obj.name = 'Length of body';
					break;
				case "405052004":
					obj.color = colors.lime;
					obj.name = 'Level of stress';
					break;
				case "55423-8":
					obj.color = colors.cyan;
					obj.name = 'Number of steps in unspecifed time Pedometer';
					break;
				case "924481000000109":
					obj.color = colors.silver;
					obj.name = 'Seif-monitoring of blood pressure';
					break;
				case "SNUBH0046":
					obj.color = colors.navy;
					obj.name = 'Sleep satisfaction score';
					break;
			}

			return obj;
		}

		function groupChartLabelAndColor(group) {
			var obj = new Object();
			obj.name = '';
			obj.color = null;
			switch (group) {
				case "Minors":
					obj.color = colors.red;
					obj.name = '<20';
					break;
				case "20S":
					obj.color = colors.orange;
					obj.name = '20S';
					break;
				case "30S":
					obj.color = colors.yellow;
					obj.name = '30S';
					break;
				case "40S":
					obj.color = colors.green;
					obj.name = '40S';
					break;
				case "50S":
					obj.color = colors.blue;
					obj.name = '50S';
					break;
				case "60S":
					obj.color = colors.purple;
					obj.name = '60S';
					break;
				case "Elderly":
					obj.color = colors.cyan;
					obj.name = '≥70';
					break;
			}

			return obj;
		}
		// ** Labels **

		// ** Graph **
		function showUserCountGraph(labelArray, yAxisArray) {

			strataChart.data = {
				labels: labelArray,
				datasets: [{
					label: 'User Count By Period',
					data: yAxisArray,
					borderColor: colors.red,
					pointRadius: 0
				}]
			};

			strataChart.update();
		}

		function showObservationCountGraph(labelArray, datasets) {

			observationChart.data = {
				labels: labelArray,
				datasets: datasets
			};

			observationChart.update();
		}

		function showGroupGraph(labelArray, datasets) {

			groupChart.data = {
				labels: labelArray,
				datasets: datasets
			};

			groupChart.update();
		}

		//차트 초기화
		function initChart() {
			strataChart.data = null;
			strataChart.update();

			observationChart.data = null;
			observationChart.update();

			groupChart.data = null;
			groupChart.update();
		}

		// ** Graph **		

		// ** UI **
		// Use Term 값 설정
		function setUseTerm() {

			if (observation != undefined && observation != null) {

				var dayBegin = new Date(observation.effectiveDateTimeStart);
				var dayEnd = new Date(observation.effectiveDateTimeEnd);

				var diff = dayEnd - dayBegin;
				var cDay = 24 * 60 * 60 * 1000; // hour * minute * second * millisecond

				var useTermMin = 1;
				var useTermMax = parseInt(diff / cDay) + 1;

				$('#inputUseTermBegin').val(useTermMin);
				$('#inputUseTermBegin').attr('min', useTermMin);
				$('#inputUseTermBegin').attr('max', useTermMax - 1);

				$('#inputUseTermEnd').val(useTermMax);
				$('#inputUseTermEnd').attr('min', useTermMin);
				$('#inputUseTermEnd').attr('max', useTermMax);
			}
		}

		function checkPeriodValue() {
			var result = false;

			useTermBeginVal = $('#inputUseTermBegin').val();
			useTermEndVal = $('#inputUseTermEnd').val();

			if (useTermBeginVal != null && useTermEndVal != null) {
				if (parseInt(useTermEndVal) >= parseInt(useTermBeginVal)) {
					result = true;
				}
			}

			return result;
		}

		$("#inputUseTermBegin").change(function () {
			filteringUserCountByPeriod(observationObjArray);
			filteringObservationCount(observationObjArray);
			filteringGroupCount(observationObjArray);
		});

		$("#inputUseTermEnd").change(function () {
			filteringUserCountByPeriod(observationObjArray);
			filteringObservationCount(observationObjArray);
			filteringGroupCount(observationObjArray);
		});

		$("#selectGroup").change(function () {
			filteringGroupCount(observationObjArray);
		});

		$("#obsPeriod").change(function () {
			observationPeriodVal = $("#obsPeriod").val();
			filteringUserCountByPeriod(observationObjArray);
			filteringGroupCount(observationObjArray);
			filteringObservationCount(observationObjArray);
		});

		// ** UI **	
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