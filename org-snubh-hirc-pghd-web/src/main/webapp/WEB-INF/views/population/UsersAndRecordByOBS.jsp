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

	<title># of USERS & RECORDS by OBS</title>

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
                <label class="col-md-12" style="font-size: 30px; text-align: center;">Distribution of Usage by Life-log Items</label>
            </div>
        </div>
		<div class="row">
			<div class="col-md-12" style="margin-top: 20px; border: 1px solid skyblue; background-color: white">
				<div class=" graphRow1" style="margin-top: 10px;">
					<label class="col-md-12 style=" style="font-size: 20px; text-align: center;">Observation Category</label>
				</div>
				<div class="form-group row-space">
					<select id="select1" class="customSelect" name="OBSERVATION_CATEGORY">
					</select>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-md-12" style="margin-top: 20px; border: 1px solid skyblue; background-color: white">
				<div class=" graphRow1" style="margin-top: 10px;">
					<label class="col-md-12" style="font-size: 20px; text-align: center;">Date Format</label>
				</div>
				<div class=" form-group row-space">
					<select id="select2" class="customSelect">
					</select>
				</div>
			</div>
		</div>
		<div class="row" style="margin-top: 20px; border: 1px solid skyblue; background-color: white">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div id="recordCountGraph" class="graphRow1">
						<canvas id="chartUsercount" class="col-md-12" style="margin-top: 30px; margin-bottom: 30px"></canvas>
					</div>
				</div>
			</div>
		</div>
		<div class="row" style="margin-top: 20px; border: 1px solid skyblue; background-color: white;">
			<div class="pageContainer">
				<div class="col-xs-12">
					<div id="recordValueGraph" class="graphRow1">
						<canvas id="chartRecordCount" class="col-md-12" style="margin-top: 30px; margin-bottom: 30px"></canvas>
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
		var selectedObservationCategory = null;
		var selectedPeriodUnit = null;

		//chart
		var usercountChart;
		var recordCountChart;
		// chart label
		var userCountLabelArray = [];
		var recordCountLableArray = [];

		var userTotalCount = 0;
		var recordTotalCount = 0;

		var yearCount;
		var monthCount;
		var weekCount;
		var dayCount;

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
			this.questionnaireResponse = parent.questionnaireResponse;
			this.defalutResourceCount = parent.defalutResourceCount;
			this.bundle = parent.bundle;

			setChart();
			setSelectTime('select2');

			search(patient, observation, device, questionnaireResponse, bundle);
		}

		function setChart() {

			var usercountChartScale = {
				x: {
					beginAtZero: true,
					display: true,
					title: {
						display: true,
						text: 'Observation Date',
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
						text: 'Number of User',
						font: {
							size: 20
						},
						padding: { top: 10, left: 0, right: 0, bottom: 0 }
					}
				}
			};

			var ctxUsercountChart = document.getElementById("chartUsercount").getContext('2d');
			usercountChart = new Chart(ctxUsercountChart, {
				type: 'bar',
				data: {
					datasets: [{
						label: 'User Count',
						data: null,
						backgroundColor: colors2.blue,
						borderColor: colors.blue,
						borderWidth: 1
					}]
				},
				options: {
					scales: usercountChartScale,
					plugins: {
						legend: {
							display: false,
						},
						tooltip: {
							callbacks: {
								label: function (tooltipItem) {
									var countVal = tooltipItem.dataset.data[tooltipItem.dataIndex];
									var ratioVal = ((countVal / userTotalCount)) * 100;
									return '[User Count : ' + countVal + '] [Ratio : ' + ratioVal + '%]';
								}
							},
						}
					}
				}
			});

			var recordCountChartScale = {
				x: {
					beginAtZero: true,
					display: true,
					title: {
						display: true,
						text: 'Observation Date',
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
						text: 'Number of Record',
						font: {
							size: 20
						},
						padding: { top: 10, left: 0, right: 0, bottom: 0 }
					}
				}
			};

			var ctxRecordCountChart = document.getElementById("chartRecordCount").getContext('2d');
			recordCountChart = new Chart(ctxRecordCountChart, {
				type: 'bar',
				data: {
					datasets: [{
						label: 'Record',
						data: null,
						backgroundColor: colors2.red,
						borderColor: colors.red,
						borderWidth: 1
					}]
				},
				options: {
					scales: recordCountChartScale,
					plugins: {
						legend: {
							display: false,
						},
						tooltip: {
							callbacks: {
								label: function (tooltipItem) {
									var countVal = tooltipItem.dataset.data[tooltipItem.dataIndex];
									var ratioVal = ((countVal / recordTotalCount)) * 100;
									return '[Record Count : ' + countVal + '] [Ratio : ' + ratioVal + '%]';
								}
							},
						}
					}
				}
			});
		}

		function search(patient, observation, device, questionnaireResponse, bundle) {

			this.observationObjArray = [];
			this.patient = patient;
			this.observation = observation;
			this.device = device;
			this.questionnaireResponse = questionnaireResponse;
			this.bundle = bundle;

			setSelectObservationCategory('select1', observation);

			if (!checkDataCriteria()) {
				initChart();
			} else {
				var queryString = createQueryString();
				var query = client.request(queryString, { flat: true });
				SlickLoader.enable();
				query.then(function (results) {
					SlickLoader.disable();
					setObservationObjArray(results);
					filteringUserCount(observationObjArray);
					filteringObservationCount(observationObjArray);
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

		function filteringUserCount(observationObjArray) {

			if (observationObjArray != null && observationObjArray.length > 0) {
				// filtering by observation.code
				var filteredObservations = [];				

				selectedObservationCategory = $('#select1 option:selected').val();
				selectedPeriodUnit = $('#select2 option:selected').val();

				if (selectedObservationCategory != 'all') {
					filteredObservations = observationObjArray.filter(i => i.code == selectedObservationCategory);
				} else {
					filteredObservations = observationObjArray;
				}

				//일자별 group				
				var groupedObservations = groupBy(filteredObservations, 'date');
				var userCountObj = null;
				var userCountObjArray = new Array();

				//일자별 환자 리스트 생성				
				Object.keys(groupedObservations).forEach(items => {

					userCountObj = new Object();
					userCountObj.patientList = [];
					userCountObj.date = getFormatDate(new Date(items));

					groupedObservations[items].forEach(item => {
						userCountObj.patientList.push(item.patientId);
					})

					var patientIdSet = new Set(userCountObj.patientList);
					userCountObj.patientList = patientIdSet;
					userCountObjArray.push(userCountObj);
				});

				// x-axis Label
				userCountLabelArray = setGraphLabels(observation);

				var yAxisArray = [];
				var totalUserCountVal = 0;

				if (userCountLabelArray.length > 0) {
					switch (selectedPeriodUnit) {
						case "0": //yearly
						default:
							userCountLabelArray.forEach(item => {
								var filtered = userCountObjArray.filter(i => new Date(i.date).getFullYear() == item);
								if (filtered != undefined && filtered != null && filtered.length > 0) {
									var newArray = [];
									filtered.forEach(i => {
										if (i.patientList.size > 0) {
											var arr = Array.from(i.patientList);
											arr.forEach(id => newArray.push(id));
										}
									});
									var newSet = new Set(newArray);
									yAxisArray.push(newSet.size);
									totalUserCountVal += newSet.size;
								} else {
									yAxisArray.push(0);
								}
							});
							break;
						case "1": //monthly
							userCountLabelArray.forEach(item => {
								var year = item.substr(0, 4);
								var month = item.substr(4, 2);

								var filtered = userCountObjArray.filter(i => new Date(i.date).getFullYear() == year &&
									("0" + (new Date(i.date).getMonth() + 1)).slice(-2) == month);
								if (filtered != undefined && filtered != null && filtered.length > 0) {
									var newArray = [];
									filtered.forEach(i => {
										if (i.patientList.size > 0) {
											var arr = Array.from(i.patientList);
											arr.forEach(id => newArray.push(id));
										}
									});
									var newSet = new Set(newArray);
									yAxisArray.push(newSet.size);
									totalUserCountVal += newSet.size;
								} else {
									yAxisArray.push(0);
								}
							});
							break;
						case "2": //weekly
							userCountLabelArray.forEach(item => {
								var year = item.substr(0, 4);
								var week = item.substr(4, 2);

								var filtered = userCountObjArray.filter(i => new Date(i.date).getFullYear() == year &&
									("0" + (new Date(i.date).getWeek())).slice(-2) == week);
								if (filtered != undefined && filtered != null && filtered.length > 0) {
									var newArray = [];
									filtered.forEach(i => {
										if (i.patientList.size > 0) {
											var arr = Array.from(i.patientList);
											arr.forEach(id => newArray.push(id));
										}
									});
									var newSet = new Set(newArray);
									yAxisArray.push(newSet.size);
									totalUserCountVal += newSet.size;
								} else {
									yAxisArray.push(0);
								}
							});
							break;
						case "3": //daily
							userCountLabelArray.forEach(item => {
								var date1 = new Date(item);
								var filtered = userCountObjArray.filter(i => getFormatDate(new Date(i.date)) == getFormatDate(date1));
								if (filtered != undefined && filtered != null && filtered.length > 0) {
									var newArray = [];
									filtered.forEach(i => {
										if (i.patientList.size > 0) {
											var arr = Array.from(i.patientList);
											arr.forEach(id => newArray.push(id));
										}
									});
									var newSet = new Set(newArray);
									yAxisArray.push(newSet.size);
									totalUserCountVal += newSet.size;
								} else {
									yAxisArray.push(0);
								}
							});
							break;
					}
					this.userTotalCount = totalUserCountVal;
				}
				showUserCountGraph(userCountLabelArray, yAxisArray);
			} else { // data length = 0				
				userCountLabelArray = setGraphLabels(observation);
				showUserCountGraph(userCountLabelArray, null);
			}
		}

		function filteringObservationCount(observationObjArray) {
			if (observationObjArray != null && observationObjArray.length > 0) {
				// filtering by observation.code
				var filteredObservations = [];

				selectedObservationCategory = $('#select1 option:selected').val();
				selectedPeriodUnit = $('#select2 option:selected').val();

				if (selectedObservationCategory != 'all') {
					filteredObservations = observationObjArray.filter(i => i.code == selectedObservationCategory);
				} else {
					filteredObservations = observationObjArray;
				}

				//일자별 grouping				
				var groupedObservations = groupBy(filteredObservations, 'date');
				var recordCountObj = null;
				var recordCountObjArray = new Array();

				//일자별 Observation Count 리스트 생성				
				Object.keys(groupedObservations).forEach(items => {

					recordCountObj = new Object();
					recordCountObj.date = getFormatDate(new Date(items));
					recordCountObj.count = groupedObservations[items].length;

					recordCountObjArray.push(recordCountObj);
				});

				// x-axis Label
				recordCountLableArray = setGraphLabels(observation);

				var yAxisArray = [];
				var totalRecordCountVal = 0;

				if (recordCountObjArray.length > 0) {
					switch (selectedPeriodUnit) {
						case "0": //yearly
						default:
							recordCountLableArray.forEach(item => {
								var filtered = recordCountObjArray.filter(i => new Date(i.date).getFullYear() == item);
								if (filtered != undefined && filtered != null && filtered.length > 0) {
									var sum = 0;
									filtered.forEach(i => {
										sum = sum + i.count;
									});
									yAxisArray.push(sum);
									totalRecordCountVal += sum;
								} else {
									yAxisArray.push(0);
								}
							});
							break;
						case "1": //monthly
							recordCountLableArray.forEach(item => {
								var year = item.substr(0, 4);
								var month = item.substr(4, 2);

								var filtered = recordCountObjArray.filter(i => new Date(i.date).getFullYear() == year &&
									("0" + (new Date(i.date).getMonth() + 1)).slice(-2) == month);
								if (filtered != undefined && filtered != null && filtered.length > 0) {
									var sum = 0;
									filtered.forEach(i => {
										sum = sum + i.count;
									});
									yAxisArray.push(sum);
									totalRecordCountVal += sum;
								} else {
									yAxisArray.push(0);
								}
							});
							break;
						case "2": //weekly							
							recordCountLableArray.forEach(item => {
								var year = item.substr(0, 4);
								var week = item.substr(4, 2);

								var filtered = recordCountObjArray.filter(i => new Date(i.date).getFullYear() == year &&
									("0" + (new Date(i.date).getWeek())).slice(-2) == week);

								if (filtered != undefined && filtered != null && filtered.length > 0) {
									var sum = 0;
									filtered.forEach(i => {
										sum = sum + i.count;
									});
									yAxisArray.push(sum);
									totalRecordCountVal += sum;
								} else {
									yAxisArray.push(0);
								}
							});
							break;
						case "3": //daily
							recordCountLableArray.forEach(item => {
								var date1 = new Date(item);
								var filtered = recordCountObjArray.filter(i => getFormatDate(new Date(i.date)) == getFormatDate(date1));
								if (filtered != undefined && filtered != null && filtered.length > 0) {
									var sum = 0;
									filtered.forEach(i => {
										sum = sum + i.count;
									});
									yAxisArray.push(sum);
									totalRecordCountVal += sum;
								} else {
									yAxisArray.push(0);
								}
							});
							break;
					}
					this.recordTotalCount = totalRecordCountVal;
				}
				showRecordCountGraph(recordCountLableArray, yAxisArray);
			} else {
				recordCountLableArray = setGraphLabels(observation);
				showRecordCountGraph(recordCountLableArray, null);
			}
		}

		function showUserCountGraph(labelArray, yAxisArray) {

			usercountChart.data = {
				labels: labelArray,
				datasets: [{
					label: 'User Count',
					data: yAxisArray,
					backgroundColor: colors2.blue,
					borderColor: colors.blue,
					borderWidth: 1
				}]
			};
			usercountChart.update();
		}

		function showRecordCountGraph(labelArray, yAxisArray) {

			recordCountChart.data = {
				labels: labelArray,
				datasets: [{
					label: 'Record',
					data: yAxisArray,
					backgroundColor: colors2.red,
					borderColor: colors.red,
					borderWidth: 1
				}]
			};
			recordCountChart.update();
		}

		//차트 초기화
		function initChart() {

			usercountChart.data = null;
			usercountChart.update();

			recordCountChart.data = null;
			recordCountChart.update();
		}

		// ** Graph Labels **

		function setGraphLabels(observation) {

			var labelArray = [];

			if (observation != null) {

				var array1 = observation.effectiveDateTimeStart.split('-');
				var array2 = observation.effectiveDateTimeEnd.split('-');

				var da1 = new Date(observation.effectiveDateTimeStart);
				var da2 = new Date(observation.effectiveDateTimeEnd);

				if (da1 > da2 ) {
					return labelArray;
				}

				var diff = da2 - da1;
				var cDay = 24 * 60 * 60 * 1000; // hour * minute * second * millisecond
				var cMonth = cDay * 30;  // month
				var cYear = cMonth * 12; // year

				// yearCount = parseInt(diff / cYear) + 1;
				yearCount = da2.getFullYear() - da1.getFullYear();				
				
				//monthCount = parseInt(diff / cMonth) + 1;
				monthCount = monthDiff(da1, da2);

				//dayCount = parseInt(diff / cDay) + 1;
				dayCount = parseInt(diff / cDay);
				weekCount = parseInt(dayCount / 7);				

				var dateObj = null;
				var dateObjArray = new Array();

				switch (selectedPeriodUnit) {
					case "0": //yearly
					default:
						if (da2.getFullYear() === da1.getFullYear()) {
							labelArray.push(da1.getFullYear().toString());
						} else {
							for (let index = 0; index <= yearCount; index++) {
								labelArray.push(da1.getFullYear().toString());
								da1.setFullYear(da1.getFullYear() + 1);
							}
						}						
						break;
					case "1": //monthly
					    if (da2.getFullYear() === da1.getFullYear() && da2.getMonth() === da1.getMonth()) {
							var year = da1.getFullYear().toString();
							var month = (da1.getMonth() + 1).toString();
							month = month >= 10 ? month : '0' + month;
							labelArray.push(year + month);
						} else {
							for (let index = 0; index <= monthCount; index++) {
								var year = da1.getFullYear().toString();
								var month = (da1.getMonth() + 1).toString();
								month = month >= 10 ? month : '0' + month;
								labelArray.push(year + month);
								da1.setMonth(da1.getMonth() + 1);
							}
						}						
						break;
					case "2": //weekly    
					    if (weekCount === 0) {
							if (da2.getFullYear() > da1.getFullYear()) {
								var startYear = da1.getFullYear().toString();								
								var startWeekNumber = da1.getWeek().toString();
								startWeekNumber = startWeekNumber >= 10 ? startWeekNumber : '0' + startWeekNumber;
								labelArray.push(startYear + startWeekNumber);

								var endYear = da2.getFullYear().toString();								
								var endWeek = da2.getWeek();								

								for (let week = 1; week <= endWeek; week++) {									
									week = '0' + week;
									labelArray.push(endYear + week);
								}
								
							} else {
								var year = da1.getFullYear().toString();								
								var startWeek = da1.getWeek().toString();
								var endWeek = da2.getWeek().toString();
								startWeek = startWeek >= 10 ? startWeek : '0' + startWeek;
								endWeek = endWeek >= 10 ? endWeek : '0' + endWeek;

								labelArray.push(year + startWeek);	
								if (startWeek != endWeek) {
									labelArray.push(year + endWeek);
								}								
							}
						} else {
							if (da2.getFullYear() > da1.getFullYear()) {
								for (let index = 0; index <= yearCount; index++) {
									
									var year = da1.getFullYear().toString();
									var weekNumber = da1.getWeek();

									if (index === 0) {
										
										var lastDate = new Date(da1.getFullYear(), 11, 31);
										var lastWeekNumber = lastDate.getWeek();
										weekNumber = da1.getWeek();

										for (weekNumber; weekNumber <= lastWeekNumber; weekNumber++) {
											var weekNumberString = weekNumber.toString();
											weekNumberString = weekNumberString >= 10 ? weekNumberString : '0' + weekNumberString;
											labelArray.push(year + weekNumberString);
										}										
									} else {
										var lastWeekNumber = null;

										if (da1.getFullYear() === da2.getFullYear()) {
											lastWeekNumber = da2.getWeek();								
										} else {
											var lastDate = new Date(da1.getFullYear(), 11, 31);
											lastWeekNumber = lastDate.getWeek();
										}

										weekNumber = 1;										

										for (weekNumber; weekNumber <= lastWeekNumber; weekNumber++) {
											var weekNumberString = weekNumber.toString();
											weekNumberString = weekNumberString >= 10 ? weekNumberString : '0' + weekNumberString;
											labelArray.push(year + weekNumberString);
										}
									}
									da1.setFullYear(da1.getFullYear() + 1);									
								}
							} else { // same year
								var year = da1.getFullYear().toString();
								var startWeekNumber = da1.getWeek();
								var lastWeekNumber = da2.getWeek();

								for (startWeekNumber; startWeekNumber <= lastWeekNumber; startWeekNumber++) {
									var weekNumberString = startWeekNumber.toString();
									weekNumberString = weekNumberString >= 10 ? weekNumberString : '0' + weekNumberString;
									labelArray.push(year + weekNumberString);
								}
							}														
						}						
						break;
					case "3": //daily
					    if (da2.getFullYear() === da1.getFullYear() && da2.getMonth() === da1.getMonth() && da2.getDate() === da1.getDate()) {
							labelArray.push(getFormatDate(da1));
						} else {
							for (let index = 0; index <= dayCount; index++) {
								labelArray.push(getFormatDate(da1));
								da1.setDate(da1.getDate() + 1);
							}
						}						
						break;
				}
			}
			return labelArray;
		}

		function monthDiff(d1, d2) { // 개월수 차이 계산
			var months;
			months = (d2.getFullYear() - d1.getFullYear()) * 12;
			months -= d1.getMonth();
			months += d2.getMonth();
			return months <= 0 ? 0 : months;
		}

		// ** Graph Labels **

		// ** UI **
		$("#select1").change(function () {
			selectedObservationCategory = $('#select1 option:selected').val();
			filteringUserCount(observationObjArray);
			filteringObservationCount(observationObjArray);
		});

		$("#select2").change(function () {
			selectedPeriodUnit = $('#select2 option:selected').val();
			filteringUserCount(observationObjArray);
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