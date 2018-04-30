package org.eclipse.emf.refactor.smells.runtime.handler;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.smells.runtime.core.EObjectGroup;
import org.eclipse.emf.refactor.smells.runtime.core.ModelSmellResult;
import org.eclipse.emf.refactor.smells.runtime.core.ResultModel;
import org.eclipse.emf.refactor.smells.runtime.managers.RuntimeManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class FindModelSmellHandler implements IHandler {

	private IFile selectedFile;
	private EObject selectedEObject;
	private IProject selectedProject;
	
	private Shell shell;

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) { }

	@Override
	public void dispose() { }

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		
		if (!(selection instanceof IStructuredSelection))
			return null;
		
	
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		List<?> selectedElementsList = structuredSelection.toList();
		
		Cursor oldCursor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getCursor();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setCursor(new Cursor(null,SWT.CURSOR_WAIT));
		
		int nSelectedEObjects = 0;
		for (Object selectedElement : selectedElementsList) {
			if (!(selectedElement instanceof EObject))
				continue;
			++nSelectedEObjects;
			selectedEObject = (EObject) selectedElement;
			
			if (selectedEObject == null) {	
				MessageDialog.openError(
						shell,
						"EMF Quality Assurance: Error when trying to execute smell search",
						"No selected EMF model element!");
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setCursor(oldCursor);
				return null;
			}
			
			try {
				if (selectedEObject != null) {
					String path = selectedEObject.eResource().getURI().toPlatformString(true);
					selectedFile = (IFile) ResourcesPlugin.getWorkspace().getRoot().findMember(path);
				} 
				selectedProject = selectedFile.getProject();
				RuntimeManager.getInstance();
				System.out.println("Root: " + selectedEObject);
				System.out.println("Project: " + selectedProject);
				RuntimeManager.findConfiguredModelSmells(selectedProject, selectedEObject, selectedFile);
			} catch (Exception ex) {
//				ex.printStackTrace();
				Throwable cause = ex.getCause();
				if(!(cause == null) && cause.getClass().getName().equals("org.eclipse.emf.ecore.xmi.PackageNotFoundException")){
					MessageDialog.openError(
							shell,
							"EMF Quality Assurance: Error when trying to open File",
							"The file you selected is not a (valid) EMF model.");
				} else {
				MessageDialog.openError(
						shell,
						"EMF Quality Assurance: Error when trying to execute smell search", 
						ex.toString());
				ex.printStackTrace();
				}
			}
		}
		
		if (nSelectedEObjects > 1) {
			LinkedList<ResultModel> resultModels = RuntimeManager.getResultModels();
			List<ResultModel> lastNResultModels = resultModels.subList(resultModels.size() - nSelectedEObjects, resultModels.size());
			
			ResultModel mergedResultModel = mergeResultModels(lastNResultModels);
			
			for (int i = 1; i <= nSelectedEObjects; ++i) {
				resultModels.removeLast();
			}
			
			resultModels.add(mergedResultModel);
			
			RuntimeManager.getResultModelTreeViewer().setInput(resultModels);
			RuntimeManager.getResultModelTreeViewer().refresh();
		}
		
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setCursor(oldCursor);
		return true;
		
	}

	private ResultModel mergeResultModels(List<ResultModel> resultModels) {
		Iterator<ResultModel> it = resultModels.iterator();
		ResultModel firstResultModel = it.next();
		LinkedList<ModelSmellResult> mergeModelSmellResults = new LinkedList<ModelSmellResult>(firstResultModel.getModelSmellResults());
		while (it.hasNext()) {
			ResultModel resultModel = it.next();
			for (ModelSmellResult modelSmellResult : resultModel.getModelSmellResults()) {
				Optional<ModelSmellResult> matchingModelSmellResult =  mergeModelSmellResults.stream().filter(r -> r.getModelSmell().getId().equals(modelSmellResult.getModelSmell().getId())).findAny();
				if (matchingModelSmellResult.isPresent()) {
					List<EObjectGroup> eObjectGroup = matchingModelSmellResult.get().getEObjectGroups();
					for (EObjectGroup g : modelSmellResult.getEObjectGroups()) {
						if (eObjectGroup.stream().filter(x -> x.getEObjects().equals(g.getEObjects())).count() == 0)
							eObjectGroup.add(g);
					}
					//matchingModelSmellResult.get().addEObjectGroups(modelSmellResult.getEObjectGroups());
				} else {
					mergeModelSmellResults.add(modelSmellResult);
				}
			}
		}
		
		return new ResultModel(firstResultModel.getDate(), firstResultModel.getIFile(), mergeModelSmellResults);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) { }
	

	
}
