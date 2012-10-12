package org.eclipse.emf.refactor.smells.managers;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

public class SelectionManager {

	public static EObject getEObject(ISelection selection) {
		if (selection == null)
			return null;
		if (selection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) selection;
			Object o = ss.getFirstElement();
			if (o instanceof EObject) {
				return (EObject) o;
			}
		}		
		return null;
	}

	public static IFile getFile(ISelection selection) {
		if (selection == null)
			return null;
		if (selection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) selection;
			Object o = ss.getFirstElement();
			if (o instanceof IFile) {
				return (IFile) o;
			}
		}		
		return null;
	}

}
