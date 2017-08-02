package org.bac.gati.tools.journalexplorer.rse.base;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ISphereJournalExplorerRSEBasePlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.bac.gati.tools.journalexplorer.rse.base";

	// The shared instance
	private static ISphereJournalExplorerRSEBasePlugin plugin;
	
	/**
	 * The constructor
	 */
	public ISphereJournalExplorerRSEBasePlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ISphereJournalExplorerRSEBasePlugin getDefault() {
		return plugin;
	}

}
