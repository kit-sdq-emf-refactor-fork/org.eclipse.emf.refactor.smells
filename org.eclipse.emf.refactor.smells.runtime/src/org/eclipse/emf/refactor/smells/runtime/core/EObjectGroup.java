package org.eclipse.emf.refactor.smells.runtime.core;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.core.ModelSmell;


/**
 * Class holding a set of EObjects which are constituting a smell on a model.
 * 
 * @author Matthias Burhenne
 *
 */

public class EObjectGroup {
	ModelSmell smell;
	LinkedList<EObject> eObjects;
	ModelSmellResult modelSmellResult;
	
	public EObjectGroup(ModelSmell smell, LinkedList<EObject> eObjects){
		this.eObjects = eObjects;
		this.smell = smell;
	}

	public LinkedList<EObject> getEObjects() {
		return eObjects;
	}
	
	public ModelSmell getModelSmell(){
		return smell;
	}

	public void setEObjects(List<EObject> eObjects) {
		LinkedList<EObject> newEObjects = new LinkedList<EObject>();
		newEObjects.addAll(eObjects);
		this.eObjects = newEObjects;
	}

	public ModelSmellResult getModelSmellResult() {
		return modelSmellResult;
	}

	public void setModelSmellResult(ModelSmellResult smellresult) {
		this.modelSmellResult = smellresult;
	}	
	
}
