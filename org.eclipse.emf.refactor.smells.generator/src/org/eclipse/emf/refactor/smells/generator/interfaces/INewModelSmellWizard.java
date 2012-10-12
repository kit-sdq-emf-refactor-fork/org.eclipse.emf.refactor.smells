package org.eclipse.emf.refactor.smells.generator.interfaces;

import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;

public interface INewModelSmellWizard {

	public LinkedList<IProject> getProjects();

	public void setNewModelSmellTargetProject(String text);

	public int getPageNumbers();

	public WizardPage getSecondPage();

	public void updateSecondPage();

	public void setNewSmellId(String text);

	public void setNewSmellName(String text);

	public void setNewSmellDescription(String text);

	public void setNewSmellMetamodel(String text);

	public String getNewSmellMetamodel();
}
