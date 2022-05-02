<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html>

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="Content-Script-Type" content="text/javascript" />
    <meta http-equiv="Content-Style-Type" content="text/css" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>OBSERVATION VALUE DIST</title>

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


<body ng-app="pghdApp">
    <!-- <div>
<canvas id="myChart1" width="400" height="200"></canvas>
</div> -->
    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <label class="col-md-12" style="font-size: 30px; text-align: center;">Status of Data by Life-log
                    Items</label>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12" style="margin-top: 15px; border: 1px solid skyblue; background-color: white">
                <div class=" graphRow1" style="margin-top: 20px;">
                    <label class="col-md-12" style="font-size: 20px; text-align: center;">Life-log Items</label>
                </div>
                <div class=" form-group row-space" style="margin-bottom: 20px;">
                    <select id="selObservationCategory" class="form-select" onchange="showGraph()">
                    </select>
                </div>
            </div>
        </div>
        <div class="row" style="margin-top: 15px; border: 1px solid skyblue; background-color: white">
            <div class="pageContainer">
                <div class="col-xs-12" style="margin-top: 20px; margin-bottom: 20px;">
                    <div id="recordCountGraph" class="graphRow1">
                        <canvas id="chartUsercount" class="col-md-12"></canvas>
                    </div>
                </div>
            </div>
        </div>
        <div class="row" style="margin-top: 15px; border: 1px solid skyblue; background-color: white;">
            <div class="pageContainer">
                <div class="col-xs-12" style="margin-top: 20px; margin-bottom: 20px;">
                    <div id="recordValueGraph" class="graphRow1">
                        <canvas id="chartUserValue" class="col-md-12"></canvas>
                    </div>
                </div>
            </div>
        </div>
        <div class="row" style="margin-top: 15px; border: 1px solid skyblue; background-color: white;">
            <div class="pageContainer">
                <div class="col-xs-12" style="margin-top: 20px; margin-bottom: 20px;">
                    <div id="recordValueGraph" class="graphRow1">
                        <canvas id="chartValueAsDate" class="col-md-12"></canvas>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script type="text/javascript">

        const client = parent.client;
        var dataCriteriaPatient = parent.patient;
        var dataCriteriaObservation = parent.observation;
        var dataCriteriaDevice = parent.device;
        var dataCriteriaBundle = parent.bundle;
        var dataCriteriaQuestionaire = parent.questionnaireResponse;
        var defaultResourceCount = parent.defalutResourceCount;

        //Combobox Selected Value
        var selectedObservationCategory = null;

        var searchResults = null;

        //query
        var observationCount = null;
        var observationObjArray = [];

        //chart
        var minTerm;
        var periodTerm;
        var recordCountChart;
        var recordValueChart;
        var recordValueScalesPerDateChart;
        var observationCodelabelArray = [];

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

        var groupBy = function (xs, key) {
            return xs.reduce(function (rv, x) {
                (rv[x[key]] = rv[x[key]] || []).push(x);
                return rv;
            }, {});
        };

        window.onload = function () {
            setSelectObservationCategory('selObservationCategory', dataCriteriaObservation);
            selectedObservationCategory = $('#selObservationCategory option:selected').val();

            setChart();

            search(dataCriteriaPatient, dataCriteriaObservation, dataCriteriaDevice, dataCriteriaQuestionaire, dataCriteriaBundle);
        }

        $("#selObservationCategory").change(function () {
            filteringObservationCode(observationObjArray);
        });

        function periodTermCal() {
            var startDateVal = new Date(dataCriteriaObservation.effectiveDateTimeStart);
            var endDateVal = new Date(dataCriteriaObservation.effectiveDateTimeEnd);
            var termMs = endDateVal.getTime() - startDateVal.getTime();
            this.periodTerm = (termMs / (1000 * 60 * 60 * 24)) + 1;
            if(periodTerm < 2){
                this.minTerm = 0;
            }
            else{
                this.minTerm = 1;
            }
        }

        function setChart() {
            periodTermCal();
            var recordCountScales = {
                x: {
                    min: minTerm,
                    suggestedMax: periodTerm,
                    // beginAtZero: true,
                    display: true,
                    title: {
                        display: true,
                        text: 'Period of Use',
                        font: {
                            size: 20
                        },
                        padding: { top: 10, left: 0, right: 0, bottom: 0 }
                    },
                    ticks: {
                        callback: function (value, index, values) {
                            if (Number.isInteger(value)) {
                                return value;
                            }
                        },
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
                        callback: function (value, index, values) {
                            if (Number.isInteger(value)) {
                                return value;
                            }
                        },
                        font: {
                            size: 15,
                        }
                    }
                }
            };
            const ctxRecordCountChart = document.getElementById("chartUsercount").getContext('2d');
            recordCountChart = new Chart(ctxRecordCountChart, {
                type: 'scatter',
                data: {
                    datasets: [{
                        label: 'Record Count',
                        data: null,
                        borderColor: '#2196f3',
                        backgroundColor: '#2196f3',
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: recordCountScales,
                    plugins: {
                        legend: {
                            position: 'right'
                        }
                    }
                }
            });

            var recordValueScales = {
                x: {
                    beginAtZero: true,
                    display: true,
                    title: {
                        display: true,
                        text: 'Age',
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
                        text: 'Value',
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
            var ctxRecordValueChart = document.getElementById("chartUserValue").getContext('2d');
            recordValueChart = new Chart(ctxRecordValueChart, {
                type: 'scatter',
                data: {
                    datasets: [{
                        label: 'Observation Value',
                        data: null,
                        borderColor: '#2196f3',
                        backgroundColor: '#2196f3',
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: recordValueScales,
                    plugins: {
                        legend: {
                            position: 'right'
                        }
                    }
                }
            });

            var recordValueScalesPerDate = {
                x: {
                    min: minTerm,
                    suggestedMax: periodTerm,
                    display: true,
                    title: {
                        display: true,
                        text: 'Observation Date',
                        font: {
                            size: 20
                        },
                        padding: { top: 10, left: 0, right: 0, bottom: 0 }
                    },
                    ticks: {
                        callback: function (value, index, values) {
                            if (Number.isInteger(value)) {
                                var startDateVal = new Date(dataCriteriaObservation.effectiveDateTimeStart);
                                var labelDateVal = new Date(startDateVal.getTime() + (86400000 * (value - 1)));
                                var year = labelDateVal.getFullYear();
                                var month = ("0" + (1 + labelDateVal.getMonth())).slice(-2);
                                var day = ("0" + labelDateVal.getDate()).slice(-2);

                                return year + month + day;
                            }
                        },
                        font: {
                            size: 15,
                        }
                    },
                },
                y: {
                    beginAtZero: true,
                    display: true,
                    title: {
                        display: true,
                        text: 'Value',
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
            const ctxRecordValueScalesPerDate = document.getElementById("chartValueAsDate").getContext('2d');
            recordValueScalesPerDateChart = new Chart(ctxRecordValueScalesPerDate, {
                type: 'scatter',
                data: {
                    datasets: [{
                        label: 'Value as Number',
                        data: null,
                        borderColor: '#2196f3',
                        backgroundColor: '#2196f3',
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: recordValueScalesPerDate,
                    plugins: {
                        legend: {
                            position: 'right'
                        }
                    }
                }
            });
        }

        function search(patient, observation, device, questionnaireResponse, bundle) {
            periodTermCal()
            updateChartOption();
            this.observationObjArray = [];
            dataCriteriaPatient = patient;
            dataCriteriaObservation = observation;
            dataCriteriaDevice = device;
            dataCriteriaBundle = bundle;
            setSelectObservationCategory('selObservationCategory', dataCriteriaObservation);

            if (!checkDataCriteria(dataCriteriaPatient, dataCriteriaObservation)) {
                initChart();
            } else {
                var queryString = createQueryString();
                var query = client.request(queryString, { flat: true });
                SlickLoader.enable();
                query.then(function (results) {
                    SlickLoader.disable();
                    setObservationObjArray(results);
                    filteringObservationCode(observationObjArray);
                }).catch(error => alert(error));
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
                    observationObj.valueQuantity = null;

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

                    if (observation.hasOwnProperty('valueQuantity')) {
                        observationObj.valueQuantity = observation.valueQuantity.value;
                    }

                    observationObjArray.push(observationObj);
                });

                observationObjArray = filteredByAge(observationObjArray);
                //날짜별 내림차순 정렬
                observationObjArray.sort(date_descending);
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

        function filteringObservationCode(observationObjArray) {
            if (observationObjArray != null && observationObjArray.length > 0) {
                var datasets = new Array();

                // Set Combobox selected
                selectedDropOffGroup = $('#selObservationCategory option:selected').val();

                datasets = filteringByObservationCode(observationObjArray);
                showUsercountGraph(datasets, selectedDropOffGroup);
                showUserValueGraph(datasets, selectedDropOffGroup);
                showValueasnumberGraph(datasets, selectedDropOffGroup);
            }
        }

        function filteringByObservationCode(observationObjArray) {

            var datasets = new Array();
            var dataset = null;

            var patientObj = null;
            var patientObjArray = new Array();

            var groupedByPatientId = groupBy(observationObjArray, 'patientId');

            Object.keys(groupedByPatientId).forEach(items => {

                patientObj = new Object();
                patientObj.period = 1;
                patientObj.age = null;
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
                }

                var groupedByCode = groupBy(groupedByPatientId[items], 'code');
                Object.keys(groupedByCode).forEach(elements => {
                    dataset = new Object();
                    dataset.code = elements;
                    dataset.period = patientObj.period;
                    dataset.age = patientObj.age;
                    dataset.count = groupedByCode[elements].length;
                    dataset.valueArr = new Array();
                    groupedByCode[elements].forEach(valueElement => {
                        if (valueElement.hasOwnProperty('valueQuantity')) {
                            var valueObj = new Object();
                            var startDate = new Date(dataCriteriaObservation.effectiveDateTimeStart);
                            var obsDate = new Date(valueElement.date);
                            var obsDateDay = parseInt(((obsDate - startDate) / cDay) + 1);
                            valueObj.value = valueElement.valueQuantity
                            valueObj.date = obsDateDay;
                            dataset.valueArr.push(valueObj);

                        }
                    });
                    datasets.push(dataset);
                });
            })

            return datasets;
        }

        function showUsercountGraph(results, obsCode) {
            var data = [];
            var datasetObjArr = new Array();
            datasetObjArr = getRecordCount(results, obsCode);
            recordCountChart.data.datasets = datasetObjArr;
            recordCountChart.update();
        }

        function getRecordCount(data, obsCode) {
            var datasetObjArr = new Array();
            var groupedByCode = groupBy(data, 'code');
            if (obsCode === 'all') {
                Object.keys(groupedByCode).forEach(elements => {
                    var datasetObj = new Object();
                    var scatterValArr = new Array();
                    datasetObj = setDataObjOptions(elements)
                    groupedByCode[elements].forEach(dataset => {
                        var scatterValue = new Object();
                        scatterValue.x = dataset.period;
                        scatterValue.y = dataset.count;
                        scatterValArr.push(scatterValue);
                    })
                    datasetObj.data = scatterValArr;
                    datasetObjArr.push(datasetObj);
                });
            }
            else {
                var datasetObj = new Object();
                var scatterValArr = new Array();
                datasetObj = setDataObjOptions(obsCode)
                groupedByCode[obsCode].forEach(dataset => {
                    var scatterValue = new Object();
                    scatterValue.x = dataset.period;
                    scatterValue.y = dataset.count;
                    scatterValArr.push(scatterValue);
                })
                datasetObj.data = scatterValArr;
                datasetObjArr.push(datasetObj);
            }
            return datasetObjArr;
        };

        function showUserValueGraph(results, obsCode) {
            var data = [];
            var datasetObjArr = new Array();
            datasetObjArr = getRecordValue(results, obsCode, 'age');
            recordValueChart.data.datasets = datasetObjArr;
            recordValueChart.update();
        }

        function showValueasnumberGraph(results, obsCode) {
            var data = [];
            var datasetObjArr = new Array();
            datasetObjArr = getRecordValue(results, obsCode, 'date');
            recordValueScalesPerDateChart.data.datasets = datasetObjArr;
            recordValueScalesPerDateChart.update();
        }

        function setDataObjOptions(obsCode) {
            var datasetObj = new Object();
            switch (obsCode) {
                case '997671000000106':
                    datasetObj.label = 'Blood gluscose level';
                    datasetObj.borderColor = 'rgba(255, 99, 132, 1)';
                    datasetObj.backgroundColor = 'rgba(255, 99, 132, 1)';
                    break;
                case '27113001':
                    datasetObj.label = 'Body weight';
                    datasetObj.borderColor = 'rgba(255, 159, 64, 1)';
                    datasetObj.backgroundColor = 'rgba(255, 159, 64, 1)';
                    break;
                case '248263006':
                    datasetObj.label = 'Duration of sleep';
                    datasetObj.borderColor = 'rgba(255, 205, 86, 1)';
                    datasetObj.backgroundColor = 'rgba(255, 205, 86, 1)';
                    break;
                case '55411-3':
                    datasetObj.label = 'Exercise duration';
                    datasetObj.borderColor = 'rgba(75, 192, 192, 1)';
                    datasetObj.backgroundColor = 'rgba(75, 192, 192, 1)';
                    break;
                case '74008-4':
                    datasetObj.label = 'Exercise intensity';
                    datasetObj.borderColor = 'rgba(54, 162, 235, 1)';
                    datasetObj.backgroundColor = 'rgba(54, 162, 235, 1)';
                    break;
                case '248646004':
                    datasetObj.label = 'Heart beat';
                    datasetObj.borderColor = 'rgba(153, 102, 255, 1)';
                    datasetObj.backgroundColor = 'rgba(153, 102, 255, 1)';
                    break;
                case '248334005':
                    datasetObj.label = 'Length of body';
                    datasetObj.borderColor = 'rgba(201, 203, 207, 1)';
                    datasetObj.backgroundColor = 'rgba(201, 203, 207, 1)';
                    break;
                case '405052004':
                    datasetObj.label = 'Level of stress';
                    datasetObj.borderColor = 'rgba(153, 204, 255, 1)';
                    datasetObj.backgroundColor = 'rgba(153, 204, 255, 1)';
                    break;
                case '55423-8':
                    datasetObj.label = 'Numbers of steps in unspecified time Pedometer';
                    datasetObj.borderColor = 'rgba(153, 153, 255, 1)';
                    datasetObj.backgroundColor = 'rgba(153, 153, 255, 1)';
                    break;
                case '924481000000109':
                    datasetObj.label = 'Self-monitoring of blood pressure';
                    datasetObj.borderColor = 'rgba(204, 153, 255, 1)';
                    datasetObj.backgroundColor = 'rgba(204, 153, 255, 1)';
                    break;
                case 'SNUBH0046':
                    datasetObj.label = 'Sleep satisfaction score';
                    datasetObj.borderColor = 'rgba(255, 153, 255, 1)';
                    datasetObj.backgroundColor = 'rgba(255, 153, 255, 1)';
                    break;
                default:
                    break;
            }
            return datasetObj;
        }

        function getRecordValue(data, obsCode, xAxisType) {
            var datasetObjArr = new Array();
            var groupedByCode = groupBy(data, 'code');
            if (obsCode === 'all') {
                Object.keys(groupedByCode).forEach(elements => {
                    var datasetObj = new Object();
                    var scatterValArr = new Array();
                    datasetObj = setDataObjOptions(elements)
                    groupedByCode[elements].forEach(dataset => {
                        dataset.valueArr.forEach(valueElement => {
                            var scatterValue = new Object();
                            if (xAxisType == 'age') {
                                scatterValue.x = dataset.age;
                            }
                            else if (xAxisType == 'date') {
                                scatterValue.x = valueElement.date;
                            }
                            scatterValue.y = valueElement.value;
                            scatterValArr.push(scatterValue);
                        })
                    })
                    datasetObj.data = scatterValArr;
                    datasetObjArr.push(datasetObj);
                });
            }
            else {
                var datasetObj = new Object();
                var scatterValArr = new Array();
                datasetObj = setDataObjOptions(obsCode)
                groupedByCode[obsCode].forEach(dataset => {
                    dataset.valueArr.forEach(valueElement => {
                        var scatterValue = new Object();
                        if (xAxisType == 'age') {
                            scatterValue.x = dataset.age;
                        }
                        else if (xAxisType == 'date') {
                            scatterValue.x = valueElement.date;
                        }
                        scatterValue.y = valueElement.value;
                        scatterValArr.push(scatterValue);
                    })
                })
                datasetObj.data = scatterValArr;
                datasetObjArr.push(datasetObj);
            }
            return datasetObjArr;
        };

        function initChart() {
            recordCountChart.data = null;
            recordCountChart.update();

            recordValueChart.data = null;
            recordValueChart.update();

            recordValueScalesPerDateChart.data = null;
            recordValueScalesPerDateChart.update();
        }

        function updateChartOption() {
            recordCountChart.destroy();
            recordValueChart.destroy();
            recordValueScalesPerDateChart.destroy();
            setChart();
        }
    </script>
</body>

</html>