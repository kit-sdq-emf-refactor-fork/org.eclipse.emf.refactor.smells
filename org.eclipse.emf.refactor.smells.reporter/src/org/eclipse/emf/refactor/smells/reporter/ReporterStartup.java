package org.eclipse.emf.refactor.smells.reporter;

import org.eclipse.emf.refactor.smells.reporter.actions.ExportAction;
import org.eclipse.emf.refactor.smells.runtime.managers.RuntimeManager;
import org.eclipse.ui.IStartup;

public class ReporterStartup implements IStartup {

	@Override
	public void earlyStartup() { 
		RuntimeManager.getInstance(new ExportAction());
		System.out.println("Started");
	}

}
