package org.eclipse.emf.refactor.smells.runtime.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.refactor.smells.runtime.core.EObjectGroup;
import org.eclipse.emf.refactor.smells.runtime.core.ModelSmellResult;
import org.eclipse.emf.refactor.smells.runtime.core.ResultModel;
import org.eclipse.emf.refactor.smells.runtime.managers.RuntimeManager;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
public class FindModelSmellHandler extends AbstractHandler {

	private static final Shell SHELL = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		
		if (!(selection instanceof IStructuredSelection))
			throw new ExecutionException("Selection is not instance of IStructuredSelection and could not be handled");
		

		List<?> selectedElementsList = ((IStructuredSelection) selection).toList();
		
		Cursor oldCursor = SHELL.getCursor();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setCursor(new Cursor(null,SWT.CURSOR_WAIT));
		
		List<EObject> selectedEObjects = selectedElementsList.stream().filter(o -> o instanceof EObject).map(o -> (EObject) o).collect(Collectors.toList());
		
		List<IContainer> selectedContainers = selectedElementsList.stream().filter(o -> o instanceof IContainer).map(o -> (IContainer) o).collect(Collectors.toList());
		
		List<IFile> selectedFiles = selectedElementsList.stream().filter(o -> o instanceof IFile).map(o -> (IFile) o).collect(Collectors.toList());
		
		handleEObjects(selectedEObjects, true);		
		handleContainers(selectedContainers);
		for (IFile selectedFile : selectedFiles) {
			handleEObject(getEObjectFromEcoreFile(selectedFile));
		}
		
		SHELL.setCursor(oldCursor);
		return null;
		
	}

	private void handleContainers(List<? extends IContainer> containers) {
		for (IContainer container : containers) {
			handleContainer(container);
		}
	}

	private void handleContainer(IContainer container) {
		try {
			List<IFile> ecoreFiles = getEcoreFiles(container);
			List<EObject> eObjects = ecoreFiles.stream().map(elm -> getEObjectFromEcoreFile(elm)).collect(Collectors.toList());
			handleEObjects(eObjects, true);
			
		} catch (CoreException e) {
			MessageDialog.openError(SHELL, "Inaccessible resource", "An error occured while trying to access the selected resource");
			e.printStackTrace();
		} 

	}

	private EObject getEObjectFromEcoreFile(IFile ecoreFile) {
		URI uri = URI.createPlatformResourceURI(ecoreFile.getFullPath().toString(), true); 
		TransactionalEditingDomain transactionalEditingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
		ResourceSet resourceSet = transactionalEditingDomain.getResourceSet();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		Resource resource = resourceSet.getResource(uri, true);
		return resource.getContents().get(0);
	}

	private List<IFile> getEcoreFiles(IResource resource) throws CoreException {
		List<IFile> result = new ArrayList<IFile>();
		if (resource instanceof IFile) {
			if (((IFile) resource).getFileExtension().equals("ecore")) {
				result.add((IFile) resource);
				return result;
			}
		}
		else if (resource instanceof IContainer){
			for (IResource r : ((IContainer) resource).members()) {
				result.addAll(getEcoreFiles(r));
			}
		}
		return result;
	}

	private void handleEObjects(List<EObject> selectedEObjects, boolean mergeResults) {
		for (EObject selectedEObject : selectedEObjects) {
			handleEObject(selectedEObject);				
		}
		
		if (mergeResults && selectedEObjects.size() > 1) {
			LinkedList<ResultModel> resultModels = RuntimeManager.getResultModels();
			List<ResultModel> lastNResultModels = resultModels.subList(resultModels.size() - selectedEObjects.size(), resultModels.size());
			
			ResultModel mergedResultModel = mergeResultModels(lastNResultModels);
			
			for (int i = 1; i <= selectedEObjects.size(); ++i) {
				resultModels.removeLast();
			}
			
			resultModels.add(mergedResultModel);
			
			RuntimeManager.getResultModelTreeViewer().setInput(resultModels);
			RuntimeManager.getResultModelTreeViewer().refresh();
		}
	}

	private void handleEObject(EObject selectedEObject) {
		String path = selectedEObject.eResource().getURI().toPlatformString(true);
		IFile selectedFile = (IFile) ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		IProject selectedProject = selectedFile.getProject();
		
		RuntimeManager.findConfiguredModelSmells(selectedProject, selectedEObject, selectedFile);
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
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if (!(selection instanceof IStructuredSelection))
			return false;
		

		List<?> selectedElementsList = ((IStructuredSelection) selection).toList();
		

		return selectedElementsList.stream().allMatch(elm -> (elm instanceof EObject) ||
				(elm instanceof IContainer) ||
				((elm instanceof IFile) && ((IFile) elm).getFileExtension().equals("ecore")));
	}

	
}
