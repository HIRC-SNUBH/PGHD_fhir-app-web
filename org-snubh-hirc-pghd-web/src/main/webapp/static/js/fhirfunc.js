/*
* @Description : URL Query String 생성
 * @Params
  - resourceType : FHIR Resource Type
  - key : URL query param (key)
  - value : URL query param (value)
 * @Return : String
*/ 
function setFhirQueryString(resourceType, key, value) {

    var queryString = null;
    //var option = "&_count=0";
    var option = "&_summary=count";
    
    if (resourceType != null) {
        queryString = "/" + resourceType;
        if (key != null) {
            queryString = queryString + "?" + key + "=";
            if (value != null) {
                queryString = queryString + value;
            }
        }
        return queryString + option;
    } else {
        console.error("Check parameters. resourceType / key / value");
    }    
}


/* 
 * @Description : extract Bundle.total value
 * @Params
  - data : FHIR.client.request() 의 Response. Type : Promise<Object>  
 * @Return : Bundle.total
*/ 
function getResourceCount(data) {
    if (data != null) {
        var jsonString = JSON.stringify(data, null, 4);
        var jsonData = JSON.parse(jsonString);
        return jsonData.total; // Bundle.total
    } else {
        return null;
    }
}

function convertObservationCategoryToCode (checked) {

    var codeVal = null;

    switch (checked) {
        case "chkBGL":
            codeVal = '248334005';
            return codeVal;
        case "chkBW":
            codeVal = '997671000000106';
            return codeVal;
        case "chkDoS":
            codeVal = '924481000000109';
            return codeVal;
        case "chkED":
            codeVal = '27113001';
            return codeVal;
        case "chkEI":
            codeVal = 'SNUBH0046';
            return codeVal;
        case "chkHB":
            codeVal = '248263006';
            return codeVal;
        case "chkLoB":
            codeVal = '55423-8';
            return codeVal;
        case "chkLoS":
            codeVal = '405052004';
            return codeVal;
        case "chkNoSiUTP":
            codeVal = '248646004';
            return codeVal;
        case "chkSMoBP":
            codeVal = '74008-4';
            return codeVal;
        case "chkSSS":
            codeVal = '55411-3';
            return codeVal;
    }
}