package com.ylmdawn.osgi.springsecurity.core;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private BundleContext context;

	public void start(final BundleContext bundleContext) throws Exception {
		this.context = bundleContext;
		this.context
				.registerService(
						"org.springframework.osgi.context.event.OsgiBundleApplicationContextListener",
						new WebBundleListener(bundleContext), null);
	}

	public void stop(final BundleContext bundleContext) throws Exception {
		this.context
				.ungetService(this.context
						.getServiceReference("org.springframework.osgi.context.event.OsgiBundleApplicationContextListener"));
		this.context = null;
	}

}
