package org.eclipse.emf.refactor.smells.xtext.managers;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.refactor.smells.managers.SelectionManager;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocumentUtil;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

public class XtextSelectionManager extends SelectionManager {

	public static EObject getRoot(ISelection selection) {
		if (null != selection && selection instanceof TextSelection) {
    		TextSelection tSelection = (TextSelection) selection;
    		XtextEditor xEditor = EditorUtils.getActiveXtextEditor();
    		if (null != xEditor) {
	    		IXtextDocument doc = XtextDocumentUtil.get(xEditor);
	    		if (null != doc) {
		    		XtextResource resource;
		    		resource = doc.readOnly(new IUnitOfWork<XtextResource, XtextResource>() {
			    		public XtextResource exec(XtextResource state) throws Exception {
			    			return state;
			    		}
			    	});
		    		EObjectAtOffsetHelper helper = new EObjectAtOffsetHelper();
		    		EObject eObject = helper.resolveElementAt(resource , tSelection.getOffset());
		    		System.out.println("EObject: " +  eObject);
		    		if (eObject != null) {
		    			System.out.println("Root: " + EcoreUtil.getRootContainer(eObject));
		    			return EcoreUtil.getRootContainer(eObject);
		    		}
	    		}
    		}
    	}
		return null;
	}

}
