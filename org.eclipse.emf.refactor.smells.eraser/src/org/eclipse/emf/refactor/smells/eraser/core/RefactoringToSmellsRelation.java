package org.eclipse.emf.refactor.smells.eraser.core;

import java.util.Map;
import java.util.Set;

import org.eclipse.emf.refactor.refactoring.core.Refactoring;
import org.eclipse.emf.refactor.smells.core.ModelSmell;

/**
 * Singleton class wrapping the relation between model refactorings and possibly
 * caused model smells provided by extensions of EMF Fix'es extension point.
 * 
 * @author Matthias Burhenne
 */
public class RefactoringToSmellsRelation {
	public static final RefactoringToSmellsRelation INSTANCE = new RefactoringToSmellsRelation();
	private Map<Refactoring, Set<ModelSmell>> relationMap;
	
	private RefactoringToSmellsRelation(){
		relationMap = RelationLoader.loadRefactoringToSmellsRelation();
	}
	
	/**
	 * Return all the potentially caused smells for the given Refactoring.
	 * @param refactoring - the refactoring that is potentially causing the smells
	 * @return - Set of ModelSmell objects
	 */
	public Set<ModelSmell> getCausedSmells(Refactoring refactoring){
		return relationMap.get(refactoring);
	}
	
	
}
