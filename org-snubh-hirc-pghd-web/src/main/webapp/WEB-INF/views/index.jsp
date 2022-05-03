<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<spring:eval expression="@customProperties['server.url']" var="REST_SERVICE_ROOT_URI"/>
<spring:eval expression="@customProperties['fhirserver.url']" var="FHIR_SERVICE_ROOT_URI"/>

<!doctype html>
<html>

<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="Content-Script-Type" content="text/javascript" />
<meta http-equiv="Content-Style-Type" content="text/css" />
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>PGHD App</title>

<!-- Bootstrap core CSS -->
<link href="<c:url value='/static/css/bootstrap.min.css' />" rel="stylesheet" />
<!-- Custom styles for this template -->
<link href="<c:url value='/static/css/common.css' />" rel="stylesheet" />

<!-- Bootstrap core JavaScript ================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="<c:url value='/static/js/jquery.min.js' />"></script>
<script src="<c:url value='/static/js/jquery-ui.min.js' />"></script>
<script src="<c:url value='/static/js/bootstrap.min.js' />"></script>


<link href="<c:url value='/static/css/bootstrap-datepicker.min.css' />" rel="stylesheet" />
<script src="<c:url value='/static/js/bootstrap-datepicker.min.js' />"></script>
<script src="<c:url value='/static/js/bootstrap-datepicker.kr.js' />"></script>

<!-- fhir Client -->
<script src="<c:url value='/static/js/fhirclient/build/fhir-client.js' />"></script>
<script src="<c:url value='/static/js/func.js' />"></script>

<!-- Chart -->
<script src="<c:url value='/static/js/chart.js-3.5.1/package/dist/chart.js' />"></script>
<style>
body {
	padding-top: 60px;
	background-color: white;
}

div.main {
	padding: 0px 0px 0px 0px;
	overflow: hidden;
	background-color: white;
	/* height: auto; */
}

div.panel-heading {
	padding: 5px 15px;
}

div.panel-divline {
	padding: 5px 0px;
	background-color: rgb(0, 0, 0);
}

li.list-group-item {
	height: auto;
	padding: 5px 15px;
}

ul.collapse {
	padding-inline-start: 0px;
}

ul.collapsing {
	padding-inline-start: 0px;
}
</style>
</head>

<body>
	<div class="navbar navbar-inverse navbar-fixed-top">
		<div class="container">
			<!-- 상단메뉴의 이름 -->
			<a class="navbar-brand" href="${REST_SERVICE_ROOT_URI}index"> Home</a>
			<!-- 상단 메뉴가 좁아지면서 햄버거 버튼됨 -->
			<div class="navbar-header">
				<button class="navbar-toggle collapsed" data-toggle="collapse" data-target="#target">
					<span class="sr-only">Toggle Navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
				</button>
			</div>
			<!--펼쳐진 상단 메뉴 -->
			<div class="collapse navbar-collapse" id="target">
				<ul class="nav navbar-nav">
					<!-- 상단메뉴의 드롭다운메뉴 -->
					<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <span>Population Profile of Life-log</span> <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="${REST_SERVICE_ROOT_URI}population/NumberOfUsers" target="mainFrame">Number of Users</a></li>
							<li><a href="${REST_SERVICE_ROOT_URI}population/DropOutRate" target="mainFrame">Drop out Rate</a></li>
							<li class="divider"></li>
							<li><a href="${REST_SERVICE_ROOT_URI}population/UsersAndRecordByOBS" target="mainFrame">Distribution of Usage by Life-log Items</a></li>
							<li><a href="${REST_SERVICE_ROOT_URI}population/TPRbyAgeGenderGraph" target="mainFrame">Distribution of Usage by Demographic</a></li>
							<li class="divider"></li>
							<li><a href="${REST_SERVICE_ROOT_URI}population/ObsValueDist" target="mainFrame">Status of Data by Life-log Items</a></li>
						</ul></li>
					<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <span>Personal Profile of Life-log</span> <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="${REST_SERVICE_ROOT_URI}profile/UsageStatusByPGHDItems" target="mainFrame">Personal Data Distribution of Life-log Values</a></li>
						</ul></li>

					<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown"> <span>Population profile of questionnaire</span> <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="${REST_SERVICE_ROOT_URI}questionnaire/SankeyDiagram" target="mainFrame">Sankey Diagram</a></li>
							<li><a href="${REST_SERVICE_ROOT_URI}questionnaire/DistributionOfResponse" target="mainFrame">Distribution Of Response by Questionnaire</a></li>
						</ul></li>
				</ul>

				<ul class="nav navbar-nav navbar-right">
				</ul>

			</div>
			<!--펼쳐진 상단 메뉴끝 -->
		</div>
	</div>
	<div class="container-fluid">
		<div class="row">
			<div id="left" class="col-md-3">
				<div class="panel-heading">
					<button type="button" style="background-color: rgb(84, 121, 177); border: none; color: white; padding: 15px 32px; text-align: center; text-decoration: none; display: inline-block; font-size: 12px;" onclick="setDataCriteria()">RUN</button>
				</div>

				<div class="panel-divline"></div>
				<div style="height: 10px;"></div>

				<div>
					<label style="font-size: 20px; color: #31708F;">Life-log Setting</label>
				</div>

				<!-- 사이드 바 메뉴-->
				<div class="panel panel-info">
					<div class="expand panel-heading" href="#genderAge" data-toggle="collapse" aria-expanded="false">
						<h3 class="panel-title">
							<i id="genderAgeI" class="glyphicon glyphicon-expand"></i> <a class="panel-title">Gender&Age</a>
						</h3>
					</div>
					<ul id="genderAge" class="list-group collapse">
						<li class="list-group-item"><input type="checkbox" id="chkFemale" name="dataCriteria" checked> Female <input type="checkbox" id="chkMale" name="dataCriteria" checked> Male</li>
						<li class="list-group-item">Age <input type="number" style="width: 80px; height: 25px;" min="1" max="150" id="numAgeS" name="dataCriteria" placeholder="min Age"> To <input type="number" style="width: 80px; height: 25px;"
							min="1" max="150" id="numAgeE" name="dataCriteria" placeholder="max Age">
						</li>
					</ul>
				</div>
				<div class="panel panel-info">
					<div class="expand panel-heading" href="#lifeLog" data-toggle="collapse" aria-expanded="false">
						<h3 class="panel-title">
							<i id="lifeLogI" class="glyphicon glyphicon-expand"></i> <a class="panel-title">Life-log</a>
						</h3>
					</div>
					<ul id="lifeLog" class="list-group collapse py-2">
						<li class="list-group-item">Period <input type="text" style="width: 100px; height: 25px;" class="datepicker" placeholder="start date" id="dateObsS" name="dataCriteria"> To <input type="text"
							style="width: 100px; height: 25px;" class="datepicker" placeholder="end date" id="dateObsE" name="dataCriteria">
						</li>
						<li class="list-group-item"><input type="checkbox" id="chkBGL" name="dataCriteria" checked> Blood glucose level</li>
						<li class="list-group-item"><input type="checkbox" id="chkBW" name="dataCriteria" checked> Body weight</li>
						<li class="list-group-item"><input type="checkbox" id="chkDoS" name="dataCriteria" checked> Duration of sleep</li>
						<li class="list-group-item"><input type="checkbox" id="chkED" name="dataCriteria" checked> Exercise duration</li>
						<li class="list-group-item"><input type="checkbox" id="chkEI" name="dataCriteria" checked> Exercise intensity</li>
						<li class="list-group-item"><input type="checkbox" id="chkHB" name="dataCriteria" checked> Heart beat</li>
						<li class="list-group-item"><input type="checkbox" id="chkLoB" name="dataCriteria" checked> Length of body</li>
						<li class="list-group-item"><input type="checkbox" id="chkLoS" name="dataCriteria" checked> Level of stress</li>
						<li class="list-group-item"><input type="checkbox" id="chkNoSiUTP" name="dataCriteria" checked> Numbers of steps in unspecified time Pedometer</li>
						<li class="list-group-item"><input type="checkbox" id="chkSMoBP" name="dataCriteria" checked> Self-monitoring of blood pressure</li>
						<li class="list-group-item"><input type="checkbox" id="chkSSS" name="dataCriteria" checked> Sleep satisfaction score</li>
					</ul>
				</div>
				<div class="panel panel-info">
					<div class="expand panel-heading" href="#device" data-toggle="collapse" aria-expanded="false">
						<h3 class="panel-title">
							<i id="deviceI" class="glyphicon glyphicon-expand"></i> <a class="panel-title">Device</a>
						</h3>
					</div>
					<ul id="device" class="list-group collapse">
						<li class="list-group-item">Exposure(S) <input type="text" style="width: 100px; height: 25px;" class="datepicker" placeholder="start date" id="dateDevExpSS" name="dataCriteria"> To <input type="text"
							style="width: 100px; height: 25px;" class="datepicker" placeholder="end date" id="dateDevExpSE" name="dataCriteria"></li>
						<li class="list-group-item">Exposure(E) <input type="text" style="width: 100px; height: 25px;" class="datepicker" placeholder="start date" id="dateDevExpES" name="dataCriteria"> To <input type="text"
							style="width: 100px; height: 25px;" class="datepicker" placeholder="end date" id="dateDevExpEE" name="dataCriteria">
						</li>
						<li class="list-group-item"><input type="checkbox" id="chkLLPA" name="dataCriteria" checked> Life log platform : Apple Health-kit</li>
						<li class="list-group-item"><input type="checkbox" id="chkLLPS" name="dataCriteria" checked> Life log platform : Samsung S-Health</li>
						<li class="list-group-item"><input type="checkbox" id="chkPHRISAS" name="dataCriteria" checked> Patient health record information system application software</li>
					</ul>
				</div>

				<div class="panel-divline"></div>
				<div style="height: 10px;"></div>

				<div>
					<label style="font-size: 20px; color: #31708F;">Questionnaire Setting</label>
				</div>

				<div class="panel panel-info">
					<div class="expand panel-heading" href="#questionnaireResponse" data-toggle="collapse" aria-expanded="false">
						<h3 class="panel-title">
							<i id="questionnaireResponseI" class="glyphicon glyphicon-expand"></i> <a class="panel-title">Questionnaire</a>
						</h3>
					</div>
					<ul id="questionnaireResponse" class="list-group collapse">
						<li class="list-group-item">Period <input type="text" style="width: 100px; height: 25px;" class="datepicker" placeholder="start date" id="dateQRS" name="dataCriteria"> To <input type="text"
							style="width: 100px; height: 25px;" class="datepicker" placeholder="end date" id="dateQRSE" name="dataCriteria"></li>
					</ul>
				</div>

				<div class="panel-divline"></div>
				<div style="height: 10px;"></div>

				<div>
					<label style="font-size: 20px; color: #31708F;">Snapshot Setting</label>
				</div>

				<div class="panel panel-info">
					<div class="expand panel-heading" href="#bundle" data-toggle="collapse" aria-expanded="false">
						<h3 class="panel-title">
							<i id="bundleI" class="glyphicon glyphicon-expand"></i> <a class="panel-title">Snapshot</a>
						</h3>
					</div>
					<ul id="bundle" class="list-group collapse">
						<li class="list-group-item">Bundle ID <input type="text" style="width: 200px; height: 25px;" id="textBundleID" name="dataCriteriaText">
						</li>
					</ul>
				</div>

			</div>
			<div id="right" class="col-sm-9 main">
				<iframe name="mainFrame" id="mainFrame" class="page-iframe" src="" frameborder="0" style="background-color: white;"></iframe>
			</div>



		</div>
	</div>

	<script type="text/javascript">

				var client = new FHIR.client("${FHIR_SERVICE_ROOT_URI}");
				var dataCriteriaDatepickers;
				var dataCriteriaNumbers;
				var dataCriteriaTexts;
				var dataCriteriaCheckboxes;

				var patient;
				var observation;
				var device;
				var questionnaireResponse;
				var bundle;

				var defalutResourceCount = 2147483647;

				var timer;
				$(document).ready(function () {
					// set initial age value
					$('#numAgeS').val(1);
					$('#numAgeE').val(100);

					// set initial observation date
					$('#dateObsS').val('2019-01-01');
					$('#dateObsE').val('2019-01-02');

					// set initial questionare response date
					$('#dateQRS').val('2016-02-01');
					$('#dateQRSE').val('2016-02-01');

					setEvent();
					//initDataCriteria();			
					setDataCriteriaValue();
					$('#mainFrame').attr('src', "${FHIR_SERVICE_ROOT_URI}");
				});

				function initDataCriteria() {
					//var patient = new Object();
					patient = new Object();
					patient.gender = new Array();;
					patient.birthdateStart = null;
					patient.birthdateEnd = null;

					//var observation = new Object();
					observation = new Object();
					observation.effectiveDateTimeStart = null;
					observation.effectiveDateTimeEnd = null;
					observation.code = new Array();

					//var device = new Object();
					device = new Object();
					device.timingPeriodStartStart = null;
					device.timingPeriodStartEnd = null;
					device.timingPeriodEndStart = null;
					device.timingPeriodEndEnd = null;
					device.type = new Array();

					questionnaireResponse = new Object();
					questionnaireResponse.authoredStart = null;
					questionnaireResponse.authoredEnd = null;
					
					bundle = new Object();
					bundle.id = null;
				}

				function setDataCriteriaValue() {

					let enabledSettingsNuber = Array.from(dataCriteriaNumbers).filter(i => i.value > 0).map(i => i.id);
					let enabledSettingsdatepicker = Array.from(dataCriteriaDatepickers).filter(i => i.value.length > 0).map(i => i.id);
					let enabledSettingsCheck = Array.from(dataCriteriaCheckboxes).filter(i => i.checked).map(i => i.id)

					initDataCriteria();

					enabledSettingsNuber.forEach(function (item, index, list) {
						switch (item) {
							case "numAgeS":
								var age = $("#" + item).val();
								patient.birthdateStart = age
								break;
							case "numAgeE":
								var age = $("#" + item).val();
								patient.birthdateEnd = age;
								break;
							default:
								break;
						}
					});

					enabledSettingsdatepicker.forEach(function (item, index, list) {
						switch (item) {
							case "dateObsS":
								observation.effectiveDateTimeStart = $("#" + item).val();
								break;
							case "dateObsE":
								observation.effectiveDateTimeEnd = $("#" + item).val();
								break;
							case "dateDevExpSS":
								device.timingPeriodStartStart = $("#" + item).val();
								break;
							case "dateDevExpSE":
								device.timingPeriodStartEnd = $("#" + item).val();
								break;
							case "dateDevExpES":
								device.timingPeriodEndStart = $("#" + item).val();
								break;
							case "dateDevExpEE":
								device.timingPeriodEndEnd = $("#" + item).val();
								break;
							case "dateQRS":
								questionnaireResponse.authoredStart = $("#" + item).val();
								break;
							case "dateQRSE":
								questionnaireResponse.authoredEnd = $("#" + item).val();
								break;
							default:
								break;
						}
					});

					enabledSettingsCheck.forEach(function (item, index, list) {
						switch (item) {
							case "chkLLPA":
							case "chkLLPS":
							case "chkPHRISAS":
								device.type.push(item);
								break;
							case "chkFemale":
							case "chkMale":
								patient.gender.push(item);
								break;
							case "chkBGL":
							case "chkBW":
							case "chkDoS":
							case "chkED":
							case "chkEI":
							case "chkHB":
							case "chkLoB":
							case "chkLoS":
							case "chkNoSiUTP":
							case "chkSMoBP":
							case "chkSSS":
							default:
								observation.code.push(item);
								break;
						}
					});

					bundle.id = $("#textBundleID").val();

					console.log(enabledSettingsNuber);
					console.log(enabledSettingsdatepicker);
					console.log(enabledSettingsCheck);
				}

				function setDataCriteria() {

					setDataCriteriaValue();					

					if($('#mainFrame').contents().get(0) != undefined){
						$('#mainFrame')[0].contentWindow.search(patient, observation, device, questionnaireResponse, bundle);
					}	
				}
				
				function setEvent() {
					$('.datepicker').datepicker({
						calendarWeeks: false,
						todayHighlight: true,
						changeYear: true,
						changeMonth: true,
						autoclose: true,
						format: "yyyy-mm-dd",
						language: "kr",
						todayBtn: true,
						clearBtn: true
					}).on('changeDate', function (ev) {
						setDataCriteriaValue();
					});

					dataCriteriaDatepickers = document.querySelectorAll(".datepicker");
					dataCriteriaDatepickers.forEach(function (number) {
						number.addEventListener('change', function () {
							setDataCriteriaValue();
						})
					});

					dataCriteriaNumbers = document.querySelectorAll("input[type=number][name=dataCriteria]");
					dataCriteriaNumbers.forEach(function (number) {
						number.addEventListener('change', function () {
							setDataCriteriaValue();
						})
					});

					
					dataCriteriaTexts = document.querySelectorAll("input[type=text][name=dataCriteriaText]");
					dataCriteriaTexts.forEach(function (text) {
						text.addEventListener('change', function () {
							setDataCriteriaValue();
						})
					});
					
					dataCriteriaCheckboxes = document.querySelectorAll("input[type=checkbox][name=dataCriteria]");
					dataCriteriaCheckboxes.forEach(function (checkbox) {
						checkbox.addEventListener('change', function () {
							setDataCriteriaValue();
						})
					});
					
					$(".expand").on("click",function(){
						
						var href = $(this).attr('href');
						
						if (timer) clearTimeout(timer);
					    timer = setTimeout(function() { 						    
							$(href+'I').toggleClass("glyphicon-expand");
							$(href+'I').toggleClass("glyphicon-collapse-down");
					    }, 250); 
					});
									
				}
			</script>
</body>

</html>