package org.eclipse.emf.refactor.smells.managers;

import java.util.LinkedList;

import org.eclipse.emf.refactor.smells.core.ModelSmell;
import org.eclipse.emf.refactor.smells.core.ModelSmellLoader;


/**
 * Central controller class of EMF Smells. The logic for all the use cases is represented in one
 * <br>- or more methods of this class.
 * 
 * @author Matthias Burhenne
 *
 */

public class ModelSmellManager {
	
	private static LinkedList<ModelSmell> allSmells = null;
	
	private static ModelSmellManager instance;
	
	private ModelSmellManager() {
		allSmells = ModelSmellLoader.loadModelSmells();
		System.out.println("ModelSmellManager initialized!");
	}
	
	public static ModelSmellManager getInstance() {
		if (instance == null) {
			instance = new ModelSmellManager();
		}
		return instance;
	}
	
	/**
	 * Loads a LinkedList of all smell plugins (wrapped into <i>ModelSmell</i> objects) 
	 * <br>- currently installed in the Eclipse instance.
	 * 
	 * @return all smells currently installed
	 */
	public static LinkedList<ModelSmell> getAllModelSmells() {
		if(allSmells == null)
			allSmells = ModelSmellLoader.loadModelSmells();
		return allSmells;
	}	
	
	/**
	 * Returns a LinkedList containing all selected metrics for a given project that are
	 * defined for the given meta model and the given context.
	 * 
	 * @param metamodel
	 * @param context
	 * 
	 * @return filteredMetrics
	 */
	public static LinkedList<ModelSmell> getFilteredMetrics(String metamodel, String context) {
		LinkedList<ModelSmell> filteredModelSmells = new LinkedList<ModelSmell>();
		for (ModelSmell modelSmell : getAllModelSmells()) {
			if (modelSmell.getMetamodel().equals(metamodel)) {
				filteredModelSmells.add(modelSmell);
			}
		}
		return filteredModelSmells;
	}
	
}
