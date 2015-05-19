/*******************************************************************************
 * Copyright (c) 2007 Cognos Incorporated, IBM Corporation and others
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ContextPathServletAdaptor implements Servlet {

	private Servlet delegate;
	String contextPath;

	public ContextPathServletAdaptor(final Servlet delegate, final String contextPath) {
		this.delegate = delegate;
		this.contextPath = (contextPath == null || contextPath.equals("/")) ? "" : contextPath; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void init(final ServletConfig config) throws ServletException {
		delegate.init(new ServletConfigAdaptor(config));
	}

	public void service(final ServletRequest request, final ServletResponse response) throws ServletException, IOException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		// httpReq.setAttribute("com.sun.faces.INVOCATION_PATH","/web/faces");
		delegate.service(new HttpServletRequestAdaptor(httpReq), response);
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
			if (contextPath.equals("/")) //$NON-NLS-1$
				return new RequestDispatcherAdaptor(delegate.getRequestDispatcher(path));
			return new RequestDispatcherAdaptor(delegate.getRequestDispatcher(contextPath + path));
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
			return delegate.getInitParameter(arg0);
		}

		public Enumeration getInitParameterNames() {
			return delegate.getInitParameterNames();
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
			return new RequestDispatcherAdaptor(delegate.getNamedDispatcher(arg0));
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

	private class HttpServletRequestAdaptor extends HttpServletRequestWrapper {
		static final String INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri"; //$NON-NLS-1$
		static final String INCLUDE_CONTEXT_PATH_ATTRIBUTE = "javax.servlet.include.context_path"; //$NON-NLS-1$
		static final String INCLUDE_SERVLET_PATH_ATTRIBUTE = "javax.servlet.include.servlet_path"; //$NON-NLS-1$
		static final String INCLUDE_PATH_INFO_ATTRIBUTE = "javax.servlet.include.path_info"; //$NON-NLS-1$
		private boolean isRequestDispatcherInclude;

		public HttpServletRequestAdaptor(final HttpServletRequest req) {
			super(req);
			isRequestDispatcherInclude = req.getAttribute(HttpServletRequestAdaptor.INCLUDE_REQUEST_URI_ATTRIBUTE) != null;
		}

		public String getServletPath() {
			if (isRequestDispatcherInclude)
				return super.getServletPath();

			String fullPath = super.getServletPath();
			return fullPath.substring(contextPath.length());
		}

		public String getContextPath() {
			if (isRequestDispatcherInclude)
				return super.getContextPath();

			if ("/faces".equals(contextPath)) {
				return super.getContextPath();
			} else {
				return super.getContextPath() + contextPath;
			}
		}

		public Object getAttribute(final String attributeName) {
			if (isRequestDispatcherInclude) {
				if (attributeName.equals(HttpServletRequestAdaptor.INCLUDE_CONTEXT_PATH_ATTRIBUTE)) {
					String contextPathAttribute = (String) super
							.getAttribute(HttpServletRequestAdaptor.INCLUDE_CONTEXT_PATH_ATTRIBUTE);
					if (contextPathAttribute == null || contextPathAttribute.equals("/")) //$NON-NLS-1$
						return contextPath;

					return contextPathAttribute + contextPath;
				} else if (attributeName.equals(HttpServletRequestAdaptor.INCLUDE_SERVLET_PATH_ATTRIBUTE)) {
					String servletPath = (String) super
							.getAttribute(HttpServletRequestAdaptor.INCLUDE_SERVLET_PATH_ATTRIBUTE);
					return servletPath.substring(contextPath.length());
				}
			}
			return super.getAttribute(attributeName);
		}

		public RequestDispatcher getRequestDispatcher(final String arg0) {
			if ("/faces".equals(contextPath)) {
				return new RequestDispatcherAdaptor(super.getRequestDispatcher(arg0));
			} else {
				return new RequestDispatcherAdaptor(super.getRequestDispatcher(contextPath + arg0));
			}
		}
	}

	private class RequestDispatcherAdaptor implements RequestDispatcher {

		private RequestDispatcher requestDispatcher;

		public RequestDispatcherAdaptor(final RequestDispatcher requestDispatcher) {
			this.requestDispatcher = requestDispatcher;
		}

		public void forward(ServletRequest req, final ServletResponse resp) throws ServletException, IOException {
			if (req instanceof HttpServletRequestAdaptor)
				req = ((HttpServletRequestAdaptor) req).getRequest();

			requestDispatcher.forward(req, resp);
		}

		public void include(ServletRequest req, final ServletResponse resp) throws ServletException, IOException {
			if (req instanceof HttpServletRequestAdaptor)
				req = ((HttpServletRequestAdaptor) req).getRequest();

			requestDispatcher.include(req, resp);
		}
	}
}
