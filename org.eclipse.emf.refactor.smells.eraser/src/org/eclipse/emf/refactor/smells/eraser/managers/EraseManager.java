package org.eclipse.emf.refactor.smells.eraser.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.refactoring.core.Refactoring;
import org.eclipse.emf.refactor.refactoring.managers.RefactoringManager;
import org.eclipse.emf.refactor.smells.core.ModelSmell;
import org.eclipse.emf.refactor.smells.eraser.core.RefactoringToSmellsRelation;
import org.eclipse.emf.refactor.smells.eraser.core.SmellToRefactoringsRelation;
import org.eclipse.emf.refactor.smells.eraser.utils.ModelRefactoringStub;
import org.eclipse.emf.refactor.smells.eraser.utils.ModelSmellStub;
import org.eclipse.emf.refactor.smells.eraser.utils.ProjectEntries;
import org.eclipse.emf.refactor.smells.managers.ModelSmellManager;
import org.eclipse.emf.refactor.smells.runtime.core.EObjectGroup;

/**
 * Central controller class of EMF Fix. It holds the logic for most of the use cases.
 * 
 * @author Matthias Burhenne
 *
 */

public class EraseManager {
	
	private static final SmellToRefactoringsRelation smellToRefactorings = SmellToRefactoringsRelation.INSTANCE;
	private static final RefactoringToSmellsRelation refactoringToSmellsRelation = RefactoringToSmellsRelation.INSTANCE;
	private static List<Refactoring> allRefactorings = RefactoringManager.getAllRefactorings();
	private static List<ModelSmell> allSmells = ModelSmellManager.getAllModelSmells();
	
	private static EraseManager instance;
	
	private EraseManager() {
		System.out.println("EraseManager initialized!");
	}
	
	public static EraseManager getInstance() {
		if (instance == null) {
			instance = new EraseManager();
		}
		return instance;
	}
	
	public ModelSmell getSmell(String id) {
		allSmells = ModelSmellManager.getAllModelSmells();
		for(ModelSmell currentSmell : allSmells) {
			if(currentSmell.getId().equals(id))
				return currentSmell;
		}
		return null;
	}
	
	public Refactoring getRefactoring(String id) {
		allRefactorings = RefactoringManager.getAllRefactorings();
		for(Refactoring currentRefactoring : allRefactorings) {
			if(currentRefactoring.getId().equals(id))
				return currentRefactoring;
		}
		return null;
	}
	
	/**
	 * Returns all applicable refactorings for the given EObjectGroup.
	 * 
	 * @param group - The EObjectGroup for which the applicable refactorings are requested
	 * @return Map containing the applicable refactorings (key) and their possible context objects (value)
	 */
	public Map<Refactoring, Set<EObject>> getApplicableRefactoringsDynamically(EObjectGroup group){
		Map<Refactoring, Set<EObject>> applicableRefactorings = new HashMap<Refactoring, Set<EObject>>();
		for(Refactoring refactoring : allRefactorings){
			for(EObject eObject : group.getEObjects()){
				boolean isApplicable = passesInitialCheck(eObject, refactoring);
				if(isApplicable) {
					Set<EObject> contextObjects = applicableRefactorings.get(refactoring);
					if(contextObjects == null){
						contextObjects = new HashSet<EObject>();
					}
					contextObjects.add(eObject);
					applicableRefactorings.put(refactoring, contextObjects);					
				}				
			}
		}
		return applicableRefactorings;
	}
	
	/**
	 * Return a boolean indicating whether or not the initial check passed for the given Refactoring and
	 * context object.
	 * 
	 * @param contextObject - the EObject used as the refactoring's context object
	 * @param refactoring - the EMFRefactoring of which the initial check is supposed to be performed
	 * @return boolean indicating whether or not the initial check passed (true) or not (false)
	 */
	public boolean passesInitialCheck(EObject contextObject, Refactoring refactoring) {
		ArrayList<EObject> selection = new ArrayList<EObject>();
		selection.add(contextObject);
		if (refactoring.getGui().showInMenu(selection)) {
			System.out.println("Context: " + contextObject + "; Refactoring: " + refactoring.getName());
			refactoring.getController().setSelection(selection);
			refactoring.getController().getDataManagementObject().preselect(selection);
			boolean isApplicable = false;
			try {
				isApplicable = refactoring.getController().getLtkRefactoringProcessor().checkInitialConditions(new NullProgressMonitor()).isOK();
			} catch (OperationCanceledException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			}
			System.out.println("applicable: " + isApplicable);
			return isApplicable;
		}
		return false;
	}
	
	/**
	 * Writes the provided relations for model smells and model refactorings back the the
	 * target project's plugin.xml
	 * 
	 * @param monitor - the progressmonitor provided by the caller
	 * @param project - the target project
	 * @param entries - the RelationEntries in which the relations are stored
	 * @param saveDanglingEntries - flag indicating whether dangling relations should be saved (true) or discarded (false)
	 */
	public void saveRelations(IProgressMonitor monitor, IProject project, ProjectEntries entries, boolean saveDanglingEntries){
		PluginXMLManager.saveProjectEntries(entries, project, saveDanglingEntries);
		try {
			project.refreshLocal(IProject.DEPTH_ZERO, monitor);
			project.refreshLocal(IProject.DEPTH_ONE, monitor);
			project.refreshLocal(IProject.DEPTH_INFINITE, monitor);
			IFile manifest = project.getFile("MANIFEST.MF");
			if(manifest.exists()){
				manifest.refreshLocal(IFile.DEPTH_ZERO, monitor);
				manifest.refreshLocal(IFile.DEPTH_ONE, monitor);
				manifest.refreshLocal(IFile.DEPTH_INFINITE, monitor);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns all fixing refactorings for the smell passed as a parameter as defined by the installed relations.
	 * 
	 * @param smell - ModelSmell for which the fixing refactorings are found
	 * @return - Set of Refactoring objects
	 */
	public Set<Refactoring> getFixingRefactorings(ModelSmell smell){
		return smellToRefactorings.getFixingRefactorings(smell);
	}
	
	/**
	 * Returns all potentially caused model smells for the refactoring passed as parameter as defined by the installed relations.
	 * 
	 * @param refactoring -  the Refactoring for which the caused smells are to be found
	 * @return Set of ModelSmell objects
	 */
	public Set<ModelSmell> getCausedModelSmells(Refactoring refactoring){
		return refactoringToSmellsRelation.getCausedSmells(refactoring);
	}
	
	/**
	 * Finds all the potentially caused smells for a given set of Refactorings
	 * @param refactorings - the Collection of refactorings for which the smells are to be found
	 * @return Map containing the refactorings as keys and the lists of caused smells as values
	 */
	public Map<Refactoring, Set<ModelSmell>> getCausedModelSmells(Collection<Refactoring> refactorings){
		HashMap<Refactoring, Set<ModelSmell>> relation = new HashMap<Refactoring, Set<ModelSmell>>();
		for(Refactoring refactoring : refactorings){
			relation.put(refactoring, getCausedModelSmells(refactoring));
		}
		return relation;
	}
	
	/**
	 * Returns all the metamodel URIs for which there are model smell or model refactorings installed.
	 * 
	 * @return Set of Strings representing the metamodel URIs
	 */
	public Set<String> getMetamodelURIs(){
		HashSet<String> metamodelURIs = new HashSet<String>();
		for(ModelSmell smell : allSmells){
			metamodelURIs.add(smell.getMetamodel());
		}
		for(Refactoring refactoring : allRefactorings){
			metamodelURIs.add(refactoring.getNamespaceUri());
		}
		return metamodelURIs;
	}

	/**
	 * Getter for the field allRefactorings.
	 * 
	 * @return List of Refactoring objects.
	 */
	public List<Refactoring> getAllrefactorings() {
		return allRefactorings;
	}

	/**
	 * Getter for the field allSmells.
	 * 
	 * @return List of ModelSmell objects
	 */
	public List<ModelSmell> getAllsmells() {
		return allSmells;
	}
	
	/**
	 * Returns all the ModelSmell objects whose metamodels have the URI provided by the passed String.
	 * 
	 * @param metamodelURI - String containing the metamodel URI
	 * @return Set of ModelSmell objects.
	 */
	public Set<ModelSmell> getAllInstalledSmellsForMetamodel(String metamodelURI){
		return filterSmellsForMetamodel(allSmells, metamodelURI);
	}
	
	/**
	 * Returns all the EmfRefactor objects whose metamodels have the URI provided by the passed String.
	 * 
	 * @param metamodelURI - String containing the metamodel URI
	 * @return Set of Refactoring objects
	 */
	public Set<Refactoring> getAllInstalledRefactoringsForMetamodel(String metamodelURI){
		return filterRefactoringsForMetamodel(allRefactorings, metamodelURI);
	}
	
	/**
	 * Loads stubs for all the Refactorings defined in the currently active workspace.
	 * 
	 * @return Set of ModelRefactoringStub objects
	 */
	public Set<ModelRefactoringStub> getAllRefactoringStubsFromWorkspace(){
		Set<ModelRefactoringStub> refactorings = new HashSet<ModelRefactoringStub>();
		List<IProject> pluginProject = getAllPluginProjects();
		for(IProject project : pluginProject){
			refactorings.addAll(PluginXMLManager.getRefactoringStubsFromPluginXML(project));
		}
		return refactorings;
	}
	
	/**
	 * Loads stubs for all the Refactorings defined in the currently active workspace and
	 * corresponding to the provided metamodel.
	 * 
	 * @param metamodelURI - String containing the metamodel URI
	 * @return Set of ModelRefactoringStub objects
	 */
	public Set<ModelRefactoringStub> getAllRefactoringStubsFromWorkspaceForMetamodel(String metamodelURI){
		return filterRefactoringStubsForMetamodel(getAllRefactoringStubsFromWorkspace(), metamodelURI);
	}
	
	/*
	 * Filters a Set of ModelRefactoringStubs to contain only those corresponding to a certain metamodel.
	 */
	private Set<ModelRefactoringStub> filterRefactoringStubsForMetamodel(Set<ModelRefactoringStub> refactoringStubs, String metamodelURI) {
		HashSet<ModelRefactoringStub> result = new HashSet<ModelRefactoringStub>();
		for(ModelRefactoringStub stub : refactoringStubs){
			if(stub.getMetamodel().equals(metamodelURI)){
				result.add(stub);
			}
		}
		return result;
	}

	/*
	 * Filters a Collection of Refactoring objects to contain only those corresponding to a certain metamodel.
	 */
	private Set<Refactoring> filterRefactoringsForMetamodel(Collection<Refactoring> refactorings, String metamodelURI){
		HashSet<Refactoring> result = new HashSet<Refactoring>();
		for(Refactoring refactoring : refactorings){
			if(refactoring.getNamespaceUri().equals(metamodelURI)){
				result.add(refactoring);
			}
		}
		return result;
	}
	
	/**
	 * Loads stubs for all the ModelSmells defined in the currently active workspace.
	 * 
	 * @return Set of ModelSmellStub objects
	 */
	public Set<ModelSmellStub> getAllSmellStubsFromWorkspace(){
		Set<ModelSmellStub> smells = new HashSet<ModelSmellStub>();
		List<IProject> pluginProjects = getAllPluginProjects();
		for(IProject project : pluginProjects){
			smells.addAll(PluginXMLManager.getSmellStubsFromPluginXML(project));
		}
		return smells;
	}
	
	/**
	 * Loads stubs for all the ModelSmells defined in the currently active workspace and
	 * corresponding to the provided metamodel.
	 * 
	 * @param metamodelURI - String containing the metamodel URI
	 * @return Set of ModelSmellStub objects
	 */
	public Set<ModelSmellStub> getAllSmellStubsFromWorkspaceForMetamodel(String metamodelURI){
		return filterSmellStubsForMetamodel(getAllSmellStubsFromWorkspace(), metamodelURI);
	}
	
	/*
	 * Filters a Set of ModelSmellStubs to contain only those corresponding to a certain metamodel.
	 */
	private Set<ModelSmellStub> filterSmellStubsForMetamodel(Set<ModelSmellStub> smellStubs, String metamodelURI) {
		HashSet<ModelSmellStub> result = new HashSet<ModelSmellStub>();
		for(ModelSmellStub stub : smellStubs){
			if(stub.getMetamodel().equals(metamodelURI)){
				result.add(stub);
			}
		}
		return result;
	}

	/*
	 * Filters a Set of ModelSmells to contain only those corresponding to a certain metamodel.
	 */
	private Set<ModelSmell> filterSmellsForMetamodel(Collection<ModelSmell> smells, String metamodelURI){
		HashSet<ModelSmell> result = new HashSet<ModelSmell>();
		for(ModelSmell smell : smells){
			if(smell.getMetamodel().equals(metamodelURI)){
				result.add(smell);
			}
		}
		return result;
	}
	
	/*
	 * Returns all projects in the currently active workspace which  are plugin projects.
	 */
	private List<IProject> getAllPluginProjects(){
		LinkedList<IProject> projects = new LinkedList<IProject>();
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : allProjects) {
			if (project.isOpen()) {
				IProjectNature nature = null;
				try {
					nature = project.getNature("org.eclipse.pde.PluginNature");
				} catch (CoreException e) {
					e.printStackTrace();
				}
				if (null != nature) 
					projects.add(project);
			}
		}
		return projects;
	}
	
	/**
	 * Returns ModelSmellStub objects for all the currently installed model smells.
	 * 
	 * @return - Set of ModelSmellStub objects
	 */
	public Set<ModelSmellStub> getStubsForAllModelSmells(){
		Set<ModelSmellStub> allStubs = new HashSet<ModelSmellStub>();
		allStubs.addAll(getAllSmellStubsFromWorkspace());
		for(ModelSmell smell : getAllsmells()){
			allStubs.add(ModelSmellStub.convertModelSmell(smell));
		}
		return allStubs;		
	}
	
	/**
	 * Returns ModelRefactoringStubs for all the currently installed model refactorings.
	 * 
	 * @return - Set of ModelRefactoringStubs
	 */
	public Set<ModelRefactoringStub> getStubsForAllRefactorings(){
		Set<ModelRefactoringStub> allStubs = new HashSet<ModelRefactoringStub>();
		allStubs.addAll(getAllRefactoringStubsFromWorkspace());
		for(Refactoring refactoring : getAllrefactorings()){
			allStubs.add(ModelRefactoringStub.convertRefactoring(refactoring));
		}
		return allStubs;
	}
	
	/**
	 * Returns all the ModelSmellStubs for all the ModelSmells currently installed and
	 * corresponding to the provided metamodel.
	 * 
	 * @param metamodelURI - String containing the metamodel URI
	 * @return - Set of ModelSmellStubs
	 */
	public Set<ModelSmellStub> getAllSmellStubsForMetamodel(String metamodelURI){
		return filterSmellStubsForMetamodel(getStubsForAllModelSmells(), metamodelURI);
	}
	
	/**
	 * Returns all the ModelRefactoringStubs for all the Refactorings currently installed and
	 * corresponding to the provided metamodel.
	 * 
	 * @param metamodelURI - String containing the metamodel URI
	 * @return - Set of ModelRefactoringStubs
	 */
	public Set<ModelRefactoringStub> getAllRefactoringStubsForMetamodel(String metamodelURI){
		return filterRefactoringStubsForMetamodel(getStubsForAllRefactorings(), metamodelURI);
	}
}
