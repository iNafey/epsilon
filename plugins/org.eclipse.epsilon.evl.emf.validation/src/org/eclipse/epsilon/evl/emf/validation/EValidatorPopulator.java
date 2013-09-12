/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package org.eclipse.epsilon.evl.emf.validation;

import java.net.URL;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.epsilon.common.dt.util.LogUtil;
import org.eclipse.ui.IStartup;

public class EValidatorPopulator implements IStartup {
	
	protected final String extensionPoint = "org.eclipse.epsilon.evl.emf.validation";
	
	public void earlyStartup() {
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extenstionPoint = registry.getExtensionPoint(extensionPoint);
		
		for (IConfigurationElement configurationElement : extenstionPoint.getConfigurationElements()) {
			
			try {
				String ePackageUri = configurationElement.getAttribute("namespaceURI");
				EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(ePackageUri);
				if (ePackage == null) continue;
				
				String bundleId = configurationElement.getAttribute("bundleId");
				
				if (bundleId == null || bundleId.trim().length() == 0) {
					bundleId = configurationElement.getDeclaringExtension().getNamespaceIdentifier();
				}
				
				URL url = Platform.getBundle(bundleId).getResource(configurationElement.getAttribute("constraints"));
				
				if (url == null) {
					LogUtil.log("Constraints file " + 
						configurationElement.getAttribute("constraints") + 
						" not found in bundle " + bundleId, 
						new Exception());
					continue;
				}
				
				EValidator evlValidator = null;
				if (url.toString().endsWith("evl")) {
					String modelName = configurationElement.getAttribute("modelName");
					if (modelName == null || modelName.trim().length() == 0) modelName = EvlValidator.DEFAULT_MODEL_NAME;

					// Use custom EvlValidator if provided: otherwise, use the default implementation
					if(configurationElement.getAttribute("validator") != null) {
						evlValidator = (EValidator) configurationElement.createExecutableExtension("validator");
					} else {
						evlValidator = new EvlValidator();
					}
					((EvlValidator) evlValidator).initialise(url.toURI(), modelName, ePackageUri, bundleId);

					// Add variables for propagating EMF Diagnostician context entries
					IConfigurationElement[] diagnosticVariables = configurationElement.getChildren("diagnosticVariable");
					for (IConfigurationElement diagnosticVariable : diagnosticVariables) {
						((EvlValidator) evlValidator).addDiagnosticianVariable(diagnosticVariable.getAttribute("name"));
					}
				}
				else {
					evlValidator = new OclValidator(url.toURI());
				}
				
				EValidator newValidator = null;
				EValidator existingValidator = EValidator.Registry.INSTANCE.getEValidator(ePackage);
				
				String composeAttributeValue = configurationElement.getAttribute("compose");
				boolean compose = composeAttributeValue == null || Boolean.valueOf(composeAttributeValue).booleanValue();
				
				if (compose) {
					if (existingValidator == null) {
						existingValidator = EObjectValidator.INSTANCE;
					}
					
					if (existingValidator instanceof CompositeEValidator) {
						((CompositeEValidator) existingValidator).getDelegates().add(evlValidator);
						newValidator = existingValidator;
					}
					else {
						//newValidator = existingValidator;
						newValidator = new CompositeEValidator();
						((CompositeEValidator) newValidator).getDelegates().add(evlValidator);
						((CompositeEValidator) newValidator).getDelegates().add(existingValidator);
					}
				}
				else {
					newValidator = evlValidator;
				}
				
				if (newValidator != existingValidator) {
					EValidator.Registry.INSTANCE.put(ePackage, newValidator);
				}
				
			} catch (Exception e) {
				LogUtil.log(e);
			}
		}
		
	}

}
