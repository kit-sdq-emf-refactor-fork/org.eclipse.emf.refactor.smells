package org.eclipse.emf.refactor.smells.reporter.actions;

import org.eclipse.emf.refactor.smells.reporter.managers.ReportManager;
import org.eclipse.emf.refactor.smells.reporter.ui.ReportingWizard;
import org.eclipse.emf.refactor.smells.reporter.ui.ReportingWizardDialog;
import org.eclipse.emf.refactor.smells.runtime.managers.RuntimeManager;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ExportAction extends Action {

	public ExportAction() {
		super();
		this.setText("Export Results");
		this.setToolTipText("Export the results list in various reports");
		this.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_PRINT_EDIT));
	}

	public void run() {
		RuntimeManager.getInstance();
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();	
		ReportManager reportManager = new ReportManager(shell, RuntimeManager.getResultModels());	
		ReportingWizard wizard = new ReportingWizard(reportManager);
		ReportingWizardDialog dialog = new ReportingWizardDialog(shell, wizard);
		dialog.create();
		dialog.open();
	}

}
