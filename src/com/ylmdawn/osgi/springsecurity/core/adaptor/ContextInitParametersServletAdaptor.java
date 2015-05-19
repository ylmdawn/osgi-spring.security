/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package com.ylmdawn.osgi.springsecurity.core.adaptor;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.servlet.*;

public class ContextInitParametersServletAdaptor implements Servlet {

	private Servlet delegate;
	Properties initParameters;

	public ContextInitParametersServletAdaptor(final Servlet delegate, final Properties initParameters) {
		this.delegate = delegate;
		this.initParameters = initParameters;
	}

	public void init(final ServletConfig config) throws ServletException {
		delegate.init(new ServletConfigAdaptor(config));
	}

	public void service(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
		delegate.service(request, response);
	}

	public void destroy() {
		delegate.destroy();
	}

	public ServletConfig getServletConfig() {
		return delegate.getServletConfig();
	}

	public String getServletInfo() {
		return delegate.getServletInfo();
	}

	private class ServletConfigAdaptor implements ServletConfig {
		private ServletConfig config;
		private ServletContext context;

		public ServletConfigAdaptor(final ServletConfig config) {
			this.config = config;
			this.context = new ServletContextAdaptor(config.getServletContext());
		}

		public String getInitParameter(final String arg0) {
			return config.getInitParameter(arg0);
		}

		public Enumeration getInitParameterNames() {
			return config.getInitParameterNames();
		}

		public ServletContext getServletContext() {
			return context;
		}

		public String getServletName() {
			return config.getServletName();
		}
	}

	private class ServletContextAdaptor implements ServletContext {

		private ServletContext delegate;

		public ServletContextAdaptor(final ServletContext delegate) {
			this.delegate = delegate;
		}

		public RequestDispatcher getRequestDispatcher(final String path) {
			return delegate.getRequestDispatcher(path);
		}

		public URL getResource(final String name) throws MalformedURLException {
			return delegate.getResource(name);
		}

		public InputStream getResourceAsStream(final String name) {
			return delegate.getResourceAsStream(name);
		}

		public Set getResourcePaths(final String name) {
			return delegate.getResourcePaths(name);
		}

		public Object getAttribute(final String arg0) {
			return delegate.getAttribute(arg0);
		}

		public Enumeration getAttributeNames() {
			return delegate.getAttributeNames();
		}

		public ServletContext getContext(final String arg0) {
			return delegate.getContext(arg0);
		}

		public String getInitParameter(final String arg0) {
			return initParameters.getProperty(arg0);
		}

		public Enumeration getInitParameterNames() {
			return initParameters.propertyNames();
		}

		public int getMajorVersion() {
			return delegate.getMajorVersion();
		}

		public String getMimeType(final String arg0) {
			return delegate.getMimeType(arg0);
		}

		public int getMinorVersion() {
			return delegate.getMinorVersion();
		}

		public RequestDispatcher getNamedDispatcher(final String arg0) {
			return delegate.getNamedDispatcher(arg0);
		}

		public String getRealPath(final String arg0) {
			return delegate.getRealPath(arg0);
		}

		public String getServerInfo() {
			return delegate.getServerInfo();
		}

		/** @deprecated **/
		public Servlet getServlet(final String arg0) throws ServletException {
			return delegate.getServlet(arg0);
		}

		public String getServletContextName() {
			return delegate.getServletContextName();
		}

		/** @deprecated **/
		public Enumeration getServletNames() {
			return delegate.getServletNames();
		}

		/** @deprecated **/
		public Enumeration getServlets() {
			return delegate.getServlets();
		}

		/** @deprecated **/
		public void log(final Exception arg0, final String arg1) {
			delegate.log(arg0, arg1);
		}

		public void log(final String arg0, final Throwable arg1) {
			delegate.log(arg0, arg1);
		}

		public void log(final String arg0) {
			delegate.log(arg0);
		}

		public void removeAttribute(final String arg0) {
			delegate.removeAttribute(arg0);
		}

		public void setAttribute(final String arg0, final Object arg1) {
			delegate.setAttribute(arg0, arg1);
		}
		
		// Added in Servlet 2.5
		public String getContextPath() {
			try {
				Method getContextPathMethod = delegate.getClass().getMethod("getContextPath", null); //$NON-NLS-1$
				return (String) getContextPathMethod.invoke(delegate, null);
			} catch (Exception e) {
				// ignore
			}
			return null;
		}
	}
}
