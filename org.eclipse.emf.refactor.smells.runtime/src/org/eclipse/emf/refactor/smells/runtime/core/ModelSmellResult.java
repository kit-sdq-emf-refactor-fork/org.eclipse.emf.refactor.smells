package org.eclipse.emf.refactor.smells.runtime.core;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.refactor.smells.core.ModelSmell;


/**
 * Class representing the result of an identification run on a given model
 * <br>- for one particular smell.
 * 
 * @author Matthias Burhenne
 *
 */

public class ModelSmellResult {
	private LinkedList<EObjectGroup> objectGroups;
	private ModelSmell modelsmell;
	private ResultModel model;
	
	public ModelSmellResult(ModelSmell modelsmell, LinkedList<EObjectGroup> objectGroups, ResultModel model){
		this.modelsmell = modelsmell;
		this.objectGroups = objectGroups;
		this.model = model;
	}
	
	public LinkedList<EObjectGroup> getEObjectGroups(){
		return objectGroups;
	}
	
	public ModelSmell getModelSmell(){
		return modelsmell;
	}
	
	public ResultModel getResultModel(){
		return model;
	}
	
	public void addEObjectGroups(List<EObjectGroup> groups){
		this.objectGroups.addAll(groups);
	}
}
