package org.eclipse.emf.refactor.smells.interfaces;

import java.util.LinkedList;

import org.eclipse.emf.ecore.EObject;

/**
 * Interface for finder classes of the respective smell plugins.
 * 
 * @author Matthias Burhenne
 *
 */

public interface IModelSmellFinder {
	
	abstract public LinkedList<LinkedList<EObject>> findSmell(EObject root);
	
}
