package org.eclipse.emf.refactor.smells.xtext.ui;

import org.eclipse.emf.refactor.smells.interfaces.IHighlighting;
import org.eclipse.emf.refactor.smells.xtext.managers.XtextHighlightManager;

public class XtextHighlighting implements IHighlighting {

	@Override
	public void highlight(Object selection) {
		XtextHighlightManager.getInstance().highlight(selection);
	}

}
