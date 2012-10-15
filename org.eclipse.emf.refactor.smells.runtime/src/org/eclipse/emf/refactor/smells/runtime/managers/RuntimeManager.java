package org.eclipse.emf.refactor.smells.runtime.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.configuration.managers.ConfigurationManager;
import org.eclipse.emf.refactor.smells.core.MetricBasedModelSmellFinderClass;
import org.eclipse.emf.refactor.smells.core.ModelSmell;
import org.eclipse.emf.refactor.smells.core.ModelSmellLoader;
import org.eclipse.emf.refactor.smells.interfaces.IHighlighting;
import org.eclipse.emf.refactor.smells.runtime.core.ModelSmellFinder;
import org.eclipse.emf.refactor.smells.runtime.core.Result;
import org.eclipse.emf.refactor.smells.runtime.core.ResultModel;
import org.eclipse.emf.refactor.smells.runtime.core.ResultViewEntry;
import org.eclipse.emf.refactor.smells.runtime.ui.ResultModelTreeView;
import org.eclipse.emf.refactor.smells.runtime.ui.ResultModelTreeViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * Central controller class of EMF Smells. The logic for all the use cases is represented in one
 * <br>- or more methods of this class.
 * 
 * @author Matthias Burhenne
 *
 */

public class RuntimeManager {
	
	private static LinkedList<ModelSmell> allSmells=null;
	private static LinkedList<ResultModel> resultModels = new LinkedList<ResultModel>();
	private static List<Action> additionalActions = new ArrayList<Action>();
	private static List<IHighlighting> additionalHighlightings = new ArrayList<IHighlighting>();
	private static ResultModelTreeViewer resultModelViewer;

	private static IEditorPart editorPart = null;	
	
	public static IEditorPart getEditorPart() {
		return editorPart;
	}

	public static void setEditorPart(IEditorPart editorPart) {
		RuntimeManager.editorPart = editorPart;
	}	
	
	private static LinkedList<ResultViewEntry> resultsViewInput;
	
	private static RuntimeManager instance;
	
	private RuntimeManager() {
		resultsViewInput = new LinkedList<ResultViewEntry>();
		System.out.println("RuntimeManager initialized!");
	}
	
	public static RuntimeManager getInstance() {
		if (instance == null) {
			instance = new RuntimeManager();
		}
		return instance;
	}
	
	public static RuntimeManager getInstance(Action action) {
		if (instance == null) {
			instance = new RuntimeManager();
			additionalActions.add(action);
		} else {
			additionalActions.add(action);
		}
		return instance;
	}
	
	public static RuntimeManager getInstance(IHighlighting highlighting) {
		if (instance == null) {
			instance = new RuntimeManager();
			additionalHighlightings.add(highlighting);
		} else {
			additionalHighlightings.add(highlighting);
		}
		return instance;
	}
	
	public static void clearResultModels(){
		resultModels = new LinkedList<ResultModel>();
		resultModelViewer.setInput(resultModels);
	}
	
	/**
	 * Starts the search for model smells on the provided root object and searches for the 
	 * <br>- smell activatet for the given <i>IProject</i>
	 * 
	 * @param project - project for which the configured smells are supposed to be found
	 * @param root - root object the search has to be performed on
	 * @param selectedFile 
	 */
	public static void findConfiguredModelSmells(IProject project, EObject root, IFile selectedFile){
		ConfigurationManager.getInstance();
		LinkedList<ModelSmell> smells = ConfigurationManager.getSelectedModelSmells(project);
		for(ModelSmell smell : smells){
			if(smell.getFinderClass() instanceof MetricBasedModelSmellFinderClass)
				((MetricBasedModelSmellFinderClass)smell.getFinderClass()).setLimit(ConfigurationManager.getLimit(project, smell.getId()));
		}
		LinkedList<Result> results = ModelSmellFinder.findModelSmells(smells, root);
		//=====
//		String path = root.eResource().getURI().toPlatformString(true);
////		IFile iFile = (IFile)org.eclipse.core.resources.ResourcesPlugin.getWorkspace().getRoot().findMember(path);
//		IFile iFile = selectedFile;
		//=====
		resultModels.add(new ResultModel(results, selectedFile, new Date()));
		// TA
		if(!isSmellViewOpen()){
			createSmellView();
		}
		// End TA
		if(resultModelViewer!=null && resultModelViewer.getContentProvider() != null)
			if(resultModels != null){
				resultModelViewer.setInput(resultModels);
			}
		for(Result result : results){
			resultsViewInput.addAll(result.getResultViewEntries());
		}
//		if(resultsViewer!=null)
//			if(resultsViewInput!=null){
//				resultsViewer.setInput(resultsViewInput);
//			}
	}
	
	private static void createSmellView() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
			try {
				page.showView(ResultModelTreeView.MENU_ID);
				setAdditionalActionsToView(getSmellView());
				setAdditionalHightightingsToView(getSmellView());
			} catch (PartInitException e) {
				e.printStackTrace();
			}
	}
	
	private static ResultModelTreeView getSmellView() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		IWorkbenchPage page = win.getActivePage();
		for(int i=0; i<page.getViewReferences().length; i++) {
			if(page.getViewReferences()[i].getId().equals(ResultModelTreeView.MENU_ID)) {
				return ((ResultModelTreeView) page.getViewReferences()[i].getView(true));
			}
		}
		return null;
	}

	private static boolean isSmellViewOpen(){
		ResultModelTreeView view = getSmellView();
		if (view != null) {
			setAdditionalActionsToView(view);
			setAdditionalHightightingsToView(getSmellView());
			return true;
		}
		return false;
	}
	
	private static void setAdditionalActionsToView(ResultModelTreeView view) {
		for (Action action : additionalActions) {
			System.out.println("RuntimeManager: add action '" + action + "' to view!");
			view.addAction(action);
		}
		view.addActionsToMenu();
	}
	
	private static void setAdditionalHightightingsToView(ResultModelTreeView view) {
		for (IHighlighting highlighting : additionalHighlightings) {
			System.out.println("RuntimeManager: add highlighting '" + highlighting + "' to view!");
			view.addHighlighting(highlighting);
		}
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
	
	public static LinkedList<ResultViewEntry> getResultsViewInput(){
		return resultsViewInput;
	}
	
	public static void setResultModelTreeViewer(ResultModelTreeViewer viewer){
		resultModelViewer = viewer;
	}
	
	public static LinkedList<ResultModel> getResultModels(){
		return resultModels;
	}
	
	
}
