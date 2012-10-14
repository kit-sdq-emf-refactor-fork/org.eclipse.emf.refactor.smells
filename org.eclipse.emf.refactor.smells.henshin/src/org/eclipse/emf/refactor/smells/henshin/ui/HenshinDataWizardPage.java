package org.eclipse.emf.refactor.smells.henshin.ui;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.refactor.smells.henshin.managers.HenshinFileManager;
import org.eclipse.emf.refactor.smells.henshin.managers.HenshinRuntimeManager;
import org.eclipse.emf.refactor.smells.managers.FileManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * The wizard page for specific data concerning the generation of smell plugins based on EMF Henshin
 * 
 * @author Matthias Burhenne
 *
 */

public class HenshinDataWizardPage extends WizardPage implements Listener {

	private static final String HENSHIN_EXT = ".henshin";
	private static final String PAGE_NAME = "org.eclipse.emf.refactor.smells.HenshinDataWizardPage";
	private static final String TITLE = "New Model Smell: Henshin Data";
	private static final String DESCRIPTION = "Please specify the corresponding Henshin transformation file.";
	private Combo transformationCombo;
	private String transf;
	private boolean initializing = false;
	
	public HenshinDataWizardPage() {
		super(PAGE_NAME);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {
		final Shell shell = parent.getShell();
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);
		setControl(container);
		createContent(container, shell);
		this.setPageComplete(false);
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public void handleEvent(Event event) {
		updatePageComplete();
		getWizard().getContainer().updateButtons();
	}

	private void createContent(Composite container, Shell shell) {
		Label label;
		Button button;
		GridData gridData;
		Group group;
		GridLayout layout;
		group = new Group(container, SWT.NONE);
		group.setText("Henshin file");
		layout = new GridLayout();
		layout.numColumns = 3;
		group.setLayout(layout);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gridData);
		// - Pre -
//		label = new Label(group, SWT.NONE);
//		label.setText("Pre-Trensformation file:");
//		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
//		preCombo = new Combo(group, SWT.READ_ONLY);
//		preCombo.addListener(SWT.Modify, this);
//		preCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		button = new Button(group, SWT.NONE);
//		button.addSelectionListener(new HenshinDataSelectionAdapter(shell,
//				preCombo));
//		button.setText("Import");
		// - Transf -
		label = new Label(group, SWT.NONE);
		label.setText("Model smell finder file:");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		transformationCombo = new Combo(group, SWT.READ_ONLY);
		transformationCombo.addListener(SWT.Modify, this);
		transformationCombo
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		button = new Button(group, SWT.NONE);
		button.addSelectionListener(new HenshinDataSelectionAdapter(shell,
				transformationCombo));
		button.setText("Import");
		// - Post -
//		label = new Label(group, SWT.NONE);
//		label.setText("Post-Trensformation file:");
//		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
//		postCombo = new Combo(group, SWT.READ_ONLY);
//		postCombo.addListener(SWT.Modify, this);
//		postCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		button = new Button(group, SWT.NONE);
//		button.addSelectionListener(new HenshinDataSelectionAdapter(shell,
//				postCombo));
//		button.setText("Import");
	}

	public void initContents() {
		IProject project = ((NewModelSmellWizardHenshin) getWizard()).getNewSmellTargetProject();
		String path = project.getLocationURI().getPath() + NewModelSmellWizardHenshin.TRANSFORMATIONS_DIR;
		setCombos(HenshinFileManager.getAllHenshinFiles(path));
	}

	private void setCombos(File[] files) {
		initializing = true;
//		preCombo.removeAll();
		transformationCombo.removeAll();
//		postCombo.removeAll();
		if (files != null) {
			for (File file : files) {
//				preCombo.add(file.getName());
				transformationCombo.add(file.getName());
//				postCombo.add(file.getName());
			}
		}
		initializing = false;
	}

	/**
	 * Überprüft die inhalte der Textfelder und erzeugt entsprechende
	 * Meldungen im Wizardfenster.
	 */
	private void updatePageComplete() {
		if(initializing)
			setPageComplete(false);
		if (!initializing)
			if (!checkTransformationTextField()) {
				setMessage(
						"Metric-Calculation file not specified, or file does not exist.",
						ERROR);
				this.setPageComplete(false);
				getWizard().getContainer().updateButtons();
			} else {
				setMessage("");
				saveContents();
				this.setPageComplete(true);
				getWizard().getContainer().updateButtons();
			}
	}

	private boolean checkTransformationTextField() {
		final String transf = this.transformationCombo.getText();
		if (!transf.isEmpty())
			return true;
		return false;
	}

	private void saveContents() {
//		this.pre = this.preCombo.getText();
		this.transf = this.transformationCombo.getText();
//		this.post = this.postCombo.getText();
	}

//	public String getPre() {
//		return pre;
//	}

	public String getTransformation() {
		return transf;
	}

//	public String getPost() {
//		return post;
//	}

	private void addToCombos(String name){
//		preCombo.add(name,0);
		transformationCombo.add(name,0);
//		postCombo.add(name,0);
	}
	

	// -------------------------------------------------------------------------------
	private class HenshinDataSelectionAdapter extends SelectionAdapter {

		private final Combo combo;
		private final Shell shell;

		public void widgetSelected(SelectionEvent e) {
			String result = browseForTransformation(shell);
			if (result != null)
				result=result.replace('\\', '/');
				boolean correctHenshinFormat = HenshinRuntimeManager.isModelSmellFile(result);
				if (! result.isEmpty() && correctHenshinFormat) {
					int i = result.lastIndexOf("/");
					String name = result.substring(i + 1);
					String destination = 
							((NewModelSmellWizardHenshin) getWizard()).getNewSmellTargetProject().getLocationURI().getPath()
							+ NewModelSmellWizardHenshin.TRANSFORMATIONS_DIR;
					FileManager.copyFile(result, destination, name);
					addToCombos(name);
					combo.select(0);
				} else {
					if(! correctHenshinFormat){
						MessageDialog.openError(
								shell,
								"Error when trying to import Henshin File",
								"The henshin file you selected does not follow the \n guidelines for a model smell file.");
					}
				}
		}

		private HenshinDataSelectionAdapter(Shell shell, Combo combo) {
			this.shell = shell;
			this.combo = combo;
		}

		private String browseForTransformation(Shell shell) {
			FileDialog fd = new FileDialog(shell, SWT.OPEN);
			fd.setText("Select Henshin-File");
			if (((NewModelSmellWizardHenshin) getWizard()).getNewSmellTargetProject() != null)
				fd.setFilterPath(((NewModelSmellWizardHenshin) getWizard()).getNewSmellTargetProject().getLocation().toString());
			String[] filterExt = { "*"+HENSHIN_EXT };
			fd.setFilterExtensions(filterExt);
			String selected = fd.open();
			return selected;
		}
	}
	
}