package org.eclipse.emf.refactor.smells.configuration.core;

import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.refactor.smells.core.MetricBasedModelSmellFinderClass;
import org.eclipse.emf.refactor.smells.core.ModelSmell;
import org.eclipse.emf.refactor.smells.managers.ModelSmellManager;

public class Configuration {
	
	private double[] limits = null;
	private IProject project;
	private boolean[] selection = null;
	
	@Override
	public String toString() {
		LinkedList<ModelSmell> modelSmells = getSelectedModelSmells();
		System.out.println("---->");
		for (ModelSmell ms : modelSmells) System.out.println("Smell: " + ms);
		System.out.println("---->");
		for (boolean b : selection) System.out.println("Selected: " + b);
		System.out.println("---->");
		if (limits != null) for (double d : limits) System.out.println("Limit: " + d);
		System.out.println("---->");
		return "";
	}

	/**
	 * Constructor which creates a new <i>Configuration</i> for a given project.
	 * 
	 * @param project
	 */
	public Configuration(IProject project) {
		this.project = project;
		ModelSmellManager.getInstance();
	}

	/**
	 * Returns the limit set in the configuration for one given smell id.
	 *  It will always return 1.0 in case the smell is not properly set.
	 *  
	 * @param smellID - id of the smell in question
	 * @return the limit set in the configuration or 1.0
	 */
	public Double getLimit(String smellID){
		LinkedList<ModelSmell> allSmells = ModelSmellManager.getAllModelSmells();
		int index = isInList(smellID, allSmells);
		try{
			if(limits == null)
				return 1.0;
			else
				return limits[index];
		}catch(ArrayIndexOutOfBoundsException ex){
			return new Double(1.0);
		}
	}
	
	public double[] getLimits(){
		return limits;
	}
	
	/**
	 * Returns the limits for all smells as set in the configuration.
	 * <br>- The limit will be 0.0 in case it is not a metric based smell.
	 * 
	 * @return limits of all smells
	 */
	public LinkedList<Double> getLimitsForSelectedSmells(){
		if(selection==null)
			return new LinkedList<Double>();
		final int numberOfSmells = ModelSmellManager.getAllModelSmells().size();
		LinkedList<Double> selectedSmellsLimits = new LinkedList<Double>();
		for (int index = 0; index < numberOfSmells; index++) {
			if (selection[index] == true){
				ModelSmell selectedSmell = ModelSmellManager.getAllModelSmells().get(index);
				if(selectedSmell.getFinderClass() instanceof MetricBasedModelSmellFinderClass)
					selectedSmellsLimits.add(limits[index]);
				else
					selectedSmellsLimits.add(1.0);
			}
		}
		return selectedSmellsLimits;
	}

	public IProject getProject() {
		return project;
	}
	
	/**
	 * Returns a <i>LinkedList</i> containing all smells selected for this project..
	 * 
	 * @return selected smells for the current project
	 */
	public LinkedList<ModelSmell> getSelectedModelSmells() {
		if(selection==null)
			return new LinkedList<ModelSmell>();
		final int numberOfSmells = ModelSmellManager.getAllModelSmells().size();
		LinkedList<ModelSmell> selectedSmells = new LinkedList<ModelSmell>();
		for (int index = 0; index < numberOfSmells; index++) {
			if (selection[index] == true){
				ModelSmell selectedSmell = ModelSmellManager.getAllModelSmells().get(index);
				selectedSmells.add(selectedSmell);
			}
		}
		return selectedSmells;
	}

	public void setLimit(String smellID, Double limit){
		System.out.println("===>>> Configuration; setLimit: " + smellID + "; " + limit);
		LinkedList<ModelSmell> allSmells = ModelSmellManager.getAllModelSmells();
		int index = isInList(smellID, allSmells);
		if(limits == null)
			limits = new double[allSmells.size()];
		if(!(index == -1)){
			limits[index] = limit;
		}
	}
	
	/**
	 * Sets the limits for all smells for this project.
	 * <br>- This method expects a double array as parameter.
	 * 
	 * @param limits - limits for all smells
	 */
	public void setLimits(double[] limits){
		this.limits = limits;
	}
	
	/**
	 * Sets the smell selection for this project.
	 * <br>- This method expects a boolean array as parameter.
	 * 
	 * @param selection - (de-)activated status for each smell
	 */
	public void setSelection(boolean[] selection) {
		this.selection = selection;
	}
	
	public void setSelection(LinkedList<String> idList) {
		this.selection = getSelection(idList);
	}
	
	private boolean[] getSelection(LinkedList<String> idList){
		
		final LinkedList<ModelSmell> allModelSmells = ModelSmellManager.getAllModelSmells();
		boolean[] selection = new boolean[allModelSmells.size()];
		for(int index=0; index<selection.length; index++)
			selection[index]=false;
		int index;
		for(String id : idList){
			index = isInList(id, allModelSmells);
			if (index>=0){
				selection[index]=true;
			}
		}
		return selection;
	}
	
	private int isInList(String id, LinkedList<ModelSmell> smells){
		for(int index=0; index<smells.size(); index++){
			if(smells.get(index).getId().equals(id))
				return index;
		}	
		return -1;
	}

}
