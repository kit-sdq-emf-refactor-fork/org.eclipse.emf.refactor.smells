package org.eclipse.emf.refactor.smells.configuration.core;

import java.util.LinkedList;

import org.eclipse.emf.refactor.smells.core.ModelSmell;

/**
 * Helper class for the property page providing a sorted list of the installed smells to be used
 * <br>- by the pages table.
 * 
 * @author Matthias Burhenne
 *
 */

public class SortedModelSmellList {
	private LinkedList<LinkedList<ModelSmell>> smellList;

	public SortedModelSmellList(LinkedList<ModelSmell> rawSmellList) {
		smellList = new LinkedList<LinkedList<ModelSmell>>();
		for (int position = 0; position < rawSmellList.size(); position++) {
			addSmellToList(rawSmellList.get(position));
		}
	}

	public int getSize(){
		return smellList.size();
	}
	
	public LinkedList<ModelSmell> get(int index){
		return smellList.get(index);
	}
	
	public LinkedList<LinkedList<ModelSmell>> getModelSmellList() {
		return smellList;
	}

	private void addSmellToList(ModelSmell smell) {
		final String metamodel = smell.getMetamodel();
		LinkedList<ModelSmell> list = getMetamodelList(metamodel);
		if (list == null) {
			list = new LinkedList<ModelSmell>();
			smellList.add(list);
		}
		list.add(smell);
	}

	private LinkedList<ModelSmell> getMetamodelList(String metamodel) {
		for (LinkedList<ModelSmell> list : smellList) {
			if (list.get(0).getMetamodel().equals(metamodel))
				return list;
		}
		return null;
	}
}
