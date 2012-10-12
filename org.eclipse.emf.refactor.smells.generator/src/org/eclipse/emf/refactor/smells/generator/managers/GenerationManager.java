package org.eclipse.emf.refactor.smells.generator.managers;

import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.refactor.smells.generator.core.MetricData;
import org.eclipse.emf.refactor.smells.generator.core.ModelSmellInfo;

public class GenerationManager {
	
	private static GenerationManager instance;
	
	protected GenerationManager() {
//		templateDirectory = setTemplateDirectory();
//		classpathEntries = setClassPathEntries();
		System.out.println("GenerationManager initialized!");
	}
	
	public static GenerationManager getInstance() {
		if (instance == null) {
			instance = new GenerationManager();
		}
		return instance;
	}

	public static LinkedList<MetricData> loadAllMetricData() {
		// TODO Auto-generated method stub
		return new LinkedList<MetricData>();
	}

	public static void createNewModelSmell(IProgressMonitor monitor,
			ModelSmellInfo modelSmellInfo, IProject newSmellTargetProject) {
		// TODO Auto-generated method stub
		System.out.println("Create ...");
	}

}
