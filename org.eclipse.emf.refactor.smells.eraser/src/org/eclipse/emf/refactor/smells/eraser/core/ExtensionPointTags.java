package org.eclipse.emf.refactor.smells.eraser.core;

/**
 * Class containing only the xml tags necessary to access the definitions of relations between model smells and
 * model refactorings inside the defining plugin.xml files.
 * 
 * @author Matthias Burhenne
 *
 */
public class ExtensionPointTags {
	
	public static final String EXTENSION_POINT_NAME_SMELL_TO_REFACTORINGS = "org.eclipse.emf.refactor.smelltorefactorings";
	public static final String SMELL_TO_REFACTORING_TAG = "smell_to_refactorings";
	
	public static final String EXTENSION_POINT_NAME_REFACTORING_TO_SMELLS = "org.eclipse.emf.refactor.refactoringtosmells";
	public static final String REFACTORING_TO_SMELL_TAG = "refactoring_to_smells";
	
	public static final String REFACTORING_TO_SMELLS_SMELL_ELEMENT_TAG = "smell";
	public static final String SMELL_TO_REFACTORINGS_REFACTORING_ELEMENT_TAG = "refactoring";
	
	public static final String SMELL_ID_TAG = "id";
	public static final String REFACTORING_ID_TAG = "id";

}
