package org.eclipse.emf.refactor.smells.papyrus.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.refactor.smells.managers.SelectionManager;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.papyrus.infra.emf.utils.EMFHelper;

//@SuppressWarnings("restriction")
public class PapyrusSelectionManager extends SelectionManager {

	public static EObject getEObject(ISelection selection) {
		if (selection == null)
			return null;
		EObject ret = SelectionManager.getEObject(selection);
		
		if (selection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) selection;
			Object o = ss.getFirstElement();
//			if (o instanceof ModelElementItem) {
//				System.out.println("instanceof ModelElementItem");
//				ModelElementItem mei = (ModelElementItem) o;
//	    		System.out.println("element: " + mei.getEObject());
//	    		ret = mei.getEObject();
//			} else {
				if (o instanceof IGraphicalEditPart) {
		    		System.out.println("instanceof IGraphicalEditPart");
		    		IGraphicalEditPart gep = (IGraphicalEditPart) o;
		    		System.out.println("element: " + gep.resolveSemanticElement());
		    		ret = gep.resolveSemanticElement();
		    	} else {
		    		return EMFHelper.getEObject(o);
		    	}
//			}
		}
		return ret;
	}

	public static Object getObject(ISelection selection) {
		if (selection == null)
			return null;
		if (selection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) selection;
			return ss.getFirstElement();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Object[] getSelection(ISelection selection) {
		System.out.println("selection instanceof StructuredSelection: " + 
									(selection instanceof StructuredSelection));
		if (null != selection) {
			if (selection instanceof StructuredSelection) {
				StructuredSelection ss = (StructuredSelection) selection;
				if (null == ss.getFirstElement())
					return ss.toArray();
				List<Object> list = new ArrayList<Object>();
				final Iterator<Object> i = ss.iterator();
				while (i.hasNext()) {
					final Object selectedEObject = i.next();
					list.add(selectedEObject);
				}
				return list.toArray();
			}
		}
		return new Object[0];
	}

	public static Diagram getDiagram(ISelection selection) {
		for (Object o : getSelection(selection)) {
			if (o instanceof IGraphicalEditPart) {
		   	System.out.println("instanceof IGraphicalEditPart");
		    	IGraphicalEditPart gep = (IGraphicalEditPart) o;
		    	System.out.println("element: " + gep.resolveSemanticElement());
		    	System.out.println("notation view: " + gep.getNotationView());
		    	System.out.println("notation view element: " + gep.getNotationView().getElement());
		    	EObject root = EcoreUtil.getRootContainer(gep.getNotationView());
		    	System.out.println("root: " + root);
		    	return (Diagram) root;
		    }
		}
		return null;
	}

}
