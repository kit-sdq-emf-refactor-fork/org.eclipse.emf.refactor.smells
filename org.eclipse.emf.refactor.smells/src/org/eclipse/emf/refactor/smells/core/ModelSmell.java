package org.eclipse.emf.refactor.smells.core;

import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinder;

/**
 * Wrapper class for the single smell plugins.
 * 
 * @author Matthias Burhenne
 *
 */

public class ModelSmell implements Comparable<ModelSmell> {
	private String id;
	private String name;
	private String description;
	private String metamodel;
	private IModelSmellFinder finderClass;

	/**
	 * The constructor that creates a new <i>ModelSmell</i> object.
	 * 
	 * @param name
	 * @param description
	 * @param metamodel
	 * @param calculateClass
	 * @param id
	 */
	public ModelSmell(String name, String description, String metamodel,
			IModelSmellFinder finderClass, String id) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.metamodel = metamodel;
		this.finderClass = finderClass;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		if (description == null)
			return " - ";
		return description;
	}

	public String getMetamodel() {
		return metamodel;
	}

	public IModelSmellFinder getFinderClass() {
		return finderClass;
	}

	@Override
	public String toString() {
		return "EMF Model Smell: " + this.name + "(" + this.id + ")";
	}

	@Override
	public int compareTo(ModelSmell smell) {
		if(this.equals(smell)) {
			return 0;
		}
		if(this.getName().equals(smell.getName())) {
			return 0;
		} else {
		    return this.getName().compareTo(smell.getName());	
		}
	}
	
	
}
