package org.eclipse.emf.refactor.smells.eraser.utils;

import org.eclipse.emf.refactor.smells.core.ModelSmell;

/**
 * Stub class being used by the property page to represent model smells defined in the
 * currently active workspace as well as those from plugins.
 * 
 * @author Matthias Burhenne
 *
 */
public class ModelSmellStub {
	private final String id;
	private final String name;
	private final String description;
	private final String metamodel;
	
	public ModelSmellStub(String id, String name, String description, String metamodel){
		this.id = id;
		this.name = name;
		this.description = description;
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
	 * Getter for the field description
	 * 
	 * @return - description as a String
	 */
	public String getDescription() {
		return description;
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
	 * Returns a ModelSmellStub object representing the passed ModelSmell
	 * @param smell - the smell that is to be converted
	 * @return ModelSmellStub
	 */
	public static ModelSmellStub convertModelSmell(ModelSmell smell){
		return new ModelSmellStub(smell.getId(),
				smell.getName(),
				smell.getDescription(),
				smell.getMetamodel());
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
		ModelSmellStub other = (ModelSmellStub) obj;
		if (id == null) {
			if (other.getId() != null)
				return false;
		} else if (!id.equals(other.getId()))
			return false;
		return true;
	}
}
