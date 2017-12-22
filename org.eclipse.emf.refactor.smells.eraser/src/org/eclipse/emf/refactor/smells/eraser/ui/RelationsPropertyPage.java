package org.eclipse.emf.refactor.smells.eraser.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.refactor.smells.eraser.managers.EraseManager;
import org.eclipse.emf.refactor.smells.eraser.managers.PluginXMLManager;
import org.eclipse.emf.refactor.smells.eraser.utils.ModelRefactoringStub;
import org.eclipse.emf.refactor.smells.eraser.utils.ModelSmellStub;
import org.eclipse.emf.refactor.smells.eraser.utils.ProjectEntries;
import org.eclipse.emf.refactor.smells.eraser.utils.SetSorter;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * Property page for an easier definition of relations between model smells and model refactorings.
 * 
 * @author Matthias Burhenne
 *
 */
public class RelationsPropertyPage extends PropertyPage {

	private static final String DESCRIPTION_COLUMN_LABEL = "Description";
	private static final String NAME_COLUMN_LABEL = "Name";
	private static final String CHECK_COLUMN_LABEL = "Selected";
	private static final String SMELLS_TAB_LABEL = "Usable Refactorings to erase Smell";
	private static final String REFACTORINGS_TAB_LABEL = "Smells possibly caused by Refactoring";
	private static final String METAMODEL_LABEL = "Metamodel:";
	private static final String HEADER_TEXT = "Please set the relations between model smells and model refactorings.";
	private static final String NO_PLUGIN_NATURE_ERROR_MESSAGE = "The project you selected is not a plugin project!";
	IProject project;
	List<Table> tables;
	
	Combo refactoringCombo, smellCombo, metamodelCombo;
	private TabItem smellsTab;
	private TabItem refactoringsTab;
	private Table smellToRefactoringsTable;
	private Table refactoringToSmellsTable;
	private ProjectEntries entries;
	private ProjectEntries defaultEntries;
	private boolean isPluginProject;
	
	@Override
	protected Control createContents(Composite parent) {
		project = getActualProjectForPropertyPage(this);
		IProjectNature nature = null;
		try {
			nature = project.getNature("org.eclipse.pde.PluginNature");
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if(nature != null){
			this.isPluginProject = true;
			
		entries = PluginXMLManager.getRelationEntries(project);
		defaultEntries = PluginXMLManager.getRelationEntries(project);
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		layout.marginHeight = 20;
		layout.marginWidth = 10;
		layout.numColumns = 1;
		panel.setLayout(layout);
		createHeader(panel);
		createControls(panel, entries);
		return panel;
		}else{
			this.isPluginProject = false;
			Composite panel = new Composite(parent, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.verticalSpacing = 20;
			layout.horizontalSpacing = 10;
			layout.marginHeight = 20;
			layout.marginWidth = 10;
			layout.numColumns = 1;
			panel.setLayout(layout);
			Label message = new Label(panel, SWT.LEFT);
			message.setText(NO_PLUGIN_NATURE_ERROR_MESSAGE);
			return panel;
		}
	}

	/*
	 * Creates and adds the header of the property page.
	 */
	private void createHeader(Composite panel) {
		Text text = new Text(panel, SWT.LEFT);
		text.setBackground(panel.getBackground());
		text.setText(HEADER_TEXT);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		text.setLayoutData(gridData);
	}

	private void createControls(Composite parent, ProjectEntries entries) {
		Composite group = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label label = new Label(group, SWT.LEFT);
		label.setText(METAMODEL_LABEL);
		metamodelCombo = new Combo(group, SWT.LEFT | SWT.READ_ONLY);
		metamodelCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		for(String metamodelURI : EraseManager.getInstance().getMetamodelURIs()){
			metamodelCombo.add(metamodelURI);
		}
		metamodelCombo.addListener(SWT.Selection, new Listener(){

			@Override
			public void handleEvent(Event event) {
				String metamodelURI = metamodelCombo.getText();
				fillCombos(metamodelURI);
				saveTableContent();
				emptyTables();
				
				
			}

			
			
		});
		TabFolder tabs = new TabFolder(parent, SWT.NULL);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalAlignment = GridData.FILL;
		tabs.setLayoutData(gridData);
		
		refactoringsTab = new TabItem(tabs, SWT.NULL);
		refactoringsTab.setText(REFACTORINGS_TAB_LABEL);
		Composite refComposite = new Composite(tabs, SWT.NULL);
		refComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		refactoringsTab.setControl(refComposite);
		GridLayout refLayout = new GridLayout();
		refComposite.setLayout(refLayout);
		
		Composite refactoringComboComposite = new Composite(refComposite, SWT.NONE);
		GridLayout refactoringComboCompLayout = new GridLayout();
		refactoringComboComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		refactoringComboCompLayout.numColumns = 2;
		refactoringComboComposite.setLayout(refactoringComboCompLayout);
		Label refComboLabel = new Label(refactoringComboComposite, SWT.LEFT);
		refComboLabel.setText("Refactoring:");
		
		refactoringCombo = new Combo(refactoringComboComposite, SWT.LEFT | SWT.READ_ONLY);
		refactoringCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		smellsTab = new TabItem(tabs, SWT.NULL);
		smellsTab.setText(SMELLS_TAB_LABEL);
		Composite smellComposite = new Composite(tabs, SWT.NULL);
		smellComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		smellsTab.setControl(smellComposite);
		GridLayout smellLayout = new GridLayout();
		smellComposite.setLayout(smellLayout);
		
		Composite smellComboComposite = new Composite(smellComposite, SWT.NONE);
		GridLayout smellComboCompLayout = new GridLayout();
		smellComboComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		smellComboCompLayout.numColumns = 2;
		smellComboComposite.setLayout(smellComboCompLayout);
		Label smellComboLabel = new Label(smellComboComposite, SWT.LEFT);
//		smellComboLabel.setSize(refComboLabel.getSize());
		smellComboLabel.setText("Smell:");
		
		smellCombo = new Combo(smellComboComposite, SWT.LEFT | SWT.READ_ONLY);
		smellCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		smellToRefactoringsTable = new Table(smellComposite, SWT.CHECK | SWT.SINGLE
				| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL);
		smellToRefactoringsTable.setHeaderVisible(true);
		smellToRefactoringsTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		TableColumn column = new TableColumn(smellToRefactoringsTable, SWT.CENTER | SWT.READ_ONLY);
		column.setText(CHECK_COLUMN_LABEL);
		column.setWidth(60);
		column = new TableColumn(smellToRefactoringsTable, SWT.LEFT | SWT.READ_ONLY);
		column.setText(NAME_COLUMN_LABEL);
		column.setWidth(200);
		column = new TableColumn(smellToRefactoringsTable, SWT.LEFT | SWT.READ_ONLY);
		column.setText(DESCRIPTION_COLUMN_LABEL);
		column.setWidth(200);
		
		refactoringToSmellsTable = new Table(refComposite, SWT.CHECK | SWT.SINGLE
				| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL);
		refactoringToSmellsTable.setHeaderVisible(true);
		refactoringToSmellsTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		column = new TableColumn(refactoringToSmellsTable, SWT.CENTER | SWT.READ_ONLY);
		column.setText(CHECK_COLUMN_LABEL);
		column.setWidth(60);
		column = new TableColumn(refactoringToSmellsTable, SWT.LEFT | SWT.READ_ONLY);
		column.setText(NAME_COLUMN_LABEL);
		column.setWidth(200);
		column = new TableColumn(refactoringToSmellsTable, SWT.LEFT | SWT.READ_ONLY);
		column.setText(DESCRIPTION_COLUMN_LABEL);
		column.setWidth(200);
		
	}

	/*
	 * Fills the ComboBoxes containing the metamodel URIs, the model refactorings and model smells.
	 */
	protected void fillCombos(String metamodelURI) {
		smellCombo.removeAll();
		Set<ModelSmellStub> smellStubs = EraseManager.getInstance().getAllSmellStubsForMetamodel(metamodelURI);
		int i = 0;
		for(ModelSmellStub smellStub : SetSorter.sortSmellStubSet(smellStubs)){ //SetSorter.sortSmellSet(EraseManager.getAllInstalledSmellsForMetamodel(metamodelURI))){
			smellCombo.setData("" + i, smellStub);
			smellCombo.add(smellStub.getName(), i);// + "(" + smellStub.getId() + ")", i);
			i++;
		}
		smellCombo.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				saveTableContent();
				fillTables();
			}
		});
		
		refactoringCombo.removeAll();
		Set<ModelRefactoringStub> refactoringStubs = EraseManager.getInstance().getAllRefactoringStubsForMetamodel(metamodelURI);
		i = 0;
		for(ModelRefactoringStub refactoringStub : SetSorter.sortRefactoringStubSet(refactoringStubs)){
			refactoringCombo.setData("" + i, refactoringStub);
			refactoringCombo.add(refactoringStub.getName(), i);// + "(" + refactoringStub.getId() + ")", i);
			i++;
		}
		refactoringCombo.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				saveTableContent();
				fillTables();
				
			}
		});
		
		
	}

	/*
	 * Gets the actual object representing the target project.
	 */
	private static IProject getActualProjectForPropertyPage(PropertyPage page) {
		IProject project;
		if (page.getElement() instanceof IJavaProject) {
			project = ((IJavaProject) page.getElement()).getProject();
		} else {
			project = ((IProject) page.getElement());
		}
		return project;
	}
	
	/*
	 * Clears all the data from the tables.
	 */
	protected void emptyTables() {
		refactoringToSmellsTable.removeAll();
		smellToRefactoringsTable.removeAll();
		
	}

	/*
	 * Saves the table content in the ProjectEntries stored in the field entries.
	 */
	protected void saveTableContent() {
		if(refactoringToSmellsTable.getItems().length > 0){
			ModelRefactoringStub refactoringStub = (ModelRefactoringStub) refactoringToSmellsTable.getData();
			HashSet<ModelSmellStub> smellStubs = new HashSet<ModelSmellStub>();
			for(TableItem item : refactoringToSmellsTable.getItems()){
				ModelSmellStub smellStub = (ModelSmellStub) item.getData();
				if(item.getChecked())
					smellStubs.add(smellStub);
			}
			entries.setCausedSmells(refactoringStub, smellStubs);
		}
		if(smellToRefactoringsTable.getItems().length > 0){
			ModelSmellStub smellStub = (ModelSmellStub)smellToRefactoringsTable.getData();
			HashSet<ModelRefactoringStub> refactorings = new HashSet<ModelRefactoringStub>();
			for(TableItem item : smellToRefactoringsTable.getItems()){
				ModelRefactoringStub refactoringStub = (ModelRefactoringStub)item.getData();
				if(item.getChecked()){
					refactorings.add(refactoringStub);
				}
			}
			entries.setFixingRefactorings(smellStub, refactorings);
		}
	}

	/*
	 * Fills the tables for both tabs.
	 */
	protected void fillTables() {
		emptyTables();
		try{
			ModelRefactoringStub refactoringStub = (ModelRefactoringStub) refactoringCombo.getData("" + refactoringCombo.getSelectionIndex());
			fillRefactoringTable(refactoringStub);
		}catch(Exception ex){
//			ex.printStackTrace();
		}
		try{
			ModelSmellStub smellStub = (ModelSmellStub) smellCombo.getData("" + smellCombo.getSelectionIndex());
			fillSmellTable(smellStub);
		}catch(Exception ex){
//			ex.printStackTrace();
		}
		
	}

	/*
	 * Reads the information on relations between model smells and their fixing refactorings from the field
	 * entries and uses it to fill the corresponding table.
	 */
	protected void fillSmellTable(ModelSmellStub smellStub) {
		smellToRefactoringsTable.setData(smellStub);
		for(ModelRefactoringStub refactoringStub : SetSorter.sortRefactoringStubSet(EraseManager.getInstance().getAllRefactoringStubsForMetamodel(smellStub.getMetamodel()))){
			TableItem item = new TableItem(smellToRefactoringsTable, SWT.NONE);
			boolean checked = entries.getFixingRefactorings(smellStub) != null && entries.getFixingRefactorings(smellStub).contains(refactoringStub);
			item.setData(refactoringStub);
			item.setText(1, refactoringStub.getName());// + "(" + refactoringStub.getId() + ")");
			item.setText(2, refactoringStub.getId());
			item.setChecked(checked);
		}
		
	}

	/*
	 * Reads the information on relations between model refactorings and their caused smells from the field
	 * entries and uses it to fill the corresponding table.
	 */
	protected void fillRefactoringTable(ModelRefactoringStub refactoringStub) {
		refactoringToSmellsTable.setData(refactoringStub);
		for(ModelSmellStub smellStub : SetSorter.sortSmellStubSet(EraseManager.getInstance().getAllSmellStubsForMetamodel(refactoringStub.getMetamodel()))){
			TableItem item = new TableItem(refactoringToSmellsTable, SWT.NONE);
			item.setData(smellStub);
			boolean checked = entries.getCausedSmells(refactoringStub) != null && entries.getCausedSmells(refactoringStub).contains(smellStub);
			item.setText(1, smellStub.getName());// + "(" + smellStub.getId() + ")");
			item.setText(2, smellStub.getDescription());
			item.setChecked(checked);
		}
		
	}


	@Override
	protected void performApply() {
		performOk();
	}


	@Override
	protected void performDefaults() {
		if(!isPluginProject)
			return;
		emptyTables();
		entries.clear();
		for(ModelRefactoringStub refactoringStub : defaultEntries.getAllRefactoringsInCausingRelations()){
			entries.setCausedSmells(refactoringStub, defaultEntries.getCausedSmells(refactoringStub));
		}
		for(ModelSmellStub smellStub : defaultEntries.getAllSmellsInFixingRelations()){
			entries.setFixingRefactorings(smellStub, defaultEntries.getFixingRefactorings(smellStub));
		}
		fillTables();
	}


	@Override
	public boolean performOk() {
		if(!isPluginProject)
			return false;
		boolean saveDanglingEntries = false;
		boolean danglingRelationsPresent = 
			!(entries.getDanglingCausedSmells().isEmpty() 
					&& entries.getDanglingFixingRefactorings().isEmpty());
		if(danglingRelationsPresent){
			String message = "There are entries for IDs without reference to model smells or\n" +
					"model refactorings from installed plugins or in workspace:\n" +
					"Do you wish to keep those entries?";
			saveDanglingEntries = MessageDialog.openQuestion(this.getShell(), "Dangling IDs found", message);
		}
		saveTableContent();
		NullProgressMonitor monitor = new NullProgressMonitor();
		EraseManager.getInstance().saveRelations(monitor, project, entries, saveDanglingEntries);
		return super.performOk();
	}

}
