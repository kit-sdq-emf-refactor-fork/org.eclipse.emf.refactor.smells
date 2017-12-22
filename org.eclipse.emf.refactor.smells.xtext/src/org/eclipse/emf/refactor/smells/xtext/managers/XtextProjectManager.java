package org.eclipse.emf.refactor.smells.xtext.managers;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.refactor.smells.managers.ProjectManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.utils.EditorUtils;

public class XtextProjectManager extends ProjectManager {
	
	@SuppressWarnings("finally")
	public static IProject getActualProject(ISelection selection) {
		IProject actualProject = ProjectManager.getActualProject();
		System.out.println("Project: " + actualProject);
		try {
			XtextEditor editor = EditorUtils.getActiveXtextEditor();
			if (editor != null) {
				IEditorInput input = editor.getEditorInput();
				System.out.println("Editor Input: " + input);
				if (input instanceof IFileEditorInput) {
					IFileEditorInput fileInput = (IFileEditorInput) input;
					actualProject = fileInput.getFile().getProject();
					System.out.println("Project: " + actualProject);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return actualProject;
		}
	}

}
