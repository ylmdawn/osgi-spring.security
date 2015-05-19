package com.ylmdawn.osgi.springsecurity.core;



import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.context.ApplicationContext;
import org.springframework.osgi.context.event.OsgiBundleApplicationContextEvent;

import com.ylmdawn.osgi.springsecurity.core.adaptor.FilterServletAdaptor;
import com.ylmdawn.osgi.springsecurity.core.wrapper.SpringSecurityValidateServletWrapper;

/**
 * 统一安全服务注册监听器.<br>
 * @author yangli <br>
 * @version 1.0.0 2015-5-14<br>
 * @see 
 * @since JDK 1.5.0
 */
public class WebHttpServiceTracker extends ServiceTracker {
	
	private BundleContext bundleContext;
	private HttpServiceConfig httpServiceConfig;
	private OsgiBundleApplicationContextEvent event;
	

	public WebHttpServiceTracker(final BundleContext context,final HttpServiceConfig httpServiceConfig,final OsgiBundleApplicationContextEvent event) {
		super(context, HttpService.class.getName(), null);
		this.bundleContext = context;
		this.httpServiceConfig = httpServiceConfig;
		this.event = event;
	}

	public Object addingService(final ServiceReference reference) {
		HttpService service = (HttpService) this.bundleContext.getService(reference);
		if (service != null) {
			try {
				ApplicationContext applicationContext = this.event.getApplicationContext();
				// 注册统一安全验证入口.
				service.registerServlet(httpServiceConfig.getBaseUrl(), SpringSecurityServletWrapper(applicationContext), null, null);
				
//				if (LOG.isInfoEnabled())
//					LOG.info("注册统一资源安全验证访问入口[" + httpServiceConfig.getBaseUrl() + "]成功");
			} catch (Exception e) {
//				LOG.error("注册Web资源异常", e);
			}
		}
		return service;
	}

	public void removedService(final ServiceReference reference, final Object service) {
		HttpService httpService = (HttpService) service;
		if (service != null) {
			try {
				httpService.unregister(httpServiceConfig.getBaseUrl());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		super.removedService(reference, service);
	}
	
	/**
	 * 通过Spring Security验证后的servlet.
	 * @param applicationContext 当前应用场景
	 * @return
	 */
	private Servlet SpringSecurityServletWrapper(final ApplicationContext applicationContext) {
		//Filter门面,FilterChainProxy.
		//TODO 加入服务监听机制.
		Filter springSecurityFilterChain = null;
		try {
			springSecurityFilterChain = (Filter) applicationContext.getBean("springSecurityFilterChain");
		} catch (Exception e) {
//			LOG.warn("未获取到springSecurityFilterChain");
		} finally {
			if (null != springSecurityFilterChain) {
				bundleContext.registerService("org.springframework.security.web.FilterChainProxy", springSecurityFilterChain, null);
			}else{
				ServiceReference service = bundleContext.getServiceReference("org.springframework.security.web.FilterChainProxy");
				if(null != service){
					springSecurityFilterChain = (Filter) bundleContext.getService(service);
				}
				bundleContext.ungetService(service);
			}
		}
//		if (LOG.isInfoEnabled())
//			LOG.info("安全控制过滤器链注册情况："+springSecurityFilterChain);
		//Servlet门面.
		SpringSecurityValidateServletWrapper springSecurityValidateServletWrapper = new SpringSecurityValidateServletWrapper();
		springSecurityValidateServletWrapper.setApplicationContext(applicationContext);
		springSecurityValidateServletWrapper.setBundleContext(bundleContext);
		springSecurityValidateServletWrapper.setWebconfig(httpServiceConfig);
		return new FilterServletAdaptor(springSecurityFilterChain, null, springSecurityValidateServletWrapper);
	}

}
