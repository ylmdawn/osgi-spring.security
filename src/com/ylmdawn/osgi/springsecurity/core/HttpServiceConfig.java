package com.ylmdawn.osgi.springsecurity.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 电商平台Web HttpService Config.<br>
 * 
 * @author yangli <br>
 * @version 1.0.0 2015-4-22<br>
 * @see
 * @since JDK 1.5.0
 */
public class HttpServiceConfig {

	/**
	 * webroot参数,静态资源的根目录名称.
	 */
	private final static String PARAM_WEBROOT = "webroot";

	/**
	 * baseurl参数,注册资源访问的根路径.
	 */
	private final static String PARAM_BASEURL = "baseurl";

	/**
	 * 
	 * 配置webconfig参数。目前支持的keys：<br>
	 * webroot：静态资源存放的更目录，value值如：webcontent.
	 * baseurl:资源发布根路径，不区分动态和静态资源.value值如：/ec.platform.
	 */
	private static Map<String, String> webconfig;

	/**
	 * 配置自定义servlet的访问路径. key:访问的URL，在根目录baseurl之内. value:servlet bean name.
	 */
	private static Map<String, String> servletconfig;

	/**
	 * 构造函数.
	 */
	public HttpServiceConfig() {
		webconfig = new HashMap<String, String>();
		servletconfig = new HashMap<String, String>();
	}

	/**
	 * 获取webconfig.
	 * 
	 * @return the webconfig
	 */
	public static Map<String, String> getWebconfig() {
		return webconfig;
	}

	/**
	 * 设置webconfig.
	 * 
	 * @param newWebconfig
	 *            the webconfig to set
	 */
	public static void setWebconfig(final Map<String, String> newWebconfig) {
		webconfig = newWebconfig;
	}

	/**
	 * 获取servletconfig.
	 * 
	 * @return the servletconfig
	 */
	public static Map<String, String> getServletconfig() {
		return servletconfig;
	}

	/**
	 * 设置servletconfig.
	 * 
	 * @param newServletconfig
	 *            the servletconfig to set
	 */
	public static void setServletconfig(
			final Map<String, String> newServletconfig) {
		servletconfig = newServletconfig;
	}

	/**
	 * 获取静态资源根目录.
	 * 
	 * @return
	 */
	public String getWebRoot() {
		return webconfig.get(PARAM_WEBROOT);
	}

	/**
	 * 获取资源发布根路径.
	 * 
	 * @return
	 */
	public String getBaseUrl() {
		return webconfig.get(PARAM_BASEURL);
	}

}
