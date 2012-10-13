package org.eclipse.emf.refactor.smells.generator.core;

import org.eclipse.core.resources.IProject;

/**
 * The class holding the basic information about a smell plugin which is to be generated.
 * 
 * @author Matthias Burhenne
 *
 */

public class ModelSmellInfo {
	private final static String PACKAGE_NAME = "org.eclipse.emf.refactor.modelsmell";
	private String name;
	private String description;
	private IProject project;
	private String metamodel;
	private String id;
	
	public ModelSmellInfo(String id, String name, String description, String metamodel, IProject project) {
		this.name = name;
		this.description = description;
		this.project = project;
		this.metamodel = metamodel;
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}

	public IProject getProject() {
		return project;
	}

	public String getMetamodel() {
		return metamodel;
	}

	public String getId() {
		return id;
	}

	public static String getPackageName() {
		return PACKAGE_NAME;
	}

	public String getClassName() {
		String head = "" + name.charAt(0);
		String tail = name.substring(1, (name.length()));
		String result = head.toUpperCase() + tail;
		return result.replace(" ", "");
	}
	
	public String getPackage(){
		return PACKAGE_NAME;
	}
	
	public String getProjectPath() {
		return project.getLocation().toString();
	}
	
	/**
	 * Returns the name of the target project for the model smell that will be generated.
	 * 
	 * @return projectName
	 */
	public String getProjectName() {
		int index = getProjectPath().lastIndexOf("/");
		String result = getProjectPath().substring(index+1);
		return result;
	}
	

}
