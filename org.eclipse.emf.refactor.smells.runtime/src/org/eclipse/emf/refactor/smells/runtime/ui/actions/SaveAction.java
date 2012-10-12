package org.eclipse.emf.refactor.smells.runtime.ui.actions;

import java.util.LinkedList;

import org.eclipse.emf.refactor.smells.runtime.core.ResultModel;
import org.eclipse.emf.refactor.smells.runtime.managers.XMLResultsManager;
import org.eclipse.emf.refactor.smells.runtime.ui.ResultModelTreeViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class SaveAction extends Action {
	
	private Shell shell;
	private ResultModelTreeViewer viewer;

	public SaveAction(Shell shell, ResultModelTreeViewer viewer) {
		super();
		this.shell = shell;
		this.viewer = viewer;
		this.setText("Save Results");
		this.setToolTipText("Save the results list to a file");
		this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT));
	}

	@SuppressWarnings("unchecked")
	public void run() {
		FileDialog fd = new FileDialog(shell, SWT.SAVE);
		fd.setText("EMF Smell: Save Results");
		fd.setFilterPath("C:/");
		String[] filterExt = { "*.xml", "*.*" };
		fd.setFilterExtensions(filterExt);
		String selected = fd.open();
		System.out.println("Selected: " + selected);
		if (selected != null) {
			XMLResultsManager.saveResults(selected, (LinkedList<ResultModel>) viewer.getInput(), shell, true);
		}
	}

}
