package org.eclipse.emf.refactor.smells.runtime.core;

import java.util.GregorianCalendar;

import org.eclipse.emf.refactor.smells.core.ModelSmell;


/**
 * A single entry for the old ModelSmellResultView.
 * 
 * @author Matthias Burhenne
 *
 */

public class ResultViewEntry {
	private ModelSmell smell;
	private String timestamp;
	private String modelelement;
	
	@SuppressWarnings("deprecation")
	public ResultViewEntry(ModelSmell modelsmell, String timestamp, String modelelement){
		this.smell = modelsmell;
		this.modelelement = modelelement;
		this.timestamp = GregorianCalendar.getInstance().getTime().toLocaleString();
	}
	
	public ModelSmell getSmell() {
		return smell;
	}

	public String getTimeStamp() {
		return timestamp;
	}

	public String getModelelement() {
		return modelelement;
	}
	

}
