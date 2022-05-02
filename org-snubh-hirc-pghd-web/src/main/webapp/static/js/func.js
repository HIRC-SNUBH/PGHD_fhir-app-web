// var REST_SERVICE_ROOT_URI = "http://localhost:8070/web/";
// var REST_SERVICE_ROOT_URI = "http://192.168.20.143:8070/web/";
// var FHIR_SERVICE_ROOT_URI = "http://localhost:8080/fhir/";
// var FHIR_SERVICE_ROOT_URI = "http://192.168.20.143:8080/fhir/";

/** FHIR *
//const client = new FHIR.client(FHIR_SERVICE_ROOT_URI);

/** Controller */
function auto_date_format(e, oThis) {
  // var len = oThis.value.length;
  // var numValue = oThis.value.replace(/[^0-9]/g, '');
  // var len = numValue.length;
  // if (len == 8) {
  //   oThis.value = numValue.substring(0, 4) + '-' + numValue.substring(4, 6) + '-' + numValue.substring(6, 8);
  // } else if (len == 7) {
  //   oThis.value = numValue.substring(0, 4) + '-' + numValue.substring(4, 6) + '-' + numValue.substring(6, 7);
  // } else if (len == 6) {
  //   oThis.value = numValue.substring(0, 4) + '-' + numValue.substring(4, 6);
  // }
}

// Date yyyy-mm-dd 포맷 적용
function getFormatDate(date) {
  var year = date.getFullYear();
  var month = date.getMonth() + 1;
  month = month >= 10 ? month : "0" + month;
  var day = date.getDate();
  day = day >= 10 ? day : "0" + day;
  return [year, month, day].join("-");
}

// DataCriteria > Observation 설정항목을 select UI에 연동
// @param selectId - select UI ID
// @param observation - parent.observation
function setObservationCategoryToSelectUI(selectId, observation) {
  if (selectId != null) {
    var $oSelect = $("#" + selectId);

    $oSelect.empty().append(new Option("All Category", "all", true, true)); // 기본 설정

    //Observation.code 적용
    if (
      typeof observation != "undefined" &&
      typeof observation.code != "undefined" &&
      observation.code != null &&
      observation.code.length > 0
    ) {
      observation.code.forEach((element) => {
        switch (element) {
          case "chkBGL":
            $oSelect.append(
              new Option("Blood glucose level", 248334005, false, false)
            );
            break;
          case "chkBW":
            $oSelect.append(
              new Option("Body weight", 997671000000106, false, false)
            );
            break;
          case "chkDoS":
            $oSelect.append(
              new Option("Duration of sleep", 924481000000109, false, false)
            );
            break;
          case "chkED":
            $oSelect.append(
              new Option("Exercise duration", 27113001, false, false)
            );
            break;
          case "chkEI":
            $oSelect.append(
              new Option("Exercise intensity", "SNUBH0046", false, false)
            );
            break;
          case "chkHB":
            $oSelect.append(new Option("Heart beat", 248263006, false, false));
            break;
          case "chkLoB":
            $oSelect.append(
              new Option("Length of body", "55423-8", false, false)
            );
            break;
          case "chkLoS":
            $oSelect.append(
              new Option("Level of stress", 405052004, false, false)
            );
            break;
          case "chkNoSiUTP":
            $oSelect.append(
              new Option(
                "Number of steps in unspecifed time Pedometer",
                248646004,
                false,
                false
              )
            );
            break;
          case "chkSMoBP":
            $oSelect.append(
              new Option(
                "Seif-monitoring of blood pressure",
                "74008-4",
                false,
                false
              )
            );
            break;
          case "chkSSS":
            $oSelect.append(
              new Option("Sleep satisfaction score", "55411-3", false, false)
            );
            break;
        }
      });
    }
    $oSelect.selectpicker("refresh");
  }
}

function getString(str) {
  if (str != undefined && str != null && str.length > 0) return str;
  else return null;
}

function getGender(name) {
  var category = new Object();
  category.name = "";
  category.code = "";

  switch (name) {
    case "chkFemale":
      category.code = "female";
      category.name = "Female";
      break;
    case "chkMale":
      category.code = "male";
      category.name = "Male";
      break;
    default:
      category = undefined;
      break;
  }
  return category;
}

function getDeviceType(name) {
  var category = new Object();
  category.name = "";
  category.code = "";

  switch (name) {
    case "chkLLPA":
      category.code = "SNUBH0010";
      category.name = "Apple Health Kit";
      break;
    case "chkLLPS":
      category.code = "SNUBH0009";
      category.name = "Samsung S-Health";
      break;
    case "chkPHRISAS":
      category.code = "462894001";
      category.name =
        "Patient health record information system-application software";
      break;
    default:
      category = undefined;
      break;
  }
  return category;
}

function getObservationCategory(name) {
  var category = new Object();
  category.name = "";
  category.code = "";

  switch (name) {
    case "chkBGL":
      category.code = "997671000000106";
      category.name = "Blood glucose level";
      break;
    case "chkBW":
      category.code = "27113001";
      category.name = "Body weight";
      break;
    case "chkDoS":
      category.code = "248263006";
      category.name = "Duration of sleep";
      break;
    case "chkED":
      category.code = "55411-3";
      category.name = "Exercise duration";
      break;
    case "chkEI":
      category.code = "74008-4";
      category.name = "Exercise intensity";
      break;
    case "chkHB":
      category.code = "248646004";
      category.name = "Heart beat";
      break;
    case "chkLoB":
      category.code = "248334005";
      category.name = "Length of body";
      break;
    case "chkLoS":
      category.code = "405052004";
      category.name = "Level of stress";
      break;
    case "chkNoSiUTP":
      category.code = "55423-8";
      category.name = "Number of steps in unspecifed time Pedometer";
      break;
    case "chkSMoBP":
      category.code = "924481000000109";
      category.name = "Seif-monitoring of blood pressure";
      break;
    case "chkSSS":
      category.code = "SNUBH0046";
      category.name = "Sleep satisfaction score";
      break;
    default:
      category = undefined;
      break;
  }

  return category;
}

function setSelectObservationCategory(id, observation) {
  var select = $("#" + id)[0];
  select.options.length = 0;
  select.append(new Option("All Category", "all", true, true));

  observation.code.forEach(function (item) {
    var category = getObservationCategory(item);
    if (category != undefined) {
      select.append(new Option(category.name, category.code, false, false));
    }
  });
}

function setSelectTime(id) {
  var select = $("#" + id)[0];
  select.append(new Option("Yearly", "0", true, false));
  select.append(new Option("Monthly", "1", false, false));
  select.append(new Option("Weekly", "2", false, false));
  select.append(new Option("Daily", "3", false, false));
}

function setSelectGroup(id) {
  var select = $("#" + id)[0];
  select.append(new Option("AGE_GROUP", "age", true, false));
  select.append(new Option("GENDER", "gender", false, false));
}

window.colors = {
  red: "rgb(255, 99, 132)",
  orange: "rgb(255, 159, 64)",
  yellow: "rgb(255, 205, 86)",
  green: "rgb(75, 192, 192)",
  blue: "rgb(54, 162, 235)",
  purple: "rgb(153, 102, 255)",
  gray: "rgb(201, 203, 207)",
  lime: "rgb(0, 255, 0)",
  cyan: "rgb(0, 255, 255)",
  silver: "rgb(192, 192, 192)",
  navy: "rgb(0, 0, 128)",
};

window.colors2 = {
  red: "rgb(255, 99, 132, 0.2)",
  orange: "rgb(255, 159, 64, 0.2)",
  yellow: "rgb(255, 205, 86, 0.2)",
  green: "rgb(75, 192, 192, 0.2)",
  blue: "rgb(54, 162, 235, 0.2)",
  purple: "rgb(153, 102, 255, 0.2)",
  gray: "rgb(201, 203, 207, 0.2)",
  lime: "rgb(0, 255, 0, 0.2)",
  cyan: "rgb(0, 255, 255, 0.2)",
  silver: "rgb(192, 192, 192, 0.2)",
  navy: "rgb(0, 0, 128, 0.2)",
};

//주차수 출력
// Date.prototype.getWeek = function () {
//   var onejan = new Date(this.getFullYear(), 0, 1);
//   return Math.ceil(((this - onejan) / 86400000 + onejan.getDay() + 1) / 7);
// };

/**
 * Returns the week number for this date.  dowOffset is the day of week the week
 * "starts" on for your locale - it can be from 0 to 6. If dowOffset is 1 (Monday),
 * the week returned is the ISO 8601 week number.
 * @param int dowOffset
 * @return int
 */
 Date.prototype.getWeek = function (dowOffset) {  
  
      dowOffset = typeof(dowOffset) == 'number' ? dowOffset : 0; //default dowOffset to zero
      var newYear = new Date(this.getFullYear(),0,1);
      var day = newYear.getDay() - dowOffset; //the day of week the year begins on
      day = (day >= 0 ? day : day + 7);
      var daynum = Math.floor((this.getTime() - newYear.getTime() - 
      (this.getTimezoneOffset()-newYear.getTimezoneOffset())*60000)/86400000) + 1;
      var weeknum;
      
      //if the year starts before the middle of a week
      if(day < 4) {
          weeknum = Math.floor((daynum+day-1)/7) + 1;
          // if(weeknum > 52) {
          //     let nYear = new Date(this.getFullYear() + 1,0,1);
          //     let nday = nYear.getDay() - dowOffset;
          //     nday = nday >= 0 ? nday : nday + 7;
          //     /*if the next year starts before the middle of
          //       the week, it is week #1 of that year*/
          //     weeknum = nday < 4 ? 1 : 53;
          // }
      }
      else {
          weeknum = Math.floor((daynum+day-1)/7);
      }
      return weeknum;
  };

//date 내림차순 정렬
function date_descending(a, b) {
  var dateA = new Date(a["date"]).getTime();
  var dateB = new Date(b["date"]).getTime();
  return dateA > dateB ? 1 : -1;
}

function getAgeGroup(age) {
  var ageGroup = null;
  if (age != null) {
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
  }
  return ageGroup;
}

function checkDataCriteria(patient, observation) {
  var returnBoolean = true;

  if (patient.gender.length < 1) {
    returnBoolean = false;
  }

  if (
    observation.effectiveDateTimeStart == null ||
    observation.effectiveDateTimeEnd == null
  ) {
    returnBoolean = false;
  }

  if (observation.code.length < 1) {
    returnBoolean = false;
  }
  return returnBoolean;
}
