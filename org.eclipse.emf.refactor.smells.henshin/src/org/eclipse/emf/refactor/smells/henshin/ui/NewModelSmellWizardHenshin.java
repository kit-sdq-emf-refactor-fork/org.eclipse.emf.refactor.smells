package org.eclipse.emf.refactor.smells.henshin.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.refactor.smells.generator.interfaces.INewModelSmellWizard;
import org.eclipse.emf.refactor.smells.generator.ui.ModelSmellDataPage;
import org.eclipse.emf.refactor.smells.henshin.core.HenshinModelSmellInfo;
import org.eclipse.emf.refactor.smells.henshin.managers.HenshinGenerationManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewModelSmellWizardHenshin extends Wizard implements INewWizard, INewModelSmellWizard {
	
	private final String WINDOW_TITLE = "New Model Smell";
	public static final String TRANSFORMATIONS_DIR = "/transformations/";
	private ModelSmellDataPage dataPage;
	private HenshinDataWizardPage henshinPage;
	private LinkedList<IProject> projects;
	
	private String newSmellId;
	private String newSmellName;
	private String newSmellMetamodel;
	private String newSmellDescription;
	private IProject newSmellTargetProject;
	
	public NewModelSmellWizardHenshin() { }
	
	@Override
	public void addPages() {
		setWindowTitle(WINDOW_TITLE);
		dataPage = new ModelSmellDataPage();
		addPage(dataPage);
		henshinPage = new HenshinDataWizardPage();
		addPage(henshinPage);
	}

	@Override
	public boolean canFinish() {
		return (dataPage.isPageComplete() && henshinPage.isPageComplete());
	}

	@Override
	public IWizardPage getStartingPage() {
		return dataPage;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		initProjects();
	}

	private void initProjects() {
		this.projects = new LinkedList<IProject>();
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : allProjects) {
			if (project.isOpen()) {
				IProjectNature nature = null;
				try {
					nature = project.getNature("org.eclipse.pde.PluginNature");
				} catch (CoreException e) {
					e.printStackTrace();
				}
				if (null != nature) 
					this.projects.add(project);
			}
		}
	}

	@Override
	public boolean performFinish() {
		try{
			getContainer().run(true, true, new IRunnableWithProgress(){
				public void run(IProgressMonitor monitor)throws InvocationTargetException, InterruptedException {
					HenshinGenerationManager.getInstance();
					HenshinGenerationManager.createNewModelSmell(monitor, getModelSmellInfo(), newSmellTargetProject);
				}
			});
		}
		catch(InvocationTargetException e){
			e.printStackTrace();
			return false;
		}
		catch(InterruptedException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private HenshinModelSmellInfo getModelSmellInfo() {
		String transformation = henshinPage.getTransformation();
		HenshinModelSmellInfo info = 
				new HenshinModelSmellInfo(newSmellId, newSmellName, 
						newSmellDescription, newSmellMetamodel, newSmellTargetProject,  
						transformation);
		return info;
	}

	@Override
	public LinkedList<IProject> getProjects() {
		return this.projects;
	}

	@Override
	public void setNewModelSmellTargetProject(String projectName) {
		for(IProject project : projects){
			String path = project.getName();
			if(path.equals(projectName))
				setNewSmellTargetProject(project);
		}		
	}
	
	public void setNewSmellTargetProject(IProject newSmellTargetProject) {
		this.newSmellTargetProject = newSmellTargetProject;
	}
	
	public IProject getNewSmellTargetProject() {
		return this.newSmellTargetProject;
	}

	@Override
	public int getPageNumbers() {
		return 2;
	}

	@Override
	public WizardPage getSecondPage() {
		return henshinPage;
	}

	@Override
	public void updateSecondPage() {
		henshinPage.initContents();
	}

	@Override
	public void setNewSmellId(String id) {
		this.newSmellId = id;
	}

	@Override
	public void setNewSmellName(String name) {
		this.newSmellName = name;
	}

	@Override
	public void setNewSmellDescription(String desc) {
		this.newSmellDescription = desc;
	}

	@Override
	public void setNewSmellMetamodel(String newSmellMetamodel) {
		this.newSmellMetamodel = newSmellMetamodel;
	}

	@Override
	public String getNewSmellMetamodel() {
		return newSmellMetamodel;
	}

}
