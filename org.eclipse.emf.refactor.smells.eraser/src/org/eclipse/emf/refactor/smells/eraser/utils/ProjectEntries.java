package org.eclipse.emf.refactor.smells.eraser.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;

/**
 * A data class wrapping the relations between model smells and model refactorings defined by a target project.
 * The representation is done by stub objects.
 * 
 * @author Matthias Burhenne
 *
 */
public class ProjectEntries {
	
	private Map<ModelRefactoringStub, Set<ModelSmellStub>> causedSmells = new HashMap<ModelRefactoringStub, Set<ModelSmellStub>>();
	private Map<ModelSmellStub, Set<ModelRefactoringStub>> fixingRefactorings = new HashMap<ModelSmellStub, Set<ModelRefactoringStub>>();
	private Map<String, Set<String>> danglingCausedSmells = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> danglingFixingRefactorings = new HashMap<String, Set<String>>();
	
	private final IProject project;
	
	public ProjectEntries(IProject project){
		this.project = project;
	}

	/**
	 * Get all the caused smells for the given refactoring as defined in the entries of the project.
	 * 
	 * @param refactoring - the model refactoring stub causing the smells
	 * @return Set of ModelSmellStub objects
	 */
	public Set<ModelSmellStub> getCausedSmells(ModelRefactoringStub refactoring) {
		return causedSmells.get(refactoring);
	}

	/**
	 * Adds a set of model smell stubs to the entry of caused smells for the given model refactoring
	 * 
	 * @param refactoring - refactoring stub for the causing refactoring
	 * @param causedSmells - set of model smell stubs representing the caused smells
	 */
	public void addCausedSmells(ModelRefactoringStub refactoring, Set<ModelSmellStub> causedSmells) {
		Set<ModelSmellStub> smells = this.causedSmells.get(refactoring);
		if(smells == null)
			smells = new HashSet<ModelSmellStub>();
		smells.addAll(causedSmells);
		this.causedSmells.put(refactoring, smells);
	}

	/**
	 * Get all the fixing refactorings for the given smell as defined in the entries of the project.
	 * 
	 * @param smell - the model smell stub fixed by the refactoring
	 * @return Set of ModelRefactoringStub objects
	 */
	public Set<ModelRefactoringStub> getFixingRefactorings(ModelSmellStub smell) {
		return fixingRefactorings.get(smell);
	}

	public void addFixingRefactorings(ModelSmellStub smell, Set<ModelRefactoringStub> fixingRefactorings) {
		Set<ModelRefactoringStub> refactorings = this.fixingRefactorings.get(smell);
		if(refactorings == null)
			refactorings = new HashSet<ModelRefactoringStub>();
		refactorings.addAll(fixingRefactorings);
		this.fixingRefactorings.put(smell, refactorings);
	}

	public IProject getProject() {
		return project;
	}
	
	public Set<ModelSmellStub> getAllSmellsInFixingRelations(){
		return fixingRefactorings.keySet();
	}
	
	public Set<ModelRefactoringStub> getAllRefactoringsInCausingRelations(){
		return causedSmells.keySet();
	}
	
	public void addFixingRefactoring(ModelSmellStub smell, ModelRefactoringStub refactoring){
		HashSet<ModelRefactoringStub> refactorings = new HashSet<ModelRefactoringStub>();
		refactorings.add(refactoring);
		addFixingRefactorings(smell, refactorings);
	}
	
	public void addCausedSmell(ModelRefactoringStub refactoring, ModelSmellStub smell){
		HashSet<ModelSmellStub> smells = new HashSet<ModelSmellStub>();
		smells.add(smell);
		addCausedSmells(refactoring, smells);
	}
	
	public void setCausedSmells(ModelRefactoringStub refactoring, Set<ModelSmellStub> smells){
		this.causedSmells.put(refactoring, smells);
	}
	
	public void setFixingRefactorings(ModelSmellStub smell, Set<ModelRefactoringStub> refactorings){
		this.fixingRefactorings.put(smell, refactorings);
	}
	
	/**
	 * Clears all the relation definition wrapped in this object.
	 */
	public void clear(){
		causedSmells.clear();
		fixingRefactorings.clear();
		danglingCausedSmells.clear();
		danglingFixingRefactorings.clear();
	}

	/**
	 * Getter for the field danglingCausedSmells
	 * 
	 * @return Map<String, Set<String>> -  the dangling relations
	 */
	public Map<String, Set<String>> getDanglingCausedSmells() {
		return danglingCausedSmells;
	}

	/**
	 * Getter for the field danglingFixingRefactorings
	 * 
	 * @return Map<String, Set<String>> -  the dangling relations
	 */
	public Map<String, Set<String>> getDanglingFixingRefactorings() {
		return danglingFixingRefactorings;
	}
	
	/**
	 * Adds one dangling relation between a model smell and a fixing refactoring to the map
	 * of dangling fixing relations
	 * 
	 * @param smellID - id of the smell
	 * @param fixingRefactoringID - id of the fixing refactoring
	 */
	public void addDanglingFixingRelation(String smellID, String fixingRefactoringID){
		Set<String> refactoringIDs = danglingFixingRefactorings.get(smellID);
		if(refactoringIDs == null){
			refactoringIDs = new HashSet<String>();
		}
		refactoringIDs.add(fixingRefactoringID);
		danglingFixingRefactorings.put(smellID, refactoringIDs);
	}
	
	/**
	 * Adds one dangling relation between a model refactoring and the model smell it
	 * propably causes to the map of dangling causing relations
	 * 
	 * @param refactoringID - id of the refactoring
	 * @param causedSmellID - id of the caused smell
	 */
	public void addDanglingCausedSmellsRelation(String refactoringID, String causedSmellID){
		Set<String> smellIDs = danglingCausedSmells.get(refactoringID);
		if(smellIDs == null){
			smellIDs = new HashSet<String>();
		}
		smellIDs.add(causedSmellID);
		danglingCausedSmells.put(refactoringID, smellIDs);
	}
}
