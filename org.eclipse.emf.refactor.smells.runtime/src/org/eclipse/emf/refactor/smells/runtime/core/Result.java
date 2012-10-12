package org.eclipse.emf.refactor.smells.runtime.core;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.core.ModelSmell;

/**
 * The result of the identification of one particular smell at a given time. Currently used by the basic
 * <br>- ModelSmellResultView which is outdated but still supported.
 * 
 * @author Matthias Burhenne
 *
 */

public class Result {
	private ModelSmell smell;
	private String timestamp;
	private LinkedList<LinkedList<EObject>> modelelements;
	
	@SuppressWarnings("deprecation")
	public Result(ModelSmell modelsmell, LinkedList<LinkedList<EObject>> modelelements){
		this.smell = modelsmell;
		this.modelelements = modelelements;
		this.timestamp = GregorianCalendar.getInstance().getTime().toLocaleString();
	}
	
	public ModelSmell getSmell() {
		return smell;
	}

	public String getTimeStamp() {
		return timestamp;
	}

	public LinkedList<LinkedList<EObject>> getModelelements() {
		return modelelements;
	}
	
	/**
	 * Returns a list of <i>ResultViewEntry</i> which represent single model elements which each
	 * <br>- form a smelly model area.
	 * @return list of EObjects
	 */
	public LinkedList<ResultViewEntry> getResultViewEntries(){
		LinkedList<ResultViewEntry> entries = new LinkedList<ResultViewEntry>();
		for(List<EObject> modelelement : modelelements){
			for(EObject eObject : modelelement){
				String modelElementString = "";
				if(eObject instanceof ENamedElement)
					modelElementString = ((ENamedElement) eObject).getName();
				else
					modelElementString = eObject.toString();
				entries.add(new ResultViewEntry(this.smell, this.timestamp, modelElementString));
			}
		}
		return entries;
	}

}


