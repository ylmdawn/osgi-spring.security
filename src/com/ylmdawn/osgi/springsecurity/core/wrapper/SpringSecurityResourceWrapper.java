package com.ylmdawn.osgi.springsecurity.core.wrapper;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 资源处理门面.<br>
 * 包括对两类资源进行处理： 1.静态资源. 2.动态资源:1）统一动态资源context;2）自定义动态资源.
 * 
 * @author Administrator <br>
 * @version 1.0.0 2015-5-13<br>
 * @see
 * @since JDK 1.5.0
 */
public class SpringSecurityResourceWrapper implements Servlet {

	/**
	 * servlet config信息.
	 */
	private ServletConfig config = null;
	/**
	 * 源Servlet.
	 */
	private Servlet servlet = null;

	public SpringSecurityResourceWrapper(final Servlet servlet,
			final ServletConfig newServletconfig) throws ServletException {
		this.servlet = servlet;
		init(newServletconfig);
	}

	public void init(final ServletConfig newServletconfig)
			throws ServletException {
		this.config = newServletconfig;
		servlet.init(config);
	}

	public ServletConfig getServletConfig() {
		return servlet.getServletConfig();
	}

	public String getServletInfo() {
		return servlet.getServletInfo();
	}

	public void service(final ServletRequest newServletrequest,
			final ServletResponse newServletresponse) throws ServletException,
			IOException {
		servlet.service(newServletrequest, newServletresponse);
	}

	public void destroy() {
		servlet.destroy();
	}

}
