package org.snubh.hirc.pghd.api.config;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseConformance;
import org.hl7.fhir.r4.model.CapabilityStatement;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementImplementationComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceOperationComponent;
import org.hl7.fhir.r4.model.CapabilityStatement.CapabilityStatementRestResourceSearchParamComponent;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Enumerations.ResourceType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.interceptor.api.Hook;
import ca.uhn.fhir.interceptor.api.Interceptor;
import ca.uhn.fhir.interceptor.api.Pointcut;

@Interceptor
@Component
public class CapabilityStatementCustomizer {

	@Value("${server.url}")
	private String url;

	@Value("${server.publisher}")
	private String publisher;

	@Value("${server.name}")
	private String name;

	@Value("${server.version}")
	private String version;

	@Value("${server.date}")
	private String date;

	@Value("${server.profileurl}")
	private String profileurl;

	private final List<String> BASE_RESOURCE_TYPE = new ArrayList<String>();

	public CapabilityStatementCustomizer() {
		super();
		BASE_RESOURCE_TYPE.add(ResourceType.BUNDLE.toCode());
		BASE_RESOURCE_TYPE.add(ResourceType.STRUCTUREDEFINITION.toCode());

	}

	@Hook(Pointcut.SERVER_CAPABILITY_STATEMENT_GENERATED)
	public void customize(IBaseConformance theCapabilityStatement) throws UnknownHostException {

		String profileBaseUrl = generateBaseProfileUrl();
		CapabilityStatement cs = (CapabilityStatement) theCapabilityStatement;
		generateCapabilityBaseValue(cs);
		generateImplementationValue(cs);

		for (CapabilityStatementRestComponent comp : cs.getRest()) {
			for (CapabilityStatementRestResourceComponent resComp : comp.getResource()) {
				resComp.setSearchInclude(null);
				resComp.setSearchRevInclude(null);
				if (!BASE_RESOURCE_TYPE.contains(resComp.getType())) {
					resComp.setProfile(String.format("%spghd-cdm-%s", profileBaseUrl, resComp.getType().toLowerCase()));
				}
				if (resComp.getType().equals(ResourceType.OBSERVATION.toCode())) {
					resComp.addSupportedProfile(String.format("%s/pghd-cdm-observationcomponent", profileBaseUrl));
				}
				setSearchParam(resComp);
				setOperation(resComp);
			}
		}
	}

	private void setSearchParam(CapabilityStatementRestResourceComponent resComp) {
		List<CapabilityStatementRestResourceSearchParamComponent> searchParamList = resComp.getSearchParam();
		for (CapabilityStatementRestResourceSearchParamComponent searchParamComponent : searchParamList) {
			searchParamComponent.setDefinition(null);
			searchParamComponent.setDocumentation(null);
		}
	}

	private void setOperation(CapabilityStatementRestResourceComponent resComp) {
		List<CapabilityStatementRestResourceOperationComponent> operationList = resComp.getOperation();
		for (CapabilityStatementRestResourceOperationComponent operation : operationList) {
			operation.setDocumentation(null);
		}
	}

	private void generateImplementationValue(CapabilityStatement cs) {
		CapabilityStatementImplementationComponent value = new CapabilityStatementImplementationComponent();
		value.setUrl(String.format("%s/fhir", url));
		value.setDescription(publisher);
		cs.setImplementation(value);
	}

	private void generateCapabilityBaseValue(CapabilityStatement cs) {
		DateTimeType dateTimeType = new DateTimeType(date);
		cs.getSoftware().setName(name).setVersion(version).setReleaseDateElement(dateTimeType);
		cs.setPublisher(publisher);
		cs.setName(name);
		cs.setDateElement(dateTimeType);
	}

	private String generateBaseProfileUrl() throws UnknownHostException {
		String profileUrl = String.format("%s/StructureDefinition/", profileurl);
		return profileUrl;
	}

}
