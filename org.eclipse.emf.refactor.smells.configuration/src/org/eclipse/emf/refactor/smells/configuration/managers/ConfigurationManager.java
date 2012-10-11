package org.eclipse.emf.refactor.smells.configuration.managers;

import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.refactor.smells.configuration.core.Configuration;
import org.eclipse.emf.refactor.smells.core.MetricBasedModelSmellFinderClass;
import org.eclipse.emf.refactor.smells.core.ModelSmell;
import org.eclipse.emf.refactor.smells.core.ModelSmellLoader;
import org.eclipse.emf.refactor.smells.managers.ModelSmellManager;

/**
 * Central controller class of EMF Smells. The logic for all the use cases is represented in one
 * <br>- or more methods of this class.
 * 
 * @author Thorsten Arendt
 *
 */

public class ConfigurationManager {
	
	private static LinkedList<ModelSmell> allSmells = null;
	private static LinkedList<Configuration> configurations;
	
	private static ConfigurationManager instance;
	
	private ConfigurationManager() {
		configurations = new LinkedList<Configuration>();
		allSmells = ModelSmellManager.getAllModelSmells();
		System.out.println("ConfigurationManager initialized!");
	}
	
	public static ConfigurationManager getInstance() {
		if (instance == null) {
			instance = new ConfigurationManager();
		}
		return instance;
	}
	
	/**
	 * Loads a LinkedList of all smell plugins (wrapped into <i>ModelSmell</i> objects) 
	 * <br>- currently installed in the Eclipse instance.
	 * 
	 * @return all smells currently installed
	 */
	public static LinkedList<ModelSmell> getAllModelSmells() {
		if(allSmells==null)
			allSmells = ModelSmellLoader.loadModelSmells();
		return allSmells;
	}
	
	/**
	 * For a given smell ID this method returns the limit set in the configuration. If it 
	 * <br>- is not a metric based smell or the limit was not set. It return 0.0.
	 * @param project - target project
	 * @param smellID - the id of the given smell
	 * @return limit set in the projects configuration or 0.0
	 */
	public static Double getLimit(IProject project, String smellID){
		System.out.println("=========>>> EMFModelSmells, getLimit");
		System.out.println("=========>>> EMFModelSmells, id: " + smellID);
		Configuration config = getConfiguration(project);
		Double limit = config.getLimit(smellID);
		System.out.println("===>>> EMF Model Smells: limit: " + limit);
		return limit;
	}
	
	/**
	 * Return the <i>ModelSmell</i> objects for the smells which are active on the
	 * <br>- given project.
	 * 
	 * @param project - target project
	 * @return - all model smell active for the given project
	 */
	public static LinkedList<ModelSmell> getSelectedModelSmells(IProject project){
		System.out.println("===>>> EMFModelSmells - getSelectedModelSmlls");
		Configuration config = getConfiguration(project);
		return config.getSelectedModelSmells();
	}
	
	/**
	 * Loads the <i>Configuration</i> of a target project from its xml-file.
	 * @param project - target project
	 * @return <i>Configuration</i> object for the project
	 */
	public static Configuration loadConfiguration(IProject project) {
		return XMLProjectFileManager.loadConfigurator(project);
	}
	
	/**
	 * Saves the current configuration of the project by writing it to
	 * <br>- the project's xml-file
	 * @param project - target project
	 */
	public static void saveConfiguration(IProject project) {
		XMLProjectFileManager.saveConfigurator(getConfiguration(project));
	}

	/**
	 * Sets the configured data for a project by manipulating the 
	 * <br>- <i>Configuration</i> object for this project currently
	 * <br>- held by EMF Smells.
	 * @param project - target project
	 * @param selection - array determening the selection of smells
	 */
	public static void setConfiguration(IProject project, boolean[] selection){
		System.out.println("===>>> EMFModelSmells, setConfiguration(2)");
		Configuration configuration = getConfiguration(project);
		configuration.setSelection(selection);
	}
	
	/**
	 * Sets the configured data for a project by manipulating the 
	 * <br>- <i>Configuration</i> object for this project currently
	 * <br>- held by EMF Smells.
	 * <br>- Also sets the limits for metric based smells.
	 * @param project - target project
	 * @param selection - array determening the selection of smells
	 * @param limits - limits for the smells
	 */
	public static void setConfiguration(IProject project, boolean[] selection, double[] limits){
		System.out.println("===>>> EMFModelSmells, setConfiguiration");
		Configuration configuration = getConfiguration(project);
		configuration.setSelection(selection);
		configuration.setLimits(limits);
	}
	
	private static Configuration getConfiguration(IProject project) {
		Configuration configuration = null;
		if(!configurations.isEmpty()) {
			System.out.println("1");
			for (Configuration tempConfiguration : configurations) {
				if (tempConfiguration.getProject().equals(project))
					configuration=tempConfiguration;
			}
		}
		if(configuration == null) {
			System.out.println("2");
			configuration = loadConfiguration(project);
		}
		if(configuration == null){
			System.out.println("3");
			configuration = new Configuration(project);	
			LinkedList<String> smellIdList = new LinkedList<String>();
			if(allSmells == null) getAllModelSmells();
			double[] limits = new double[allSmells.size()];
			for (ModelSmell smell : allSmells) {
				smellIdList.add(smell.getId());
				System.out.println("add: " + smell.getId());				
				if (smell.getFinderClass() instanceof MetricBasedModelSmellFinderClass) {
					limits[allSmells.indexOf(smell)] = 1.0;
					//((MetricBasedModelSmellFinderClass)smell.getFinderClass()).setLimit(1.0);
				}
			}			
			configuration.setSelection(smellIdList);
			configuration.setLimits(limits);
		}
		configurations.add(configuration);
		return configuration;
	}	
}
