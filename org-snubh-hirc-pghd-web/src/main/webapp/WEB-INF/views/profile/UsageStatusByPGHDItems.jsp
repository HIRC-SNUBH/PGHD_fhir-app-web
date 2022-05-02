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

    <title>Population Profile Sample</title>

    <style>
        button {
            width: 80px;
            border: none;
            color: white;
            padding: .8em .5em;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 18px;
            font-weight: bold;
            margin: 4px 2px;
            cursor: pointer;
            background-color: #CCCCCC;
        }
    </style>

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

    <!-- Loading Spinner-->
    <link href="<c:url value='/static/css/slick-loader.min.css' />" rel="stylesheet" />
    <script src="<c:url value='/static/js/slick-loader.min.js' />"></script>
</head>


<body ng-app="pghdApp">
    <div class="container" id="mainDiv">
        <div class="row">
            <div class="col-md-12">
                <label class="col-md-12" style="font-size: 30px; text-align: center;">Personal Data Distribution of Life-log Values</label>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12" style="margin-top: 15px; border: 1px solid skyblue; background-color: white">
                <div class=" graphRow1" style="margin-top: 20px;">
                    <label class="col-md-12" style="font-size: 20px; text-align: center;">PERSON Info.</label>
                </div>
                <div class=" form-group row-space" style="margin-bottom: 20px;">
                    <select id="selPatientId" class="customSelect" style="width:800px;">
                    </select>
                    <button id="lowerPageNumBtn" type="button" onclick="lowerPageNum()">
                        << </button>
                            <label id="pageNumLabel" style="font-size:16px;"> 1 </label>
                            <button id="upperPageNumBtn" type="button" onclick="upperPageNum()"> >> </button>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12" style="margin-top: 15px; border: 1px solid skyblue; background-color: white">
                <div class=" graphRow1" style="margin-top: 20px;">
                    <label class="col-md-12" style="font-size: 20px; text-align: center;">Date Format</label>
                </div>
                <div class=" form-group row-space" style="margin-bottom: 20px;">
                    <select id="selDatetermCategory" class="customSelect">
                    </select>
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
        var selectedDateUnit = null;
        var selectedPatientId = null;
        var pageNumData = 1;

        //query
        var observationCount = null;
        var observationObjArray = []; // original	

        var minTerm;
        var periodTerm;
        var chartList = new Array();
        var canvasList = new Array();

        var groupBy = function (xs, key) {
            return xs.reduce(function (rv, x) {
                (rv[x[key]] = rv[x[key]] || []).push(x);
                return rv;
            }, {});
        };

        window.onload = function () {
            // setChart("bglChart", "Value as Number");
            setSelectTime('selDatetermCategory');
            search(dataCriteriaPatient, dataCriteriaObservation, dataCriteriaDevice, dataCriteriaQuestionaire, dataCriteriaBundle);
        }

        function periodTermCal() {
            var startDateVal = new Date(dataCriteriaObservation.effectiveDateTimeStart);
            var endDateVal = new Date(dataCriteriaObservation.effectiveDateTimeEnd);
            var termMs = endDateVal.getTime() - startDateVal.getTime();
            var cDay = 0;
            switch (selectedDateUnit) {
                case '0':
                    cDay = 365.25 * 24 * 60 * 60 * 1000; // year * hour * minute * second * millisecond	
                    this.periodTerm = Math.floor((termMs / cDay) + 1);
                    break;
                case '1':
                    cDay = 30.4 * 24 * 60 * 60 * 1000; // month * hour * minute * second * millisecond	
                    this.periodTerm = Math.floor((termMs / cDay) + 1);
                    break;
                case '2':
                    cDay = 7 * 24 * 60 * 60 * 1000; // week * hour * minute * second * millisecond	
                    this.periodTerm = Math.floor((termMs / cDay) + 1);
                    break;
                case '3':
                    cDay = 24 * 60 * 60 * 1000; // hour * minute * second * millisecond	
                    this.periodTerm = Math.floor((termMs / cDay) + 1);
                    break;
                default:
                    cDay = 24 * 60 * 60 * 1000; // hour * minute * second * millisecond
                    this.periodTerm = Math.floor((termMs / cDay) + 1);
                    break;
            }
            if(periodTerm < 2){
                this.minTerm = 0;
            }
            else{
                this.minTerm = 1;
            }
        }

        $("#selPatientId").change(function () {
            initChart();
            filteringObservationCode(observationObjArray);
        });

        $("#selDatetermCategory").change(function () {
            initChart();
            periodTermCal();
            filteringObservationCode(observationObjArray);
        });

        function setChart(canvasId, dataLabel) {
            var ctxUsercountChart = document.getElementById(canvasId).getContext('2d');

            var graphScales = {
                x: {
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
                        callback: function(value, index, values) {
                            if(Number.isInteger(value)){
                                var startDateVal = new Date(dataCriteriaObservation.effectiveDateTimeStart);
                                var labelDateVal = new Date(startDateVal.getTime() + (86400000 * (value-1)));
                                
                                var finalDateVal = new Date(labelDateVal.getFullYear);
                                return labelDateVal.toLocaleDateString();
                            }
                        },
                        font:{
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
            }

            firstInitChart = new Chart(ctxUsercountChart, { 
                type: 'scatter',
                data: {
                    datasets: [{
                        label: dataLabel,
                        data: null,
                    }]
                },
                options: {
                    responsive: true,
                    scales: graphScales,
                    maintainAspectRatio: false,
                }
            });
        }

        function search(patient, observation, device, questionnaireResponse, bundle) {

            this.observationObjArray = [];
            dataCriteriaPatient = patient;
            dataCriteriaObservation = observation;
            dataCriteriaDevice = device;
            dataCriteriaBundle = bundle;
            document.getElementById('pageNumLabel').innerHTML = 1;

            if (!checkDataCriteria(dataCriteriaPatient, dataCriteriaObservation)) {
                initChart();
            } else {
                initChart();
                var queryString = createQueryString();
                var query = client.request(queryString, { flat: true });
                SlickLoader.enable();
                query.then(function (results) {
                    SlickLoader.disable();
                    setObservationObjArray(results);
                    setSelectPatientList('selPatientId', observationObjArray, 1);
                    filteringObservationCode(observationObjArray);
                }).catch(error => alert(error));
            }

        }

        function lowerPageNum() {
            var obsObjArr = this.observationObjArray;
            pageNumData = parseInt(document.getElementById('pageNumLabel').innerHTML);
            if (pageNumData > 1) {
                pageNumData--;
                document.getElementById('pageNumLabel').innerHTML = pageNumData;
                setSelectPatientList('selPatientId', obsObjArr, pageNumData);
            }
        }

        function upperPageNum() {
            var obsObjArr = this.observationObjArray;
            pageNumData = parseInt(document.getElementById('pageNumLabel').innerHTML);
            pageNumData++;
            document.getElementById('pageNumLabel').innerHTML = pageNumData;
            setSelectPatientList('selPatientId', obsObjArr, pageNumData);
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

        function setSelectPatientList(id, observationObjArray, pageNum) {
            var patientidSelectArr = new Array();
            var pageCount = 20;

            var groupedByPatientId = groupBy(observationObjArray, 'patientId');
            Object.keys(groupedByPatientId).forEach(items => {
                var patientIdSet = new Object();
                patientIdSet.text = '[' + items + '] / '
                    + groupedByPatientId[items].find(f => f.birthDate != null).birthDate + '/'
                    + groupedByPatientId[items].find(f => f.gender != null).gender;
                patientIdSet.codeVal = items;
                patientidSelectArr.push(patientIdSet);
            });

            var maxLength = patientidSelectArr.length;
            var startLoopPt = (pageNum - 1) * pageCount;
            var endLoopPt;
            if (maxLength >= pageNum * pageCount) {
                endLoopPt = pageNum * pageCount;
            }
            else {
                endLoopPt = maxLength;
            }
            var select = $('#' + id)[0];
            select.options.length = 0;
            for (var i = startLoopPt; i < endLoopPt; i++) {
                if (i === startLoopPt) {
                    select.append(new Option(patientidSelectArr[i].text, patientidSelectArr[i].codeVal, true, true));
                }
                else {
                    select.append(new Option(patientidSelectArr[i].text, patientidSelectArr[i].codeVal, false, false));
                }
            }
        }

        function filteringObservationCode(observationObjArray) {
            if (observationObjArray != null && observationObjArray.length > 0) {
                var datasets = new Array();

                // Set Combobox selected
                selectedDropOffGroup = $('#selObservationCategory option:selected').val();
                selectedDateUnit = $('#selDatetermCategory option:selected').val();
                selectedPatientId = $('#selPatientId option:selected').val();

                datasets = filteringByObservationCode(observationObjArray, selectedDateUnit);
                showUsercountGraph(datasets, selectedPatientId);
            }
        }

        function filteringByObservationCode(observationObjArray, selectedDateUnit) {

            var cDay = 24 * 60 * 60 * 1000;
            switch (selectedDateUnit) {
                case '0':
                    cDay = 365.25 * 24 * 60 * 60 * 1000; // year * hour * minute * second * millisecond	
                    break;
                case '1':
                    cDay = 30.4 * 24 * 60 * 60 * 1000; // month * hour * minute * second * millisecond	
                    break;
                case '2':
                    cDay = 7 * 24 * 60 * 60 * 1000; // week * hour * minute * second * millisecond	
                    break;
                case '3':
                    cDay = 24 * 60 * 60 * 1000; // hour * minute * second * millisecond	
                    break;
                default:
                    cDay = 24 * 60 * 60 * 1000; // hour * minute * second * millisecond	
                    break;
            }

            var datasets = new Array();
            var dataset = null;

            var patientIdSetArr = new Array();
            var patientIdSet = null;

            var patientObj = null;
            var patientObjArray = new Array();

            var groupedByPatientId = groupBy(observationObjArray, 'patientId');

            Object.keys(groupedByPatientId).forEach(items => {

                dataset = new Object();

                patientObj = new Object();
                patientObj.period = 1;
                patientObj.age = null;
                patientObj.birthDate = groupedByPatientId[items].find(f => f.birthDate != null).birthDate;

                patientObj.gender = groupedByPatientId[items][0].gender;

                groupedByPatientId[items].sort(date_descending);

                var scatterValArr = new Array();
                var dateList = [];
                groupedByPatientId[items].forEach(i2 => {
                    dateList.push(i2.date);
                    var scatterVal = new Object();
                    var begin = new Date(dataCriteriaObservation.effectiveDateTimeStart);
                    var dayEnd = new Date(i2.date);
                    var diffPeriod = dayEnd - begin;
                    var dateTerm = parseInt(diffPeriod / cDay) + 1;
                    if (i2.code != '74008-4') {
                        scatterVal.code = i2.code;
                        scatterVal.x = dateTerm;
                        if (i2.hasOwnProperty('valueQuantity')) {
                            scatterVal.y = i2.valueQuantity;
                            scatterValArr.push(scatterVal);
                        }
                    }
                });
                dataset.scatterValue = scatterValArr;

                var dayBegin = new Date(dateList[0]);
                if (patientObj.birthDate != null) {
                    var birthYear = new Date(patientObj.birthDate).getFullYear();
                    patientObj.age = dayBegin.getFullYear() - birthYear + 1;
                }

                dataset.patientId = items;

                datasets.push(dataset);
            })

            return datasets;
        }

        function setChartCanvas(rowDivId, canvasId){
            var mainDiv = document.getElementById("mainDiv");

            var rowDiv = document.createElement("div");
            rowDiv.setAttribute("id", rowDivId);
            rowDiv.setAttribute("class", "row");
            rowDiv.style.marginTop="15px";
            rowDiv.style.border="1px solid skyblue";
            rowDiv.style.backgroundColor="white";

            var pageConDiv = document.createElement("div");
            pageConDiv.setAttribute("class","pageContainer");

            var colDiv = document.createElement("div");
            colDiv.setAttribute("class","col-xs-12");
            colDiv.style.marginTop="20px"
            colDiv.style.marginBottom="20px"

            var canvasDiv = document.createElement("div");

            var chartCanvas = document.createElement("canvas");
            chartCanvas.setAttribute("id", canvasId);
            chartCanvas.setAttribute("class", "col-md-12");

            canvasDiv.appendChild(chartCanvas);
            colDiv.appendChild(canvasDiv);
            pageConDiv.appendChild(colDiv);
            rowDiv.appendChild(pageConDiv);
            mainDiv.appendChild(rowDiv);
        }

        function setAvailableChart(canvasId, inputDatasets) {
            periodTermCal();
            var obsChart;
            var ctxUsercountChart = document.getElementById(canvasId).getContext('2d');

            var graphScales = {
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
                        callback: function(value, index, values) {
                            if (Number.isInteger(value)) {
                                var startDateVal = new Date(dataCriteriaObservation.effectiveDateTimeStart);
                                var yearVal = startDateVal.getFullYear();
                                if (selectedDateUnit === '0') {
                                    var finalDateVal = startDateVal.getFullYear() + value -1;
                                    return finalDateVal;
                                }
                                else if (selectedDateUnit === '1') {
                                    var monthVal = startDateVal.getMonth() + value;
                                    var monthIntVal = Math.floor(parseInt(monthVal) / 12);

                                        monthVal = monthVal - (12 * monthIntVal);
                                        yearVal = yearVal + monthIntVal;
                                        if (monthVal === 0) {
                                            monthVal = 12;
                                            yearVal = yearVal - 1;
                                        }

                                    var month = monthVal >= 10 ? monthVal.toString() : '0' + monthVal;
                                    return yearVal + month;
                                }
                                else if (selectedDateUnit === '2') {
                                    var weekIntVal = Math.ceil(parseInt(value) % 52.1);
                                    var yearStackVal = Math.floor(parseInt(value) / 52.1);
                                    yearVal = yearVal + yearStackVal;
                                    if (weekIntVal === 0) {
                                        weekIntVal = 52;
                                        yearVal = yearVal - 1;
                                    }                                    
                                    var week = weekIntVal >= 10 ? weekIntVal.toString() : '0' + weekIntVal;
                                    return yearVal + week;
                                }
                                else {
                                    var labelDateVal = new Date(startDateVal.getTime() + (86400000 * (value - 1)));
                                    var year = labelDateVal.getFullYear();
                                    var month = ("0" + (1 + labelDateVal.getMonth())).slice(-2);
                                    var day = ("0" + labelDateVal.getDate()).slice(-2);

                                    return year + month + day;
                                }
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
            }

            obsChart = new Chart(ctxUsercountChart, { 
                type: 'scatter',
                data: {
                    datasets: inputDatasets
                },
                options: {
                    responsive: true,
                    scales: graphScales,
                    maintainAspectRatio: false,
                }
            });
            chartList.push(obsChart);
        }

        function setDivId(obsCode){
            var returnIdString = null;
            switch(obsCode){
                case "997671000000106":
                    returnIdString = "bglDiv";
                    break;
                case "27113001":
                    returnIdString = "bwDiv";
                    break;
                case "248263006":
                    returnIdString = "dosDiv";
                    break;
                case "55411-3":
                    returnIdString = "edDiv";
                    break;
                case "74008-4":
                    returnIdString = "eiDiv";
                    break;
                case "248646004":
                    returnIdString = "hbDiv";
                    break;
                case "248334005":
                    returnIdString = "lobDiv";
                    break;
                case "405052004":
                    returnIdString = "losDiv";
                    break;
                case "55423-8":
                    returnIdString = "nsupDiv";
                    break;
                case "924481000000109":
                    returnIdString = "sobpDiv";
                    break;
                case "SNUBH0046":
                    returnIdString = "sssDiv";
                    break;
                default :
                    break;
            }
            return returnIdString;
        }

        function setCanvasId(obsCode){
            var returnIdString = null;
            switch(obsCode){
                case "997671000000106":
                    returnIdString = "bglCanvas";
                    break;
                case "27113001":
                    returnIdString = "bwCanvas";
                    break;
                case "248263006":
                    returnIdString = "dosCanvas";
                    break;
                case "55411-3":
                    returnIdString = "edCanvas";
                    break;
                case "74008-4":
                    returnIdString = "eiCanvas";
                    break;
                case "248646004":
                    returnIdString = "hbCanvas";
                    break;
                case "248334005":
                    returnIdString = "lobCanvas";
                    break;
                case "405052004":
                    returnIdString = "losCanvas";
                    break;
                case "55423-8":
                    returnIdString = "nsupCanvas";
                    break;
                case "924481000000109":
                    returnIdString = "sobpCanvas";
                    break;
                case "SNUBH0046":
                    returnIdString = "sssCanvas";
                    break;
                default :
                    break;
            }
            return returnIdString;
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

        function showUsercountGraph(data, patientId) {
            data.forEach(items => {
                if (items.patientId === patientId) {
                    var groupedByCode = groupBy(items.scatterValue, 'code');
                    Object.keys(groupedByCode).forEach(elements => {
                        canvasList.push(elements);

                        var datasetObjArr = new Array();
                        var divId = setDivId(elements);
                        var canvId = setCanvasId(elements);
                        setChartCanvas(divId, canvId);

                        var datasetObj = new Object();
                        datasetObj = setDataObjOptions(elements);
                        var scatterValArr = new Array();
                        groupedByCode[elements].forEach(dataset => {
                            var scatterValue = new Object();
                            scatterValue.x = dataset.x;
                            scatterValue.y = dataset.y;
                            scatterValArr.push(scatterValue);                            
                        })
                        datasetObj.data = scatterValArr;
                        datasetObjArr.push(datasetObj);
                        setAvailableChart(canvId, datasetObjArr);
                    });                                
                }
            })
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

        function initChart() {
            chartList.forEach(chartElement => {
                chartElement.destroy();
            })
            canvasList.forEach(codeVal =>{
                var rowDivId = setDivId(codeVal);
                var rowDiv = document.getElementById(rowDivId);
                var mainDiv = rowDiv.parentElement;
                mainDiv.removeChild(rowDiv);
            });
            chartList.length = 0;
            canvasList.length = 0;
        }

    </script>

</body>

</html>