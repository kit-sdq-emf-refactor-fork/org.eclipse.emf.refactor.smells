package org.eclipse.emf.refactor.smells.papyrus;

import org.eclipse.emf.refactor.smells.papyrus.ui.PapyrusHighlighting;
import org.eclipse.emf.refactor.smells.runtime.managers.RuntimeManager;
import org.eclipse.ui.IStartup;

public class PapyrusStartup implements IStartup {

	@Override
	public void earlyStartup() {
		RuntimeManager.getInstance(new PapyrusHighlighting());
		System.out.println("Started");
	}

}
