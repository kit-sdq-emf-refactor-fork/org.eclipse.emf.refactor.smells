package org.eclipse.emf.refactor.smells.papyrus.managers;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.facet.infra.browser.uicore.internal.model.ModelElementItem;
import org.eclipse.emf.refactor.smells.managers.SelectionManager;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

@SuppressWarnings("restriction")
public class PapyrusSelectionManager extends SelectionManager {

	public static EObject getEObject(ISelection selection) {
		if (selection == null)
			return null;
		EObject ret = SelectionManager.getEObject(selection);
		
		if (selection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) selection;
			Object o = ss.getFirstElement();
			if (o instanceof ModelElementItem) {
				System.out.println("instanceof ModelElementItem");
				ModelElementItem mei = (ModelElementItem) o;
	    		System.out.println("element: " + mei.getEObject());
	    		ret = mei.getEObject();
			} else {
				if (o instanceof IGraphicalEditPart) {
		    		System.out.println("instanceof IGraphicalEditPart");
		    		IGraphicalEditPart gep = (IGraphicalEditPart) o;
		    		System.out.println("element: " + gep.resolveSemanticElement());
		    		ret = gep.resolveSemanticElement();
		    	} else {
		    		return null;
		    	}
			}
		}
		return ret;
	}

}
