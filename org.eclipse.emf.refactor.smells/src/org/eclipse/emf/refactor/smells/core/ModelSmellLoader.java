package org.eclipse.emf.refactor.smells.core;

import java.util.LinkedList;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;

/**
 * Loader class for the installed EMF Smells plugins. It wraps the plugins into
 * <br>- <i>ModelSmell</i> objects which are then used by EMF Smells.
 * 
 * @author Matthias Burhenne
 *
 */

public class ModelSmellLoader {
	
	/**
	 * Loads all smell plugins currently installed an returns them
	 * <br>- as a list of <i>ModelSmell</i> objects.
	 * @return list of <i>ModelSmell</i> objects wrapping all currently installed smell plugins
	 */
	public static LinkedList<ModelSmell> loadModelSmells() {
		LinkedList<ModelSmell> smells = new LinkedList<ModelSmell>();
		IConfigurationElement[] rawSmells = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(ExtensionPointTags.EXTENSION_POINT_NAME);
		for (IConfigurationElement element : rawSmells) {
			try {
				if (element.getName().equals(ExtensionPointTags.MODELSMELL_TAG)) {	
					System.out.println(element);
					ModelSmell smell = createModelSmell(element);
					if (null != smell) {
						smells.add(smell);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		java.util.Collections.sort(smells);
		return smells;
	}
	
	private static ModelSmell createModelSmell(IConfigurationElement element){
		try {
			final String name = element.getAttribute(ExtensionPointTags.MODELSMELL_NAME_TAG);
			final String id = element.getAttribute(ExtensionPointTags.MODELSMELL_ID_TAG);
			final String description = element
					.getAttribute(ExtensionPointTags.MODELSMELL_DESCRIPTION_TAG);
			final String metamodel = element
					.getAttribute(ExtensionPointTags.MODELSMELL_METAMODEL_TAG);
			final IModelSmellFinder finderClass = (IModelSmellFinder) 
					element.createExecutableExtension(ExtensionPointTags.MODELSMELL_FINDER_CLASS_TAG);
			return new ModelSmell(name, description, metamodel, finderClass, id);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static IConfigurationElement[] getExtensions(){
		return Platform.getExtensionRegistry()
		.getConfigurationElementsFor(ExtensionPointTags.EXTENSION_POINT_NAME);
	}
}
