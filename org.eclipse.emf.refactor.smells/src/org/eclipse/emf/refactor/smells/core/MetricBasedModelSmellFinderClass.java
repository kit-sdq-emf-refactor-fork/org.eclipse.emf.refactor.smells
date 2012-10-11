package org.eclipse.emf.refactor.smells.core;

import java.util.LinkedList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.interfaces.IModelSmellFinderClass;

/**
 * Abstract superclass for finder classes of metric based smells.
 * 
 * @author Matthias Burhenne
 *
 */

public abstract class MetricBasedModelSmellFinderClass implements IModelSmellFinderClass {

	private double limit = 0.0; // the limit for the smell. Will be 0.0 at loading time and later read from configuration
	
	@Override
	abstract public LinkedList<LinkedList<EObject>> findSmell(EObject arg0);
	
	public void setLimit(double limit){
		this.limit = limit;
	}
	
	public double getLimit(){
		return limit;
	}

}
