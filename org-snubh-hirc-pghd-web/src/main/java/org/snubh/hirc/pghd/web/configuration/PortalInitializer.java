package org.snubh.hirc.pghd.web.configuration;

import javax.servlet.Filter;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class PortalInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] { PortalConfiguration.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return null;// new Class[] { WebMvcServlet.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Filter[] getServletFilters() {

		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter("UTF-8", true, true);
		Filter[] singleton = { new CORSFilter(), new MultipartFilter(), characterEncodingFilter };
		return singleton;

	}

	@Override
	protected WebApplicationContext createRootApplicationContext() {
		WebApplicationContext context = (WebApplicationContext) super.createRootApplicationContext();
		return context;
	}

}