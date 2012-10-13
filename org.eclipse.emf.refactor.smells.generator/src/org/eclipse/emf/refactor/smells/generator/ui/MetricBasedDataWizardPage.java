package org.eclipse.emf.refactor.smells.generator.ui;


import java.util.LinkedList;

import org.eclipse.emf.refactor.smells.generator.core.MetricData;
import org.eclipse.emf.refactor.smells.generator.core.MetricStubLoader;
import org.eclipse.emf.refactor.smells.generator.interfaces.INewModelSmellWizard;
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
import org.eclipse.swt.widgets.Shell;

/**
 * The wizard page for specific data concerning the generation of smell plugins based on EMF Metrics
 * 
 * @author Matthias Burhenne
 *
 */

public class MetricBasedDataWizardPage extends WizardPage implements Listener {

	private static final String PAGE_NAME = "org.eclipse.emf.refactor.smells.MetricBasedDataWizardPage";
	private static final String TITLE = "New Model Smell: Metric Data";
	private static final String DESCRIPTION = "Please select the corresponding metric. \n" +
			"Required fields are denoted by \"(*)\".";
	private Combo metricDataCombo, comparatorCombo;
	private String pre, transf, post, comparator;
	private boolean initializing = false;
	
	private MetricData metricData;

	@Override
	public void createControl(Composite parent) {
		final Shell shell = parent.getShell();
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		container.setLayout(layout);
		setControl(container);
		createTextFields(container, shell);
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

	public MetricBasedDataWizardPage() {
		super(PAGE_NAME);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}

	private void createTextFields(Composite container, Shell shell) {
		Label label;
		GridData gridData;
		Group group;
		GridLayout layout;
		//Metric ID Combo Box and Label
		group = new Group(container, SWT.NONE);
		group.setText("Metric Data");
		layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayout(layout);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gridData);
		label = new Label(group, SWT.NONE);
		label.setText("Metric (*):");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		metricDataCombo = new Combo(group, SWT.READ_ONLY);
		metricDataCombo.addListener(SWT.Modify, this);
		metricDataCombo
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//Comparator Combo Box and Label
		label = new Label(group, SWT.NONE);
		label.setText("Comparator (*):");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		comparatorCombo = new Combo(group, SWT.READ_ONLY);
		comparatorCombo.addListener(SWT.Modify, this);
		comparatorCombo
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label = new Label(group, SWT.NONE);
		label.setText("");
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		label = new Label(group, SWT.NONE);
		label.setText("Semantic: The comparator determines how the local value is \ncompared to the limit set in the configuration (larger, smaller, equal, ...). \nFor example if you select \">\" the local value of the metric has to be \nlarger than the limit to indicate a smell.");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		
	}

	public void initContents() {
		setCombos(MetricStubLoader.loadModelMetricData());
	}

	private void setCombos(LinkedList<MetricData> metricDatas) {
		initializing = true;
		metricDataCombo.removeAll();
		comparatorCombo.removeAll();
		String metamodel = ((INewModelSmellWizard) this.getWizard()).getNewSmellMetamodel();
		for (MetricData metricData : metricDatas) {
			if(metamodel.equals(metricData.getMetamodel())){
				String key = metricData.getMetricName() + " (" + metricData.getMetricDescription() +")";
				metricDataCombo.setData(key, metricData);
				metricDataCombo.add(key);
			}
		}
		comparatorCombo.add(">");
		comparatorCombo.add(">=");
		comparatorCombo.add("<");
		comparatorCombo.add("<=");
		comparatorCombo.add("=");
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
			if (!checkMetricDataCombo()) {
				setMessage(
						"No metric selected.",
						ERROR);
				this.setPageComplete(false);
				getWizard().getContainer().updateButtons();
			} else if (!checkComparatorCombo()){
				setMessage(
						"No comparator selected.",
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

	private boolean checkMetricDataCombo() {
		final String metricString = this.metricDataCombo.getText();
		if (!metricString.isEmpty())
			return true;
		return false;
	}
	
	private boolean checkComparatorCombo() {
		final String comparatorString = this.comparatorCombo.getText();
		if (!comparatorString.isEmpty())
			return true;
		return false;
	}

	private void saveContents() {
		this.transf = this.metricDataCombo.getText();
		this.comparator = this.comparatorCombo.getText();
		this.metricData = (MetricData) this.metricDataCombo.getData(this.transf);
	}
	
	public String getComparator(){
		return comparator;
	}

	public String getPre() {
		return pre;
	}

	public String getTransf() {
		return transf;
	}

	public String getPost() {
		return post;
	}
	
	public MetricData getMetricData(){
		return metricData;
	}
}
