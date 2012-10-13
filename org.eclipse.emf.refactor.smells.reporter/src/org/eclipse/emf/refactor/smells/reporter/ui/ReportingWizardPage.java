package org.eclipse.emf.refactor.smells.reporter.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.refactor.smells.managers.FileManager;
import org.eclipse.emf.refactor.smells.reporter.managers.ReportManager;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ReportingWizardPage extends WizardPage {
	
	public Button btnHtml, btnPdf, btnPostcript, btnDoc, btnPpt, btnXls, btnOdp, btnOds, btnOdt;
	public Combo combo;
	private Button btnImport;
	public Text text;
	private Button btnBrowse;
	
	private ReportingWizard reportingWizard;
	private List<String> designs = new ArrayList<String>();

	/**
	 * Create the wizard.
	 * @param reportingWizard 
	 */
	public ReportingWizardPage(ReportingWizard reportingWizard) {
		super("DesignWizardPage");
		this.reportingWizard = reportingWizard;
		setTitle("EMF Quality Assurance - Smell Reporting -");
		setDescription("Please insert the required informations for reporting your smell detection results.");
	}
	
	private void checkPageComplete() {
		if (combo.getSelectionIndex() == -1 || text.getText().isEmpty()) setPageComplete(false);
		else setPageComplete(true);
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(1, true));
		
		Composite composite = new Composite(container, SWT.BORDER);
		GridData gd_composite = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite.widthHint = 670;
		composite.setLayoutData(gd_composite);
		composite.setLayout(new GridLayout(1, false));
		
		Label lblPleaseSelectThe = new Label(composite, SWT.NONE);
		lblPleaseSelectThe.setText("Please select the output format(s) of your smell detection report.");
		
		Composite composite_3 = new Composite(composite, SWT.NONE);
		GridData gd_composite_3 = new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1);
		gd_composite_3.widthHint = 655;
		composite_3.setLayoutData(gd_composite_3);
		composite_3.setLayout(new GridLayout(5, false));
		
		Button btnXmldefault = new Button(composite_3, SWT.CHECK);
		btnXmldefault.setSelection(true);
		btnXmldefault.setEnabled(false);
		btnXmldefault.setText("XML (default)  ");
		
		btnHtml = new Button(composite_3, SWT.CHECK);
		btnHtml.setText("HTML    ");
		
		btnPdf = new Button(composite_3, SWT.CHECK);
		btnPdf.setText("PDF     ");
		
		btnPostcript = new Button(composite_3, SWT.CHECK);
		btnPostcript.setText("PS      ");
		
		btnDoc = new Button(composite_3, SWT.CHECK);
		btnDoc.setText("DOC     ");
		
		btnPpt = new Button(composite_3, SWT.CHECK);
		btnPpt.setText("PPT");
		
		btnXls = new Button(composite_3, SWT.CHECK);
		btnXls.setText("XLS");
		
		btnOdp = new Button(composite_3, SWT.CHECK);
		btnOdp.setText("ODP");	
		btnOdp.setEnabled(true);
		
		btnOds = new Button(composite_3, SWT.CHECK);
		btnOds.setText("ODS");	
		btnOds.setEnabled(true);
		
		btnOdt = new Button(composite_3, SWT.CHECK);
		btnOdt.setText("ODT");	
		btnOdt.setEnabled(true);
		
		Composite composite_1 = new Composite(container, SWT.BORDER);
		GridData gd_composite_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_1.widthHint = 670;
		composite_1.setLayoutData(gd_composite_1);
		composite_1.setLayout(new GridLayout(1, false));
		
		Label lblPleaseSelectEither = new Label(composite_1, SWT.NONE);
		lblPleaseSelectEither.setText("Please select either a predefined report design from the list or import your custom design from");
		
		Label lblYourCustomDesign = new Label(composite_1, SWT.NONE);
		lblYourCustomDesign.setText("the file system.");
		
		Composite composite_4 = new Composite(composite_1, SWT.NONE);
		GridData gd_composite_4 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_4.widthHint = 650;
		composite_4.setLayoutData(gd_composite_4);
		composite_4.setLayout(new GridLayout(2, false));
		
		combo = new Combo(composite_4, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnImport = new Button(composite_4, SWT.NONE);
		GridData gd_btnImport = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnImport.widthHint = 70;
		btnImport.setLayoutData(gd_btnImport);
		btnImport.setText("Import");
		btnImport.addSelectionListener(new DesignListenerAdapter());
		
		Composite composite_2 = new Composite(container, SWT.BORDER);
		GridData gd_composite_2 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_2.widthHint = 670;
		composite_2.setLayoutData(gd_composite_2);
		composite_2.setLayout(new GridLayout(1, false));
		
		Label lblPleaseChoose = new Label(composite_2, SWT.NONE);
		lblPleaseChoose.setText("Please  choose location and name of your smell detection report.");
		
		Composite composite_5 = new Composite(composite_2, SWT.NONE);
		GridData gd_composite_5 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_composite_5.widthHint = 650;
		composite_5.setLayoutData(gd_composite_5);
		composite_5.setLayout(new GridLayout(2, false));
		
		text = new Text(composite_5, SWT.BORDER);
		text.setEditable(false);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		btnBrowse = new Button(composite_5, SWT.NONE);
		GridData gd_btnBrowse = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnBrowse.widthHint = 70;
		btnBrowse.setLayoutData(gd_btnBrowse);
		btnBrowse.setText("Browse");
		btnBrowse.addSelectionListener(new OutputNameListenerAdapter());
		
		setCombo();
		checkPageComplete();
	}

	private void setCombo() {
		combo.removeAll();
		designs = reportingWizard.getSmellReporter().getDesigns();
		for (String des : designs) {
			combo.add(des);
		}
		combo.addSelectionListener(new ComboListenerAdapter());
	}
	
	public void setDestination(String str) {
		text.setText(str);
	}
	
	public String getDestination() {
		return text.getText();
	}
	
	public String getDesign() {
		return combo.getItem(combo.getSelectionIndex());
	}
	
	private class ComboListenerAdapter extends SelectionAdapter {
		
		public void widgetSelected(SelectionEvent e) {
			checkPageComplete();
		}
	}
	
	private class OutputNameListenerAdapter extends SelectionAdapter {
		
		public void widgetSelected(SelectionEvent e) {
			String result = browseForOutputName();
			if (result != null && ! result.isEmpty()) {
				result = result.replace('\\', '/');
				setDestination(result);
			} else {
				setDestination("");
			}
			checkPageComplete();
		}
		
		private String browseForOutputName() {
			System.out.println("Browse for Output (1) a");
			FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
			System.out.println("Browse for Output (1) b");
			fileDialog.setText("Please select the location and the name of the output file(s).");
			System.out.println("Browse for Output (1) c");
			if (reportingWizard.getSmellReporter().getProject() != null) {
				System.out.println("Browse for Output (1) d");
				System.out.println("reportingWizard.getSmellReporter().getProject(): " + reportingWizard.getSmellReporter().getProject());
				System.out.println("reportingWizard.getSmellReporter().getProject().getLocation(): " + reportingWizard.getSmellReporter().getProject().getLocation());
				fileDialog.setFilterPath(reportingWizard.getSmellReporter().getProject().getLocation().toString());
			}
			System.out.println("Browse for Output (1) e");
			String[] filterExt = { };
			fileDialog.setFilterExtensions(filterExt);
			System.out.println("Browse for Output (2)");
			String selected = fileDialog.open();
			System.out.println("Browse for Output (3)");
			if (! isValidName(selected)) selected = browseForOutputName();
			System.out.println("Browse for Output (4)");
			return selected;
		}

		private boolean isValidName(String selected) {
			boolean valid = false;
			if (selected == null) {
				valid = true; // the file dialog was cancelled
			}  else {
				if (! selected.isEmpty()) {
					selected = selected.replace('\\', '/');
					int i = selected.lastIndexOf("/");
					String name = selected.substring(i + 1);
					if (name.contains(".")) {
						String message = "Please insert an output name without file extension!";
						MessageDialog.openError(getShell(), "EMF Quality Assurance - Smell Reporting -", message);
						valid = false;
					} else {
						valid = true;
					}
				} else {
					valid = true;
				}				
			}
			return valid;
		}
	}
	
	private class DesignListenerAdapter extends SelectionAdapter {
		
		public void widgetSelected(SelectionEvent e) {
			String result = browseForDesign();
			if (result != null && ! result.isEmpty()) {
				result = result.replace('\\', '/');
				int i = result.lastIndexOf("/");
				String name = result.substring(i + 1);
				File targetFile = new File(result);
				if (targetFile.exists()) {
					String destination = 
							reportingWizard.getSmellReporter().getProject().getLocationURI().getPath()
							+ ReportManager.DESIGNS_DIR + "/";
					FileManager.copyFile(result, destination, name);
					reportingWizard.getSmellReporter().addExternalDesign(name);
					setCombo();
				}
			}
			checkPageComplete();
		}
		
		private String browseForDesign() {
			FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
			fileDialog.setText("Select Smell Report File");
			if (reportingWizard.getSmellReporter().getProject() != null) {
				fileDialog.setFilterPath(reportingWizard.getSmellReporter().getProject().getLocation().toString());
			}
			String[] filterExt = { "*" + ReportManager.DESIGNS_FILE_EXTENSION  };
			fileDialog.setFilterExtensions(filterExt);
			String selected = fileDialog.open();
			return selected;
		}
	}
}
