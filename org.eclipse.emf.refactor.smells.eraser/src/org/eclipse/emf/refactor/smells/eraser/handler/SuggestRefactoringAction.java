package org.eclipse.emf.refactor.smells.eraser.handler;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.refactor.refactoring.core.Refactoring;
import org.eclipse.emf.refactor.smells.core.ModelSmell;
import org.eclipse.emf.refactor.smells.eraser.managers.EraseManager;
import org.eclipse.emf.refactor.smells.eraser.ui.SuggestionDialog;
import org.eclipse.emf.refactor.smells.runtime.core.EObjectGroup;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class SuggestRefactoringAction implements IObjectActionDelegate {
	
	private Shell shell;
	private ISelection selection;

	public SuggestRefactoringAction() { 
		super();
	}

	@Override
	public void run(IAction action) {
		EObjectGroup group = null;
		if(selection instanceof IStructuredSelection){
			IStructuredSelection structSelect = (IStructuredSelection)selection;
			if(!structSelect.isEmpty() && structSelect.getFirstElement() instanceof EObjectGroup)
				group = (EObjectGroup) structSelect.getFirstElement();
		}
		if(group != null){
			System.out.println("Group: " +  group);			
			ModelSmell smell = group.getModelSmell();
			SuggestionDialog dialog = new SuggestionDialog(shell, EraseManager.getInstance().getCausedModelSmells(EraseManager.getInstance().getFixingRefactorings(smell)), group);
			int dialogResult = dialog.open();
			if(dialogResult == Dialog.OK) {
				Refactoring selectedRefactoring = dialog.getSelectedRefactoring();
				EObject contextObject = dialog.getSelectedContextObject();
				ArrayList<EObject> contextList = new ArrayList<EObject>();
				contextList.add(contextObject);
				selectedRefactoring.getController().setSelection(contextList);
				selectedRefactoring.getController().getDataManagementObject().preselect(contextList);
				RefactoringWizard wizard = selectedRefactoring.getGui().show();
				RefactoringWizardOpenOperation wizardDialog = new RefactoringWizardOpenOperation(wizard);
				try {
					wizardDialog.run(shell, "EMF Refactor");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

}
