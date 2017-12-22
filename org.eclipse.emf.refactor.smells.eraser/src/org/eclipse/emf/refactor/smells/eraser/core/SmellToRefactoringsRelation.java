package org.eclipse.emf.refactor.smells.eraser.core;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.refactor.refactoring.core.Refactoring;
import org.eclipse.emf.refactor.smells.core.ModelSmell;

/**
 * Singleton class wrapping the relation between model smells and possibly
 * fixing model refactorings provided by extensions of EMF Fix'es extension point.
 * 
 * @Author Matthias Burhenne
 */
public class SmellToRefactoringsRelation {
	public static final SmellToRefactoringsRelation INSTANCE = new SmellToRefactoringsRelation();
	private Map<ModelSmell, Set<Refactoring>> relationMap;
	
	private SmellToRefactoringsRelation(){
		relationMap = RelationLoader.loadSmellToRefactoringsRelation();
	}
	
	/**
	 * Returns the model refactorings potentially fixing the given ModelSmell.
	 * 
	 * @param smell -  the model smell that is to be fixed
	 * @return Set of Refactoring objects
	 */
	public Set<Refactoring> getFixingRefactorings(ModelSmell smell){
		Set<Refactoring> result = relationMap.get(smell);
		if(result != null)
			return relationMap.get(smell);
		else
			return new HashSet<Refactoring>();
	}
	
}
