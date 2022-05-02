package org.snubh.hirc.pghd.api.config;

import java.util.Map;

import javax.servlet.ServletException;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.openapi.OpenApiInterceptor;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.LoggingInterceptor;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;

public class RestfulServlet extends RestfulServer {

	private final static ApplicationContext APPLICATION_CONTEXT = ContextLoader.getCurrentWebApplicationContext();
	private final static FhirContext FHIR_CONTEXT = FhirContext.forR4();

	public static ApplicationContext getApplicationContext() {
		return APPLICATION_CONTEXT;
	}

	public static FhirContext getFhirContextR4() {
		return FHIR_CONTEXT;
	}

	private static final long serialVersionUID = 1782470245648331760L;

	public RestfulServlet() {
	}

	@Override
	public void initialize() throws ServletException {
		super.initialize();

		setFhirContext(FHIR_CONTEXT);
		Map<String, IResourceProvider> map = APPLICATION_CONTEXT.getBeansOfType(IResourceProvider.class);
		setResourceProviders(map.values());

		OpenApiInterceptor openApiInterceptor = new OpenApiInterceptor();
		registerInterceptor(openApiInterceptor);

		registerInterceptor(new ResponseHighlighterInterceptor());

		CapabilityStatementCustomizer capabilityStatement = APPLICATION_CONTEXT.getBean(CapabilityStatementCustomizer.class);
		registerInterceptor(capabilityStatement);

		LoggingInterceptor loggingInterceptor = new LoggingInterceptor();
		loggingInterceptor.setLoggerName("accesslog");
		loggingInterceptor.setMessageFormat("Success Source[${remoteAddr}] Operation[${operationType} ${idOrResourceName}] Params[${requestParameters}] Time[${processingTimeMillis}] UA[${requestHeader.user-agent}]");
		loggingInterceptor
				.setErrorMessageFormat("Failure Source[${remoteAddr}] Operation[${operationType} ${idOrResourceName}] Params[${requestParameters}] Time[${processingTimeMillis}] UA[${requestHeader.user-agent}] Error[${exceptionMessage}]");
		registerInterceptor(loggingInterceptor);
	}
}
