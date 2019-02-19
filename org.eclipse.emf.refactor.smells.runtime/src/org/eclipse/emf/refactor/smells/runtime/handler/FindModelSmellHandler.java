package org.eclipse.emf.refactor.smells.runtime.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.refactor.smells.runtime.managers.RuntimeManager;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
public class FindModelSmellHandler extends AbstractHandler {

	private static final String ECORE_EXTENSION = "ecore";
	private static final Shell SHELL = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	private ResourceSet resourceSet;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		TransactionalEditingDomain transactionalEditingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
		resourceSet = transactionalEditingDomain.getResourceSet();
		
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		
		if (!(selection instanceof IStructuredSelection))
			throw new ExecutionException("Selection is not instance of IStructuredSelection and could not be handled");
		

		List<?> selectedElementsList = ((IStructuredSelection) selection).toList();
		
		Cursor oldCursor = SHELL.getCursor();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setCursor(new Cursor(null,SWT.CURSOR_WAIT));
		Set<EPackage> toAnalyze = new HashSet<EPackage>();
		
		List<EPackage> selectedEPackages = selectedElementsList.stream().filter(o -> o instanceof EPackage).map(o -> (EPackage) o).collect(Collectors.toList());
		toAnalyze.addAll(selectedEPackages);
		
		List<IContainer> selectedContainers = selectedElementsList.stream().filter(o -> o instanceof IContainer).map(o -> (IContainer) o).collect(Collectors.toList());
		for (IContainer container : selectedContainers) {
			try {
				toAnalyze.addAll(getRootEPackages(container));
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
		List<IFile> selectedFiles = selectedElementsList.stream().filter(o -> o instanceof IFile).map(o -> (IFile) o).collect(Collectors.toList());
		selectedFiles.stream().forEach(file -> toAnalyze.add(getRootEPackageFromEcoreFile(file)));
		
		EPackage elm = toAnalyze.iterator().next();
		String path = elm.eResource().getURI().toPlatformString(true);
		IFile selectedFile = (IFile) ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		IProject selectedProject = selectedFile.getProject();
		
		
		EPackage dummyPackage = EcoreFactory.eINSTANCE.createEPackage();
		dummyPackage.getESubpackages().addAll(toAnalyze);
		
		RuntimeManager.findConfiguredModelSmells(selectedProject, dummyPackage, selectedFile);
		SHELL.setCursor(oldCursor);
		disposeResourceSet(resourceSet);
		return null;
		
	}
	
    public void disposeResourceSet(ResourceSet resourceSet) {
        // unload and delete all resources
        Iterator<Resource> iterator = resourceSet.getResources().iterator();
        while (iterator.hasNext()) {
            Resource res = iterator.next();
            res.unload();
            iterator.remove();
        }
    }

	private Collection<EPackage> getRootEPackages(IContainer container) throws CoreException {
		List<IFile> ecoreFiles = getEcoreFiles(container);
		List<EPackage> ePackages = ecoreFiles.stream().map(elm -> getRootEPackageFromEcoreFile(elm)).collect(Collectors.toList());
		return ePackages;
	}


	private EPackage getRootEPackageFromEcoreFile(IFile ecoreFile) {
		URI uri = URI.createPlatformResourceURI(ecoreFile.getFullPath().toString(), true); 
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(ECORE_EXTENSION, new EcoreResourceFactoryImpl());
		Resource resource = resourceSet.getResource(uri, true);
		EcoreUtil.resolveAll(resource);
		return (EPackage) resource.getContents().get(0);
	}

	private List<IFile> getEcoreFiles(IContainer container) throws CoreException {
		List<IFile> result = new ArrayList<IFile>();
		for (IResource r : container.members()) {
			if (r instanceof IContainer)
				result.addAll(getEcoreFiles((IContainer) r));
			else if ((r instanceof IFile) && r.getFileExtension().equals(ECORE_EXTENSION))
				result.add((IFile) r);
		}
		return result;
	}


	@Override
	public boolean isEnabled() {
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if (!(selection instanceof IStructuredSelection))
			return false;
		

		List<?> selectedElementsList = ((IStructuredSelection) selection).toList();
		

		return selectedElementsList.stream().allMatch(elm -> (elm instanceof EObject) ||
				(elm instanceof IContainer) ||
				((elm instanceof IFile) && ((IFile) elm).getFileExtension().equals(ECORE_EXTENSION)));
	}

	
}
