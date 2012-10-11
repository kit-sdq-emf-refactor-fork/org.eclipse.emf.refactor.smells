package org.eclipse.emf.refactor.smells.configuration.ui;

import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.refactor.smells.configuration.core.SortedModelSmellList;
import org.eclipse.emf.refactor.smells.configuration.managers.ConfigurationManager;
import org.eclipse.emf.refactor.smells.core.MetricBasedModelSmellFinderClass;
import org.eclipse.emf.refactor.smells.core.ModelSmell;
import org.eclipse.emf.refactor.smells.managers.ModelSmellManager;
import org.eclipse.emf.refactor.smells.managers.ProjectManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * The property page for the (de)activation of smells on a given project.
 * 
 * @author Matthias Burhenne
 *
 */

public class ModelSmellPropertyPage extends PropertyPage {
	
	private IProject project;
	private TableItemsMapping mapping;
	private SortedModelSmellList modelSmellList;
	private Table[] smellTables;

	@Override
	protected Control createContents(Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		layout.marginHeight = 20;
		layout.marginWidth = 10;
		layout.numColumns = 1;
		panel.setLayout(layout);
		modelSmellList = new SortedModelSmellList(ModelSmellManager.getAllModelSmells());
		mapping = new TableItemsMapping(ModelSmellManager.getAllModelSmells());
		createHeader(panel);
		createMetricTables(panel);
		project = ProjectManager.getActualProjectForPropertyPage(this);
		fillTables();
		loadConfiguration();
		return panel;
	}

	@Override
	public boolean performOk() {
		try{
			saveConfiguration();
			ConfigurationManager.saveConfiguration(project);
			return true;
		}catch(NumberFormatException ex){
			MessageDialog.openError(
					getShell(),
					"Error when trying to save configuration!",
					"In the limit column only correctly fomated floating point numbers are allowed.");
			return false;
		}
	}

	@Override
	protected void performApply() {
		performOk();
	}

	@Override
	protected void performDefaults() {
		for (int index = 0; index < mapping.getSize(); index++) {
			mapping.getTableItem(index).setChecked(false);
		}
	}

	private void createHeader(Composite panel) {
		Text text = new Text(panel, SWT.LEFT);
		text.setBackground(panel.getBackground());
		text.setText("Please select EMF smells from the list below to be supported by the project.");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		text.setLayoutData(gridData);
	}

	private void createMetricTables(Composite panel) {
		TabFolder tabs = new TabFolder(panel, SWT.NULL);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalAlignment = GridData.FILL;
		tabs.setLayoutData(gridData);
		smellTables = new Table[modelSmellList.getModelSmellList().size()];
		for (int index = 0; index < modelSmellList.getModelSmellList().size(); index++) {
			TabItem tabItem = new TabItem(tabs, SWT.LEFT);
			tabItem.setText(""+ modelSmellList.getModelSmellList().get(index).get(0)
							.getMetamodel());
			Composite composite = new Composite(tabs, SWT.NULL);
			tabItem.setControl(composite);
			GridLayout layout = new GridLayout();
			composite.setLayout(layout);
			smellTables[index] = new Table(composite, SWT.CHECK | SWT.SINGLE
					| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL);
			smellTables[index].setHeaderVisible(true);
			gridData = new GridData(GridData.FILL_BOTH);
			smellTables[index].setLayoutData(gridData);
			TableColumn col;
			col = new TableColumn(smellTables[index], SWT.CENTER
					| SWT.READ_ONLY);
			col.setText("Selected");
			col.setWidth(60);
			col = new TableColumn(smellTables[index], SWT.LEFT
					| SWT.READ_ONLY);
			col.setText("Name");
			col.setWidth(100);
			col = new TableColumn(smellTables[index], SWT.LEFT
					| SWT.READ_ONLY);
			col.setText("Description");
			col.setWidth(200);
			col = new TableColumn(smellTables[index], SWT.NONE);
			col.setText("Limit");
			col.setWidth(100);
			
			final Table currentTable = smellTables[index]; 
			
			final TableEditor tableEditor = new TableEditor(currentTable);
			// The editor must have the same size as the cell and must
			// not be any smaller than 50 pixels.
			tableEditor.horizontalAlignment = SWT.LEFT;
			tableEditor.grabHorizontal = true;
			tableEditor.minimumWidth = 100;
			// editing the fourth column
			final int EDITABLECOLUMN = 3;

			smellTables[index].addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					// Clean up any previous editor control
					Control oldEditor = tableEditor.getEditor();
			   	 	if (oldEditor != null)
			   	 		oldEditor.dispose();

			   	 // Identify the selected row
			   	 	TableItem item = (TableItem) e.item;
			   	 	if (item == null)
			   	 		return;

			   	 	// The control that will be the editor must be a child of the
			        // Table
			        Text newEditor = new Text(currentTable, SWT.NONE);
			        newEditor.setText(item.getText(EDITABLECOLUMN));
			        newEditor.addModifyListener(new ModifyListener() {
			        	public void modifyText(ModifyEvent me) {
			        		Text text = (Text) tableEditor.getEditor();
			        		tableEditor.getItem()
			                	.setText(EDITABLECOLUMN, text.getText());
			        	}
			        });
			        newEditor.selectAll();
			        newEditor.setFocus();
			        tableEditor.setEditor(newEditor, item, EDITABLECOLUMN);
			      }
			    });		
		
		}
		
		
		
		
		
		
	}

	private void fillTables() {
		for (int index = 0; index < modelSmellList.getSize(); index++) {
			LinkedList<ModelSmell> smells = modelSmellList.get(index);
			for (ModelSmell smell : smells) {
				TableItem item = new TableItem(smellTables[index], SWT.NONE);
				item.setText(1, smell.getName());
				item.setText(2, smell.getDescription());
				mapping.setItemForSmell(smell, item);
			}
		}
	}

	private void loadConfiguration() {	
		LinkedList<ModelSmell> selectedSmells = ConfigurationManager.getSelectedModelSmells(project);
		if(selectedSmells==null || selectedSmells.isEmpty()){
			for(int i=0; i<mapping.getSize(); i++)
				mapping.getTableItem(i).setChecked(false);
		}else{
			for (ModelSmell smell : selectedSmells){
				mapping.getTableItem(smell).setChecked(true);
			}
		}
		
		for(int i=0; i<mapping.getSize(); i++){
			ModelSmell smell = mapping.getModelSmell(i);
			if(smell.getFinderClass() instanceof MetricBasedModelSmellFinderClass){
				System.out.println("===>>> property page: " + smell.getId() + "; " +
						ConfigurationManager.getLimit(project, smell.getId()).toString());
				if (selectedSmells.contains(smell)) {
					mapping.getTableItem(i).setText(3, ConfigurationManager.getLimit(project, smell.getId()).toString());
				} else {
					mapping.getTableItem(i).setText(3, "1.0");
				}
			}
		}
		

	}

	private void saveConfiguration() throws NumberFormatException{
		boolean[] selection = new boolean[mapping.getSize()];
		for (int index = 0; index < mapping.getSize(); index++) {
			selection[index] = mapping.getTableItem(index).getChecked();
		}
		try{
			double[] limits = getLimits();
			ConfigurationManager.setConfiguration(project, selection, limits);
		}catch(NumberFormatException ex){
			throw ex;
		}
	}
	
	private double[] getLimits() throws NumberFormatException{
		double[] limits = new double[mapping.getSize()];
		for (int index = 0; index < mapping.getSize(); index++) {
			String limit = mapping.getTableItem(index).getText(3);
			if(!limit.equals(""))
				limits[index] = Double.parseDouble(limit);
			else
				limits[index] = 0.0;
		}
		return limits;
	}
}