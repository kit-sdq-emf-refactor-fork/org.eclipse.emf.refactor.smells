package org.eclipse.emf.refactor.smells.eraser.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.refactor.refactoring.core.Refactoring;
import org.eclipse.emf.refactor.smells.core.ModelSmell;
import org.eclipse.emf.refactor.smells.eraser.managers.EraseManager;

/**
 * Utility class for loading the relation definitions from EMF Fix'es Extension Points
 * 
 * @author Matthias Burhenne
 *
 */
public class RelationLoader {

	/**
	 * Loads the relations between model refactorings and their caused smells.
	 * 
	 * @return HashMap<Refactoring, Set<ModelSmell>> - map representing the relation
	 */
	public static HashMap<Refactoring, Set<ModelSmell>> loadRefactoringToSmellsRelation() {
		HashMap<Refactoring, Set<ModelSmell>> relationMap = new HashMap<Refactoring, Set<ModelSmell>>();
		IConfigurationElement[] configElements = Platform.getExtensionRegistry().getConfigurationElementsFor(ExtensionPointTags.EXTENSION_POINT_NAME_REFACTORING_TO_SMELLS);
		for (IConfigurationElement element : configElements) {
			if (element.getName().equals(ExtensionPointTags.REFACTORING_TO_SMELL_TAG)) {					
				final String refactoringID = element.getAttribute(ExtensionPointTags.REFACTORING_ID_TAG);
				HashSet<String> smellIDs = new HashSet<String>();
				for(IConfigurationElement relationElementChild : element.getChildren()){
					if(relationElementChild.getName().equals(ExtensionPointTags.REFACTORING_TO_SMELLS_SMELL_ELEMENT_TAG)){
						smellIDs.add(relationElementChild.getAttribute(ExtensionPointTags.SMELL_ID_TAG));
					}
				}
				Refactoring refactoring = EraseManager.getInstance().getRefactoring(refactoringID);
				for(String smellIDFromList : smellIDs){
					ModelSmell smell = EraseManager.getInstance().getSmell(smellIDFromList);
					if(refactoring != null && smell != null){
						if(relationMap.containsKey(refactoring)){
							relationMap.get(refactoring).add(smell);
						}else{
							HashSet<ModelSmell> value = new HashSet<ModelSmell>();
							value.add(smell);
							relationMap.put(refactoring, value);
						}
					}
				}
			}
		}
		return relationMap;
	}

	/**
	 * Loads the relations between model smells and their fixing smells.
	 * 
	 * @return HashMap<ModelSmell, Set<Refactoring>> - map representing the relation
	 */
	public static HashMap<ModelSmell, Set<Refactoring>> loadSmellToRefactoringsRelation() {
		HashMap<ModelSmell, Set<Refactoring>> relationMap = new HashMap<ModelSmell, Set<Refactoring>>();
		IConfigurationElement[] configElements = Platform.getExtensionRegistry().getConfigurationElementsFor(ExtensionPointTags.EXTENSION_POINT_NAME_SMELL_TO_REFACTORINGS);
		for(IConfigurationElement element : configElements){
			if(element.getName().equals(ExtensionPointTags.SMELL_TO_REFACTORING_TAG)){
				final String smellID = element.getAttribute(ExtensionPointTags.SMELL_ID_TAG);
				HashSet<String> refactoringIDs = new HashSet<String>();
				for(IConfigurationElement relationElementChild : element.getChildren()){
					if(relationElementChild.getName().equals(ExtensionPointTags.SMELL_TO_REFACTORINGS_REFACTORING_ELEMENT_TAG)){
						refactoringIDs.add(relationElementChild.getAttribute(ExtensionPointTags.REFACTORING_ID_TAG));
					}
				}
				ModelSmell smell = EraseManager.getInstance().getSmell(smellID);
				for(String refactoringID : refactoringIDs){
					Refactoring refactoring = EraseManager.getInstance().getRefactoring(refactoringID);				
					if(refactoring != null && smell != null){
						if(relationMap.containsKey(smell)){
							relationMap.get(smell).add(refactoring);
						}else{
							HashSet<Refactoring> value = new HashSet<Refactoring>();
							value.add(refactoring);
							relationMap.put(smell, value);
						}
					}
				}
			}
		}
		return relationMap;
	}

}
