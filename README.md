PGHDcdmTofhir
===
- CDM PGHD to FHIR(IG)의 서버 API 및 시각화 WEB

## 참조
- FHIR Implementation Guide [SNUBH CDM PGHD to FHIR](https://build.fhir.org/ig/HIRC-SNUBH/PGHD-CDMtoFHIR)
- [PGHD (Patient-Generated Health Data)의 OMOP CDM 변환 가이드라인](https://github.com/HIRC-SNUBH/PGHD_conversion_guide)

## 개발환경
|Tool|버전|비고|
|:---:|:---:|:---|
|JDK|1.8||
|Tomcat|8.5||
|Maven|3.8.1||
|Database|Oracle<br />Redshift|- Oracle Database 10g<br />- PostgreSQL 8.0.2, Redshift 1.0.12103|
|CDM|v5.3|CDM V6.0 추가<br />- OBSERVATION.observation_event_id<br />- OBSERVATION.obs_event_field_concept_id<br />- SURVEY_CONDUCT|

## 오픈소스
|이름|버전|비고|
|:---:|:---:|:---|
|HAPI FHIR|5.4.1|https://github.com/hapifhir/hapi-fhir
|client-js|2.1.0|https://github.com/smart-on-fhir/client-js
|chart.js|3.5.1|https://github.com/chartjs/Chart.js
|chartjs-chart-boxplot||https://github.com/sgratzl/chartjs-chart-boxplot
|chartjs-chart-sankey||https://github.com/kurkle/chartjs-chart-sankey
|fhirstarters||https://github.com/FirelyTeam/fhirstarters|

## 환경설정
### org-snubh-hirc-pghd-web
- FHIR 서버 API 및 시각화 WEB URL 설정
```
경로 : resources/config.properties

# 시각화 WEB URL
server.url=http://localhost:6001/org-snubh-hirc-pghd-web/
# FHIR 서버 API URL
fhirserver.url=http://localhost:6001/org-snubh-hirc-pghd-api/fhir/
```

- Logging 설정
```
경로 : resources/log4j2.xml

<Property name="fd-log-root">D:/ezCaretech/hiebin_dev/org-snubh-hirc-pghd-web</Property>
```
### org-snubh-hirc-pghd-api
- FHIR 서버 API 설정
```
경로 : resources/config.properties

# FHIR 서버 API URL
server.url=http://localhost:6001/org-snubh-hirc-pghd-api

server.name=HIRC-SNUBH-PGHD-API
server.version=1.0
server.date=2021-08-31
server.publisher=HIRC-SNUBH

# Paging Search Results
server.defaulcount=40
server.defaultoffset=0
```

- Logging 설정
```
경로 : resources/log4j2.xml

<Property name="fd-log-root">D:/ezCaretech/hiebin_dev/org-snubh-hirc-pghd-api</Property>
```

- redshift 설정
```
dataSource.driverClassName=com.amazon.redshift.jdbc42.Driver
dataSource.url=jdbc:redshift:{IP}:{PORT}/{DatabaseName}
dataSource.username={ID}
dataSource.password={Password}

dataSource.maxActive=8
dataSource.maxIdle=8
dataSource.minIdle=1
dataSource.ValidationQuery=select 1

hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
hibernate.show_sql=false
hibernate.format_sql=false
hibernate.packagesToScan=org.snubh.hirc.pghd.api.dto
```
- Oracle 설정
```
dataSource.driverClassName=oracle.jdbc.driver.OracleDriver
dataSource.url=jdbc:oracle:thin:@{IP}:{PORT}/{DatabaseName}
dataSource.username={ID}
dataSource.password={Password}

dataSource.maxActive=8
dataSource.maxIdle=8
dataSource.minIdle=1
dataSource.ValidationQuery=select 1 from dual

hibernate.dialect=org.hibernate.dialect.Oracle10gDialect
hibernate.show_sql=false
hibernate.format_sql=false
hibernate.packagesToScan=org.snubh.hirc.pghd.api.dto
```
## 실행
1. Application Server에 배포 하기 위해서는 먼저 프로젝트를 빌드 해야 합니다.
```
# Maven을 이용한 빌드
mvn clean install
```
2. 이렇게 하면 **target** 폴더에 **org-snubh-hirc-pghd-api-x.x.x.war** 또는 **org-snubh-hirc-pghd-web-x.x.x.war** 파일이 생성됩니다.
3. 파일명에서 버전 부분을 삭제합니다. org-snubh-hirc-pghd-api.war 또는 org-snubh-hirc-pghd-web.war
4. 해당 파일을 **Tomcat** 하위에 **webapps/** 디렉토리에 복사하고 Tomcat을 구동합니다.
5. 다음 링크를 통해서 서버에 접속 합니다.
  - 접속포트 6001은 서버 구성 방식에 따라 다를 수 있습니다.

* 시각화 WEB : <http://localhost:6001/org-snubh-hirc-pghd-web/>
* FHIR 서버 API : <http://localhost:6001/org-snubh-hirc-pghd-api/fhir/>

  

## 시각화 WEB UI
![Alt text](/org-snubh-hirc-pghd-web-01.png "시각화 WEB UI")
![Alt text](/org-snubh-hirc-pghd-web-02.png "시각화 WEB UI")
![Alt text](/org-snubh-hirc-pghd-web-03.png "시각화 WEB UI")
![Alt text](/org-snubh-hirc-pghd-web-04.png "시각화 WEB UI")
![Alt text](/org-snubh-hirc-pghd-web-05.png "시각화 WEB UI")

## 서버 API Test UI
![Alt text](/org-snubh-hirc-pghd-api-swagger-ui-01.png "서버 API Test UI")
