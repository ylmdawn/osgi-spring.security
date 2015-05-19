package com.ylmdawn.osgi.springsecurity.core;

import java.lang.reflect.Method;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.osgi.context.event.OsgiBundleApplicationContextEvent;
import org.springframework.osgi.context.event.OsgiBundleApplicationContextListener;
import org.springframework.osgi.context.event.OsgiBundleContextClosedEvent;
import org.springframework.osgi.context.event.OsgiBundleContextRefreshedEvent;


/**
 * 监听Web Bundle变化.<br>
 * 
 * @author yangli <br>
 * @version 1.0.0 2015-4-21<br>
 * @see
 * @since JDK 1.5.0
 */
public class WebBundleListener implements OsgiBundleApplicationContextListener {

	private BundleContext bundleContext;
	private Hashtable<ApplicationContext, WebHttpServiceTracker> httpServiceTrackersMap = new Hashtable<ApplicationContext, WebHttpServiceTracker>();
	private static final String HTTP_SERVICE_CONFIG_BEAN_NAME = "WebhttpServiceConfig";

	public WebBundleListener(final BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	public void onOsgiApplicationEvent(final OsgiBundleApplicationContextEvent event) {
		if ((event instanceof OsgiBundleContextRefreshedEvent))
			processRefreshEvent(event);
		else if ((event instanceof OsgiBundleContextClosedEvent))
			processCloseEvent(event);
	}

	private void processRefreshEvent(final OsgiBundleApplicationContextEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
		HttpServiceConfig httpServiceConfig = null;
		try {
			httpServiceConfig = (HttpServiceConfig) applicationContext
					.getBean(HTTP_SERVICE_CONFIG_BEAN_NAME);
		} catch (NoSuchBeanDefinitionException localNoSuchBeanDefinitionException) {
			return;
		}
		OsgiBundleContextRefreshedEvent refreshedEvent = (OsgiBundleContextRefreshedEvent) event;

		ClassLoader oriClassLoader = Thread.currentThread().getContextClassLoader();
		ClassLoader bundleClassLoader = getBundleClassLoader(refreshedEvent.getBundle());
		try {
			Thread.currentThread().setContextClassLoader(bundleClassLoader);
			WebHttpServiceTracker httpServiceTracker = new WebHttpServiceTracker(this.bundleContext, httpServiceConfig, event);
			this.httpServiceTrackersMap.put(applicationContext,httpServiceTracker);
			httpServiceTracker.open();
		} finally {
			Thread.currentThread().setContextClassLoader(oriClassLoader);
		}
	}

	private void processCloseEvent(final OsgiBundleApplicationContextEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
		WebHttpServiceTracker tracker = (WebHttpServiceTracker) this.httpServiceTrackersMap.get(applicationContext);
		if (tracker != null)
			tracker.close();
	}

	private ClassLoader getBundleClassLoader(final Bundle bundle) {
		try {
			Method method = bundle.getClass().getDeclaredMethod(
					"getBundleLoader", null);
			method.setAccessible(true);
			Object bundleLoader = method.invoke(bundle, null);
			Method method2 = bundleLoader.getClass().getDeclaredMethod(
					"createClassLoader", null);
			method2.setAccessible(true);
			return (ClassLoader) method2.invoke(bundleLoader, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
