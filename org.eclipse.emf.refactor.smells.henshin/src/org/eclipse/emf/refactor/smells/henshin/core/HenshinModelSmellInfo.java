package org.eclipse.emf.refactor.smells.henshin.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.refactor.smells.generator.core.ModelSmellInfo;

/**
 * Subclass of <i>ModelSmellInfo</i> which holds specific information for the
 * <br>- generation of smell plugins based on EMF Henshin
 * 
 * @author Matthias Burhenne
 *
 */

public class HenshinModelSmellInfo extends ModelSmellInfo {
	String henshinFileName;

	public HenshinModelSmellInfo(String id, String name, String description,
			String metamodel, IProject project, String henshinFileName) {
		super(id, name, description, metamodel, project);
		this.henshinFileName = henshinFileName;
	}
	
	public String getHenshinFileName(){
		return henshinFileName;
	}
	
}
