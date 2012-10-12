package org.eclipse.emf.refactor.smells.runtime.ui.actions;

import org.eclipse.emf.refactor.smells.runtime.managers.RuntimeManager;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ClearAction extends Action {
	
	public ClearAction() {
		super();
		this.setText("Clear Results");
		this.setToolTipText("Remove all results from the list");
		this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE));
		
	}
	
	public void run() {
		RuntimeManager.clearResultModels();
	}

}
