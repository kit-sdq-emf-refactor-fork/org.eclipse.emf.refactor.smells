package org.eclipse.emf.refactor.smells.eraser.utils;

import org.eclipse.emf.refactor.refactoring.core.Refactoring;


/**
 * Stub class being used by the property page to represent model refactorings defined in the
 * currently active workspace as well as those from plugins.
 * 
 * @author Matthias Burhenne
 *
 */
public class ModelRefactoringStub {
	private final String id;
	private final String name;
	private final String metamodel;
	
	public ModelRefactoringStub(String id, String name, String metamodel){
		this.id = id;
		this.name = name;
		this.metamodel = metamodel;
	}

	/**
	 * Getter for the field id
	 * 
	 * @return - id as a String
	 */
	public String getId() {
		return id;
	}

	/**
	 * Getter for the field name
	 * 
	 * @return - name as a String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter for the field metamodel
	 * 
	 * @return - metamodel as a String
	 */
	public String getMetamodel() {
		return metamodel;
	}
	
	/**
	 * Returns a ModelRefactoringStub object representing the passed Refactoring
	 * @param refactoring - the refactoring that is to be converted
	 * @return ModelRefactoringStub
	 */
	public static ModelRefactoringStub convertRefactoring(Refactoring refactoring){
		return new ModelRefactoringStub(refactoring.getId(),
				refactoring.getName(),
				refactoring.getNamespaceUri());
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelRefactoringStub other = (ModelRefactoringStub) obj;
		if (id == null) {
			if (other.getId() != null)
				return false;
		} else if (!id.equals(other.getId()))
			return false;
		return true;
	}
	
}
