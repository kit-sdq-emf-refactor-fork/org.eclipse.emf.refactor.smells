package org.eclipse.emf.refactor.smells.generator.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.emf.refactor.smells.core.ModelSmell;
import org.eclipse.emf.refactor.smells.generator.interfaces.INewModelSmellWizard;
import org.eclipse.emf.refactor.smells.generator.managers.XMLPluginFileManager;
import org.eclipse.emf.refactor.smells.managers.ModelSmellManager;

/**
 * The wizard page requesting the basic data needed for the generation of every smell plugin.
 * <br>- It is the starting page of the wizard and determines the following pages.
 * 
 * @author Matthias Burhenne
 *
 */

public class ModelSmellDataPage extends WizardPage implements Listener {

	private static final String PAGE_NAME = "org.eclipse.emf.refactor.smells.ModelSmellDataPage";
	private static final String TITLE = "New Model Smell: Basic Data";
	private static final String DESCRIPTION = "Please specify basic model smell data. " +
												"Required fields are denoted by \"(*)\".";
	private Combo projectCombo;
	private Text nameTextField;
	private Text idTextField;
	private Text descriptionTextField;
	private Combo metamodelCombo;

	private boolean initialization = false;
	
	public ModelSmellDataPage() {
		super(PAGE_NAME);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);
		createTextFields(container);
		initContents();
		setControl(container);
		this.setPageComplete(false);
	}
	
	private void initContents() {
		initialization=true;
		initProjectsAndMetamodels();
		initialization=false;
	}
	
	private void initProjectsAndMetamodels() {
		for (IProject project : ((INewModelSmellWizard) getWizard()).getProjects()) {
			projectCombo.add(project.getName());
		}
		Object [] metamodelObjects = EPackage.Registry.INSTANCE.keySet().toArray(new Object[EPackage.Registry.INSTANCE.size()]);
        Arrays.sort(metamodelObjects);
		for(Object object : metamodelObjects){
			metamodelCombo.add(object.toString());
		}
	}

	private void createTextFields(Composite container) {
		Label label;
		GridData gridData;
		Group group;
		GridLayout layout;
		// - Project -
		group = new Group(container, SWT.NONE);
		group.setText("Project");
	    layout = new GridLayout();
	    layout.numColumns = 2;
	    group.setLayout(layout);
	    gridData = new GridData(GridData.FILL_HORIZONTAL);
	    group.setLayoutData(gridData);
		// col:1
		label = new Label(group, SWT.NONE);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		label.setText("Target project (*):");
		// col:2
		projectCombo = new Combo(group, SWT.READ_ONLY);
		final GridData projectComboData = new GridData(GridData.FILL_HORIZONTAL);
		projectCombo.setLayoutData(projectComboData);
		//projectCombo.setText("Select Project");
		projectCombo.addListener(SWT.Selection, (org.eclipse.swt.widgets.Listener)this);
		// - Modelsmell Data -
		group = new Group(container, SWT.NONE);
		group.setText("Model Smell data");
	    layout = new GridLayout();
	    layout.numColumns = 2;
	    group.setLayout(layout);
	    gridData = new GridData(GridData.FILL_HORIZONTAL);
	    group.setLayoutData(gridData);
		// - Name -
		// col:1
		label = new Label(group, SWT.NONE);
		label.setText("Name (*):");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		// col:2
		nameTextField = new Text(group, SWT.BORDER);
		nameTextField.addListener(SWT.Modify, this);
		nameTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// - Id -
		// col:1
		label = new Label(group, SWT.NONE);
		label.setText("Id (*):");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		// col:2
		idTextField = new Text(group, SWT.BORDER);
		idTextField.addListener(SWT.Modify, this);
		idTextField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// - Description -
		// col:1
		label = new Label(group, SWT.NONE);
		label.setText("Description:");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		// col:2
		descriptionTextField = new Text(group, SWT.BORDER);
		descriptionTextField.addListener(SWT.Modify, this);
		descriptionTextField.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));
		// - Metamodel -
		// col:1
		label = new Label(group, SWT.NONE);
		label.setText("Meta model (*):");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		// col:2
		
		metamodelCombo = new Combo(group, SWT.READ_ONLY);
		final GridData metamodelComboData = new GridData(GridData.FILL_HORIZONTAL);
		metamodelCombo.setLayoutData(metamodelComboData);
		//projectCombo.setText("Select Project");
		metamodelCombo.addListener(SWT.Selection, (org.eclipse.swt.widgets.Listener)this);
		// - Source -
//		group = new Group(container, SWT.NONE);
//		group.setText("Generation Options");
//	    layout = new GridLayout();
//	    layout.numColumns = 2;
//	    group.setLayout(layout);
//	    gridData = new GridData(GridData.FILL_HORIZONTAL);
//	    group.setLayoutData(gridData);
//		// col:1
//		label = new Label(group, SWT.NONE);
//		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
//		label.setText("Source *:");
//		// col:2
//		sourceCombo = new Combo(group, SWT.READ_ONLY);
//		final GridData comboData = new GridData(GridData.FILL_HORIZONTAL);
//		sourceCombo.setLayoutData(comboData);
//		sourceCombo.setItems(new String[] { GENERATE_CODE_SKELETON_OPTION_LABEL, 
//				GENERATE_MODEL_SMELL_FROM_HENSHIN_FILE_OPTION_LABEL,
//				GENERATE_MODEL_SMELL_FROM_METRIC_OPTION_LABEL});
//		sourceCombo.setText("Select generation type");
//		sourceCombo.addListener(SWT.Selection, this);
		
	}



	@Override
	public void handleEvent(Event event) {
		if (event.widget == projectCombo)
			((INewModelSmellWizard) getWizard()).setNewModelSmellTargetProject(projectCombo.getText());
		if(!initialization ){
			updatePageComplete();
			getWizard().getContainer().updateButtons();		
		}		
	}
	
	@Override
	public boolean canFlipToNextPage() {
		if (((INewModelSmellWizard) getWizard()).getPageNumbers() > 1) {
			if (this.isPageComplete()) {
//				((INewModelSmellWizard) getWizard()).updateSecondPage();
				return true;
			}
		} 
		return false;
	}
	
	@Override
	public WizardPage getNextPage() {
		if (((INewModelSmellWizard) getWizard()).getPageNumbers() > 1) {
			((INewModelSmellWizard) getWizard()).updateSecondPage();
			return ((INewModelSmellWizard) getWizard()).getSecondPage();
		} else {
			return null;
		}
	}

	/**
	 * Wird jedes mal ausgeführt wenn sich der Inhalt eines Textfeldes im Wizard
	 * verändert. Überprüft die inhalte der Textfelder und erzeugt
	 * entschprechende Meldungen im Wizardfenster.
	 */
	private void updatePageComplete() {

		if(nameTextField.getText().isEmpty()){
			this.setMessage("Model smell name is not specified.", ERROR);
			this.setPageComplete(false);
		} else
		if(idTextField.getText().isEmpty()){
			this.setMessage("Model smell ID is not specified.", ERROR);
			this.setPageComplete(false);
		} else
		if(!checkNewID()){
			this.setMessage("The ID you provided is already in use", ERROR);
			this.setPageComplete(false);
		} else
		if(metamodelCombo.getText().isEmpty()){
			this.setMessage("Meta model is not specified.", ERROR);
			this.setPageComplete(false);
		} else
		if(!checkProject()){
			this.setMessage("Target project is not specified.", ERROR);
			this.setPageComplete(false);
		} else {
			this.setMessage("");
			saveTextFieldValues();
			this.setPageComplete(true);			
		}
	}
	
	private boolean checkNewID(){
		ArrayList<String> usedIDs = new ArrayList<String>();
		for(ModelSmell smell : ModelSmellManager.getAllModelSmells()){
			usedIDs.add(smell.getId());
		}
		LinkedList<IProject> pluginProjects = ((INewModelSmellWizard) this.getWizard()).getProjects();
		for(IProject project : pluginProjects){
			usedIDs.addAll(XMLPluginFileManager.readSmellIDs(project));
		}
		return !usedIDs.contains(this.idTextField.getText());

	}

	private void saveTextFieldValues() {
		((INewModelSmellWizard) getWizard()).setNewSmellId(this.idTextField.getText());
		((INewModelSmellWizard) getWizard()).setNewSmellName(this.nameTextField.getText()); 
		((INewModelSmellWizard) getWizard()).setNewSmellDescription(this.descriptionTextField.getText()); 
		((INewModelSmellWizard) getWizard()).setNewSmellMetamodel(this.metamodelCombo.getText()); 
	}
	
	private boolean checkProject(){
		if(!projectCombo.getText().isEmpty())
			return true;
		return false;
	}

	@Override
	public boolean isPageComplete() {
		if(initialization)
			return false;
		return super.isPageComplete();
	}
	
	
}
