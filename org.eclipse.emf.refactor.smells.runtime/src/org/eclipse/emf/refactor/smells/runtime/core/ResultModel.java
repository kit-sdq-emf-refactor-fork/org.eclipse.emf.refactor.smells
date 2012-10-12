package org.eclipse.emf.refactor.smells.runtime.core;

import java.util.Date;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;

/**
 * Root element of the model representing the result of a complete identification
 * <br>- run of EMF Smells on one model.
 * 
 * @author Matthias Burhenne
 *
 */

public class ResultModel {
	
	private LinkedList<ModelSmellResult> modelsmellResults;
	private IFile iFile;
	private Date date;
	
	public IFile getIFile() {
		return iFile;
	}

	//	public ResultModel(LinkedList<ModelSmellResult> modelsmellResults){
//		this.modelsmellResults = modelsmellResults;
//	}
//	
	public ResultModel(LinkedList<Result> results, IFile file, Date date){
		this.date = date;
		this.iFile = file;
		LinkedList<ModelSmellResult> smellresults = new LinkedList<ModelSmellResult>();
		for(Result result : results){
			// TA
			if (result.getModelelements().isEmpty()) {
				continue;
			}
			// End TA
			LinkedList<EObjectGroup> eObjectGroups = new LinkedList<EObjectGroup>();
			ModelSmellResult smellresult = new ModelSmellResult(result.getSmell(), eObjectGroups, this);
			for(LinkedList<EObject> eObjectSubList : result.getModelelements()){
				EObjectGroup group = new EObjectGroup(result.getSmell(), eObjectSubList);
				group.setModelSmellResult(smellresult);
				eObjectGroups.add(group);
//				smellresult.addEObjectGroups(eObjectGroups);
			}
			
//			ModelSmellResult smellresult = new ModelSmellResult(result.getSmell(), eObjectGroups, this);
			smellresults.add(smellresult);
		}
		this.modelsmellResults = smellresults;
	}
	
	public LinkedList<ModelSmellResult> getModelSmellResults(){
		return modelsmellResults;
	}
	
	public Date getDate(){
		return date;
	}
}
