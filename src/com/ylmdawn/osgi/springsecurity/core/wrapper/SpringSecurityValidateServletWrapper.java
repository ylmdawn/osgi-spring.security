package com.ylmdawn.osgi.springsecurity.core.wrapper;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.osgi.context.BundleContextAware;

import com.ylmdawn.osgi.springsecurity.core.HttpServiceConfig;
import com.ylmdawn.osgi.springsecurity.core.adaptor.ResourceServlet;

/**
 * 用于拦截Http动态资源请求，并通过Spring Security进行统一安全验证.<br>
 * 
 * @author yangli <br>
 * @version 1.0.0 2015-4-22<br>
 * @see
 * @since JDK 1.5.0
 */
public class SpringSecurityValidateServletWrapper extends HttpServlet implements
		BundleContextAware, ApplicationContextAware {

	/**
	 * 序列化ID.
	 */
	private static final long serialVersionUID = 2454649716682919777L;

	/**
	 * Spring容器的Context.
	 */
	private ApplicationContext applicationContext = null;
	/**
	 * OSGI容器的Context.
	 */
	private BundleContext bundleContext = null;

	/**
	 * web config 对象.
	 */
	private HttpServiceConfig webconfig = null;

	/**
	 * 持有一个无状态的内部类servlet,处理静态资源.
	 */
	private Servlet resourceWrapper = null;

	/**
	 * 持有一个无状态的内部类servlet,处理context资源.
	 */
	private Servlet contextWrapper = null;

	/**
	 * @return the applicationContext
	 */
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * @param applicationContext
	 *            the applicationContext to set
	 */
	public void setApplicationContext(
			final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * @return the bundleContext
	 */
	public BundleContext getBundleContext() {
		return bundleContext;
	}

	/**
	 * @param bundleContext
	 *            the bundleContext to set
	 */
	public void setBundleContext(final BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	/**
	 * 获取webconfig.
	 * 
	 * @return the webconfig
	 */
	public HttpServiceConfig getWebconfig() {
		return webconfig;
	}

	/**
	 * 设置webconfig.
	 * 
	 * @param newWebconfig
	 *            the webconfig to set
	 */
	public void setWebconfig(final HttpServiceConfig newWebconfig) {
		webconfig = newWebconfig;
	}

	@Override
	protected void doHead(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		doPost(req, resp);
	}

	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		doAction(req, resp);
	}

	/**
	 * 处理经过安全验证后的资源.
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doAction(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		// HTTP请求的资源地址.
		String path = req.getPathInfo();
		// 判断资源类型.
		/*if ("/webcontext".equals(path)) {
			// 处理context统一动态资源.
			doContextRequest(req, resp);
		} else */
		if (null != webconfig.getServletconfig().get(path)) {
			// 处理servlet资源.
			doServletRequest(req, resp);
		} else {
			// 处理静态资源.
			doResourceRequest(req, resp);
		}
	}

/*	*//**
	 * 统一处理context资源请求.
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 *//*
	private void doContextRequest(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		if (null == contextWrapper) {
			//TODO 完善处理统一context服务Servlet.
			contextWrapper = new SpringSecurityResourceWrapper(
					null, this.getServletConfig());
		}
		contextWrapper.service(req, resp);
	}*/

	/**
	 * 统一处理静态资源请求.
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doResourceRequest(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		if (null == resourceWrapper) {
			resourceWrapper = new SpringSecurityResourceWrapper(
					new ResourceServlet(getWebconfig().getWebRoot()),
					this.getServletConfig());
		}
		resourceWrapper.service(req, resp);
	}

	/**
	 * 分别处理自定义servlet请求.
	 * 
	 * @param req
	 * @param resp
	 * @param beanname
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doServletRequest(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {
		// HTTP请求的资源地址.
		String path = req.getPathInfo();
		String beanname = webconfig.getServletconfig().get(path);
		Servlet servletWrapper = (Servlet) applicationContext.getBean(beanname);
		servletWrapper.init(this.getServletConfig());
		servletWrapper.service(req, resp);
	}
}