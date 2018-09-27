/*********************************************************************
* Copyright (c) 2008 The University of York.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* Contributors:
*     Horacio Hoyos - initial API and implementation
**********************************************************************/
package org.eclipse.epsilon.etl.dt.launching.tabs;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.epsilon.common.dt.EpsilonPlugin;
import org.eclipse.epsilon.common.dt.launching.tabs.AbstractAdvancedConfigurationTab;
import org.eclipse.epsilon.etl.dt.EtlPlugin;

/**
 * The Class EtlAdvancedConfigurationTab.
 * @since 1.6
 * @author Horacio Hoyos Rodriguez
 */
public class EtlAdvancedConfigurationTab extends AbstractAdvancedConfigurationTab {

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	@Override
	public EpsilonPlugin getPlugin() {
		return EtlPlugin.getDefault();
	}

	@Override
	public String getLanguage(ILaunchConfiguration configuration) {
		return getLanguage();
	}
	
	/**
	 * The language provided by the plugin. It allows other plugins to contribute
	 * alternate IModule implementation of the language.
	 * @since 1.6
	 */
	public String getLanguage() {
		return "ETL";
	}

}
