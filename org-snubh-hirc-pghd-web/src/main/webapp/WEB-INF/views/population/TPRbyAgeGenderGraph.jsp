<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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

	<title>Population Profile Sample</title>

	<!-- Bootstrap core CSS -->
	<link href="<c:url value='/static/css/bootstrap.min.css' />" rel="stylesheet" />
	<!-- Custom styles for this template -->
	<link href="<c:url value='/static/css/common.css' />" rel="stylesheet" />

	<script src="<c:url value='/static/js/jquery.min.js' />"></script>
	<script src="<c:url value='/static/js/jquery-ui.min.js' />"></script>
	<script src="<c:url value='/static/js/bootstrap.min.js' />"></script>

	<script src="<c:url value='/static/js/func.js' />"></script>

	<!-- fhir Client -->
	<script src="<c:url value='/static/js/fhirclient/build/fhir-client.js' />"></script>

	<!-- Chart -->
	<script src="<c:url value='/static/js/chart.js-3.5.1/package/dist/chart.js' />"></script>
	<script src="<c:url value='/static/js/chart.js-3.5.1/chartjs-chart-boxplot.js' />"></script>

	<!-- Loading Spinner-->
	<link href="<c:url value='/static/css/slick-loader.min.css' />" rel="stylesheet"/>
    <script src="<c:url value='/static/js/slick-loader.min.js' />"></script>
</head>

<body ng-app="pghdApp">
	<div class="container">
		<div class="row">
			<div class="col-md-12">
					<label class="col-md-12" style="font-size: 30px; text-align: center;">Distribution of Usage by Demographic</label>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12" style="margin-top: 20px; border: 1px solid skyblue; background-color: white">
				<div class="form-group row-space" style="margin-top: 20px;">
					<label class="col-md-12" style="font-size: 20px; text-align: center;">Group Category</label>
				</div>
				<div class="form-group row-space" style="margin-bottom: 20px;">
					<select id="selGroupCategory" class="form-select " style="width: 100%;">
					</select>
				</div>
			</div>
		</div>
		<div class="row" style="margin-top: 20px; border: 1px solid skyblue; background-color: white">
			<div class="pageContainer">
				<div class="col-xs-12" style="margin-top: 20px; margin-bottom: 20px;">
					<div id="userCount" class="graphRow1">
						<canvas id="chartUserCountAsCategory" class="col-md-12"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row" style="margin-top: 15px; border: 1px solid skyblue; background-color: white">
			<div class="pageContainer">
				<div class="col-xs-12" style="margin-top: 20px; margin-bottom: 20px;">
					<div class="graphRow1">
						<label class="col-md-12" style="font-size: 20px; text-align: center;">Period of Use Distribution</label>
					</div>
					<div class="graphRow1">
						<canvas id="chartUseTermBoxplot" class="col-md-12"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row" style="margin-top: 15px; border: 1px solid skyblue; background-color: white;">
			<div class="pageContainer">
				<div class="col-xs-12" style="margin-top: 20px; margin-bottom: 20px;">
					<div class="graphRow1">
						<label class="col-md-12" style="font-size: 20px; text-align: center;">Record Count Distribution</label>
					</div>
					<div class="graphRow1">
						<canvas id="chartRecordCountBoxplot" class="col-md-12"></canvas>
					</div>
				</div>
			</div>
		</div>
	</div>

	<script type="text/javascript">

		// parent
		const client = parent.client;
		var dataCriteriaPatient = parent.patient;
		var dataCriteriaObservation = parent.observation;
		var dataCriteriaDevice = parent.device;
		var dataCriteriaBundle = parent.bundle;
		var dataCriteriaQuestionaire = parent.questionnaireResponse;
		var defaultResourceCount = parent.defalutResourceCount;

		//Combobox Selected Value
		var selectedDropOffGroup = null;

		var searchResults = null;
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

		//chart option
		var UserCountbasicScales;
		var recordCountbasicScales;
		var UseTermbasicScales;

		// chart
		var usercountChart;
		var useTermBoxplotChart;
		var recordCountBoxplotChart;
		var groupChartLabelArray = [];

		var groupBy = function (xs, key) {
			return xs.reduce(function (rv, x) {
				(rv[x[key]] = rv[x[key]] || []).push(x);
				return rv;
			}, {});
		};

		window.onload = function () {

			setSelectGroup('selGroupCategory');
			selectedDropOffGroup = $('#selGroupCategory option:selected').val();

			setChartScale(selectedDropOffGroup);
			setChart();

			search(dataCriteriaPatient, dataCriteriaObservation, dataCriteriaDevice, dataCriteriaQuestionaire, dataCriteriaBundle);
		}

		$("#selGroupCategory").change(function(){			
			setChartScale(selectedDropOffGroup);
			filteringGroupCount(observationObjArray);
		});	
		
		//chart scale 설정
		function setChartScale(categoryInput){
			var xAxisLabelName = null;
			if(categoryInput =='age'){
				xAxisLabelName = 'Age group';
			}
			else{
				xAxisLabelName = 'Gender';
			}
			this.UserCountbasicScales = {
				x: {
					beginAtZero: true,
					display: true,
					title: {
						display: true,
						text: xAxisLabelName,
						font: {
							size: 20
						},
						padding: { top: 10, left: 0, right: 0, bottom: 0 }
					},
					ticks:{
                        font: {
                            size: 15,
                        }
                    }
				},
				y: {
					beginAtZero: true,
					display: true,
					title: {
						display: true,
						text: 'Number of User',
						font: {
							size: 20
						},
						padding: { top: 10, left: 0, right: 0, bottom: 0 }
					},
					ticks:{
                        font: {
                            size: 15,
                        }
                    }
				}
			};

			this.UseTermbasicScales = {
				x: {
					beginAtZero: true,
					display: true,
					title: {
						display: true,
						text: xAxisLabelName,
						font: {
							size: 20
						},
						padding: { top: 10, left: 0, right: 0, bottom: 0 }
					},
					ticks:{
                        font: {
                            size: 15,
                        }
                    }
				},
				y: {
					beginAtZero: true,
					display: true,
					title: {
						display: true,
						text: 'Period of Use',
						font: {
							size: 20
						},
						padding: { top: 10, left: 0, right: 0, bottom: 0 }
					},
					ticks:{
                        font: {
                            size: 15,
                        }
                    }
				}
			};

			this.recordCountbasicScales = {
				x: {
					beginAtZero: true,
					display: true,
					title: {
						display: true,
						text: xAxisLabelName,
						font: {
							size: 20
						},
						padding: { top: 10, left: 0, right: 0, bottom: 0 }
					},
					ticks:{
                        font: {
                            size: 15,
                        }
                    }
				},
				y: {
					beginAtZero: true,
					display: true,
					title: {
						display: true,
						text: 'Number of Record',
						font: {
							size: 20
						},
						padding: { top: 10, left: 0, right: 0, bottom: 0 }
					},
					ticks:{
                        font: {
                            size: 15,
                        }
                    }
				}
			};
		}

		//Chart 초기설정
		function setChart() {			
			const ctxUsercountChart = document.getElementById("chartUserCountAsCategory").getContext('2d');
			usercountChart = new Chart(ctxUsercountChart, {
				type: 'bar',
				data: {
					datasets: [{
						data: null,
						backgroundColor: backgroundColorValues,
						borderColor: borderColorValues,
						borderWidth: 1
					}]
				},
				options: {
					responsive: true,
					scales: UserCountbasicScales,
					maintainAspectRatio: false,
					plugins: {
						legend: {
							display: false
						}
					}
				}
			});
			const ctxUseTermBoxplotChart = document.getElementById("chartUseTermBoxplot").getContext('2d');
			useTermBoxplotChart = new Chart(ctxUseTermBoxplotChart, {
				type: 'boxplot',
				data: {
					datasets: [{
						data: null,
						backgroundColor: backgroundColorValues,
						borderColor: borderColorValues,
						borderWidth: 1,
						outlierBackgroundColor: borderColorValues
					}]
				},
				options: {
					responsive: true,
					scales: UseTermbasicScales,
					title: {
						display: true,
						text: 'Chart.js Box Plot Chart',
					},
					minStats: 'min',
					maxStats: 'max',
					plugins: {
						legend: {
							display: false
						}
					}
				}
			});
			const ctxRecordCountBoxplotChart = document.getElementById("chartRecordCountBoxplot").getContext('2d');
			recordCountBoxplotChart = new Chart(ctxRecordCountBoxplotChart, {
				type: 'boxplot',
				data: {
					datasets: [{
						data: null,
						backgroundColor: backgroundColorValues,
						borderColor: borderColorValues,
						borderWidth: 1,
						outlierBackgroundColor: borderColorValues
					}]
				},
				options: {
					responsive: true,
					scales: recordCountbasicScales,
					title: {
						display: true,
						text: 'Chart.js Box Plot Chart',
					},
					minStats: 'min',
					maxStats: 'max',
					plugins: {
						legend: {
							display: false
						}
					}
				}
			});
		}

		function search(patient, observation, device, questionnaireResponse, bundle) {

			this.observationObjArray = [];
			dataCriteriaPatient = patient;
			dataCriteriaObservation = observation;
			dataCriteriaDevice = device;
			dataCriteriaBundle = bundle;

			if (!checkDataCriteria(dataCriteriaPatient, dataCriteriaObservation)) {
				initChart();
			} else {
				var queryString = createQueryString();
				var query = client.request(queryString, { flat: true });
				SlickLoader.enable();
				query.then(function (results) {
					SlickLoader.disable();
					setObservationObjArray(results);
					filteringGroupCount(observationObjArray);
				}).catch(error => alert(error));
			}
		}

		function createQueryString() {
			var queryString = null;

			if (dataCriteriaBundle != null && dataCriteriaBundle.id != null && dataCriteriaBundle.id.length > 0) {
				queryString = '/Bundle/' + dataCriteriaBundle.id + '/$readsnapshot';
				return queryString;
			}

			var obsCode = dataCriteriaObservation.code.filter(i => getObservationCategory(i) != undefined).map(i => getObservationCategory(i).code).join(',');
			var obsDateGe = getString(dataCriteriaObservation.effectiveDateTimeStart) != null ? 'ge' + dataCriteriaObservation.effectiveDateTimeStart : '';
			var obsDateLe = getString(dataCriteriaObservation.effectiveDateTimeEnd) != null ? 'le' + dataCriteriaObservation.effectiveDateTimeEnd : '';
			var parentGender = dataCriteriaPatient.gender.filter(i => getGender(i) != undefined).map(i => getGender(i).code).join(',');
			var deviceType = dataCriteriaDevice.type.filter(i => getDeviceType(i) != undefined).map(i => getDeviceType(i).code).join(',');
			var deviceUseStartStartTiming = getString(dataCriteriaDevice.timingPeriodStartStart) != null ? 'ge' + dataCriteriaDevice.timingPeriodStartStart : '';
			var deviceUseStartEndTiming = getString(dataCriteriaDevice.timingPeriodStartEnd) != null ? 'le' + dataCriteriaDevice.timingPeriodStartEnd : '';
			var deviceUseEndStartTiming = getString(dataCriteriaDevice.timingPeriodEndStart) != null ? 'ge' + dataCriteriaDevice.timingPeriodEndStart : '';
			var deviceUseEndEndTiming = getString(dataCriteriaDevice.timingPeriodEndEnd) != null ? 'le' + dataCriteriaDevice.timingPeriodEndEnd : '';

			var queryList = new Array();
			queryList.push('category=OMOP4822320');

			if (getString(obsCode)) queryList.push('code=' + obsCode);
			if (getString(obsDateGe)) queryList.push('date=' + obsDateGe);
			if (getString(obsDateLe)) queryList.push('date=' + obsDateLe);
			if (getString(parentGender)) queryList.push('patient.gender=' + parentGender);
			if (getString(deviceType)) queryList.push('device.type=' + deviceType);
			if (getString(deviceUseStartStartTiming)) queryList.push('deviceUseStartTiming=' + deviceUseStartStartTiming);
			if (getString(deviceUseStartEndTiming)) queryList.push('deviceUseStartTiming=' + deviceUseStartEndTiming);
			if (getString(deviceUseEndStartTiming)) queryList.push('deviceUseEndTiming=' + deviceUseEndStartTiming);
			if (getString(deviceUseEndEndTiming)) queryList.push('deviceUseEndTiming=' + deviceUseEndEndTiming);

			queryList.push('_count=' + defaultResourceCount);
			queryList.push('_contained=both');

			var queryString = '/Observation?' + queryList.join('&');

			return queryString;
		}

		function setObservationObjArray(observations) {
			if (observations != null && observations.length > 0) {

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
			if (dataCriteriaPatient != null && (dataCriteriaPatient.birthdateStart != null || dataCriteriaPatient.birthdateEnd != null)) {
				var returnArray = null;

				if (dataCriteriaPatient.birthdateStart != null && dataCriteriaPatient.birthdateEnd != null) {
					returnArray = observationObjArray.filter(f => f.age >= parseInt(dataCriteriaPatient.birthdateStart) && f.age <= parseInt(dataCriteriaPatient.birthdateEnd));
				} else if (dataCriteriaPatient.birthdateStart != null && dataCriteriaPatient.birthdateEnd === null) {
					returnArray = observationObjArray.filter(f => f.age >= parseInt(dataCriteriaPatient.birthdateStart));
				} else {
					returnArray = observationObjArray.filter(f => f.age <= parseInt(dataCriteriaPatient.birthdateEnd));
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

		function filteringGroupCount(observationObjArray) {
			if (observationObjArray != null && observationObjArray.length > 0) {
				var datasets = new Array();

				// Set Combobox selected
				selectedDropOffGroup = $('#selGroupCategory option:selected').val();
				setChartScale(selectedDropOffGroup);

				//diverge by Group type
				switch (selectedDropOffGroup) {
					case "age":
						groupChartLabelArray = setLabel('age');
						datasets = filteringByAge(observationObjArray, groupChartLabelArray);
						break;
					case "gender":
						groupChartLabelArray = setLabel('gender');
						datasets = filteringByGender(observationObjArray, groupChartLabelArray);
						break;
					default:
						return;
				}
				showUsercountGraph(groupChartLabelArray, datasets);
				showTermBoxplotGraph(groupChartLabelArray, datasets);
				showCountBoxplotGraph(groupChartLabelArray, datasets);
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
				patientObj.recordCount = groupedByPatientId[items].length;

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

			groupChartLabelArray.forEach(ageGroup => {
				var obj = groupChartLabelAndColor(ageGroup);
				dataset = new Object();
				dataset.label = obj.name;
				dataset.borderColor = obj.color;
				dataset.pointRadius = 0;

				dataset.userCountData = patientObjArray.filter(i => i.ageGroup == ageGroup).length;
				dataset.useTermData = patientObjArray.filter(i => i.ageGroup == ageGroup);

				datasets.push(dataset);
			});

			return datasets;
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
						dataset.borderColor = colors.red;
						break;
					case "female":
						dataset.label = "GENDER=FEMALE";
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
					patientObj.gender = items;
					patientObj.period = 1;
					patientObj.recordCount = groupedByPatientId[i].length;

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
				dataset.userCountData = patientObjArray.length;
				dataset.useTermData = patientObjArray;

				dataSets.push(dataset);
			});

			return dataSets;
		}

		function groupChartLabelAndColor(group) {
			var obj = new Object();
			obj.name = '';
			obj.color = null;
			switch (group) {
				case "Minors":
					obj.color = colors.red;
					obj.name = 'AGE_GROUP=Minors';
					break;
				case "20S":
					obj.color = colors.orange;
					obj.name = 'AGE_GROUP=20S';
					break;
				case "30S":
					obj.color = colors.yellow;
					obj.name = 'AGE_GROUP=30S';
					break;
				case "40S":
					obj.color = colors.green;
					obj.name = 'AGE_GROUP=40S';
					break;
				case "50S":
					obj.color = colors.blue;
					obj.name = 'AGE_GROUP=50S';
					break;
				case "60S":
					obj.color = colors.purple;
					obj.name = 'AGE_GROUP=60S';
					break;
				case "Elderly":
					obj.color = colors.cyan;
					obj.name = 'AGE_GROUP=Elderly';
					break;
			}

			return obj;
		}

		function showUsercountGraph(label, results) {
			var data = [];
			results.forEach(element => { data.push(getRecordCount(element)); });
			usercountChart.data.labels = label;
			usercountChart.data.datasets = [{
				data: data,
				backgroundColor: backgroundColorValues,
				borderColor: borderColorValues,
				borderWidth: 1
			}];
			usercountChart.options.scales = UserCountbasicScales;
			usercountChart.update();
		}

		function showTermBoxplotGraph(label, results) {
			var data = [];
			results.forEach(element => { data.push(getBoxplotRecordTerm(element)); });
			useTermBoxplotChart.data.labels = label;
			useTermBoxplotChart.data.datasets = [{
				data: data,
				backgroundColor: backgroundColorValues,
				borderColor: borderColorValues,
				borderWidth: 1
			}];
			useTermBoxplotChart.options.scales = UseTermbasicScales;
			useTermBoxplotChart.update();
		}

		function showCountBoxplotGraph(label, results) {
			var data = [];
			results.forEach(element => { data.push(getBoxplotRecordCount(element)); });
			recordCountBoxplotChart.data.labels = label;
			recordCountBoxplotChart.data.datasets = [{
				data: data,
				backgroundColor: backgroundColorValues,
				borderColor: borderColorValues,
				borderWidth: 1
			}];
			recordCountBoxplotChart.options.scales = recordCountbasicScales;
			recordCountBoxplotChart.update();
		}

		function getRecordCount(dataset) {
			var returnValue
			if (dataset != null) {
				return returnValue = dataset.userCountData;
			} else {
				return null;
			}
		}

		function getBoxplotRecordTerm(dataset) {
			var valueArr = [];
			if (dataset != null) {
				for (var i = 0; i < dataset.useTermData.length; i++) {
					valueArr.push(dataset.useTermData[i].period);
				}
				return valueArr.sort(function (a, b) { return a - b; });
			} else {
				return null;
			}
		}

		function getBoxplotRecordCount(dataset) {
			var valueArr = [];
			if (dataset != null) {
				for (var i = 0; i < dataset.useTermData.length; i++) {
					valueArr.push(dataset.useTermData[i].recordCount);
				}
				return valueArr.sort(function (a, b) { return a - b; });
			} else {
				return null;
			}
		}

		function checkDataCriteria(patient, observation) {
			var returnBoolean = true;

			if (patient.gender.length < 1) {
				returnBoolean = false;
			}

			if (observation.effectiveDateTimeStart == null || observation.effectiveDateTimeEnd == null) {
				returnBoolean = false;
			}

			if (observation.code.length < 1) {
				returnBoolean = false;
			}
			return returnBoolean;
		}

		function setLabel(category) {
			var labels = new Array();
			if (category == 'age') {
				labels.push('<20');
				labels.push('20S');
				labels.push('30S');
				labels.push('40S');
				labels.push('50S');
				labels.push('60S');
				labels.push('≥70');
			}
			else {
				labels.push('Male');
				labels.push('Female');
			}
			return labels;
		}

		function initChart() {
			usercountChart.data = null;
			usercountChart.update();

			useTermBoxplotChart.data = null;
			useTermBoxplotChart.update();

			recordCountBoxplotChart.data = null;
			recordCountBoxplotChart.update();
		}

	</script>
</body>

</html>