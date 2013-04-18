package org.eclipse.emf.refactor.smells.papyrus.ui;

import org.eclipse.emf.refactor.smells.interfaces.IHighlighting;
import org.eclipse.emf.refactor.smells.papyrus.managers.HighlightManager;

public class PapyrusHighlighting implements IHighlighting {

	@Override
	public void highlight(Object selection) {
		HighlightManager.getInstance().highlight(selection);
	}

}
