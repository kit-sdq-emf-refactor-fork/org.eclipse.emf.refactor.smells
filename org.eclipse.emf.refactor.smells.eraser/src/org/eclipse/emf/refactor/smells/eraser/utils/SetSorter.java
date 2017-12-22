package org.eclipse.emf.refactor.smells.eraser.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.refactor.refactoring.core.Refactoring;
import org.eclipse.emf.refactor.smells.core.ModelSmell;

/**
 * Utility class providing functionality to sort sets of model smells/refactorings and converting them to
 * sorted lists.
 * 
 * @author Matthias Burhenne
 *
 */
public class SetSorter {
	
	/**
	 * Sorts a set of ModelSmell objects by their name.
	 * 
	 * @param smellSet - the set of smells
	 * @return - the sorted list.
	 */
	public static List<ModelSmell> sortSmellSet(Set<ModelSmell> smellSet){
		if(smellSet.isEmpty())
			return new ArrayList<ModelSmell>();
		ModelSmell[] array = new ModelSmell[1];
		array = smellSet.toArray(array);
		if(array.length > 1){
			for(int i = 0; i < array.length; i++){
				for(int j = 0; j < array.length; j++){
					if(array[i].getName().compareTo(array[j].getName()) < 0){
						swap(array, i, j);
					}
				}
			}
		}
		ArrayList<ModelSmell> result = new ArrayList<ModelSmell>();
		for(ModelSmell smell : array){
			result.add(smell);
		}
		return result;
	}
	
	/**
	 * Sorts a set of Refactoring objects by their name.
	 * 
	 * @param refactoringSet - the set of refactorings
	 * @return - the sorted list.
	 */
	public static List<Refactoring> sortRefactoringSet(Set<Refactoring> refactoringSet){
		if(refactoringSet.isEmpty()){
			return new ArrayList<Refactoring>();
		}
		Refactoring[] array = new Refactoring[1];
		array = refactoringSet.toArray(array);
		if(array.length > 1){
			for(int i = 0; i < array.length; i++){
				for(int j = 0; j < array.length; j++){
					if(array[i].getName().compareTo(array[j].getName()) < 0){
						swap(array, i, j);
					}
				}
			}
		}
		ArrayList<Refactoring> result = new ArrayList<Refactoring>();
		for(Refactoring refactoring : array){
			result.add(refactoring);
		}
		return result;
	}
	
	/*
	 * Helper method to swap two items in an array
	 */
	private static void swap(Object[] array, int i, int j){
		Object temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}

	/**
	 * Sorts a set of ModelSmellStub objects by their name.
	 * 
	 * @param smellStubs - the set of stubs
	 * @return - the sorted list.
	 */
	public static List<ModelSmellStub> sortSmellStubSet(Set<ModelSmellStub> smellStubs) {
		if(smellStubs.isEmpty()){
			return new ArrayList<ModelSmellStub>();
		}
		ModelSmellStub[] array = new ModelSmellStub[1];
		array = smellStubs.toArray(array);
		if(array.length > 1){
			for(int i = 0; i < array.length; i++){
				for(int j = 0; j < array.length; j++){
					if(array[i].getName().compareTo(array[j].getName()) < 0){
						swap(array, i, j);
					}
				}
			}
		}
		ArrayList<ModelSmellStub> result = new ArrayList<ModelSmellStub>();
		for(ModelSmellStub stub : array){
			result.add(stub);
		}
		return result;
	}
	
	/**
	 * Sorts a set of ModelRefactoringStub objects by their name.
	 * 
	 * @param refactoringStubs - the set of stubs
	 * @return - the sorted list.
	 */
	public static List<ModelRefactoringStub> sortRefactoringStubSet(Set<ModelRefactoringStub> refactoringStubs) {
		if(refactoringStubs.isEmpty()){
			return new ArrayList<ModelRefactoringStub>();
		}
		ModelRefactoringStub[] array = new ModelRefactoringStub[1];
		array = refactoringStubs.toArray(array);
		if(array.length > 1){
			for(int i = 0; i < array.length; i++){
				for(int j = 0; j < array.length; j++){
					if(array[i].getName().compareTo(array[j].getName()) < 0){
						swap(array, i, j);
					}
				}
			}
		}
		ArrayList<ModelRefactoringStub> result = new ArrayList<ModelRefactoringStub>();
		for(ModelRefactoringStub stub : array){
			result.add(stub);
		}
		return result;
	}
}
