package org.eclipse.emf.refactor.smells.reporter.ui;

import org.eclipse.emf.refactor.smells.reporter.managers.ReportManager;
import org.eclipse.jface.wizard.Wizard;

public class ReportingWizard extends Wizard {
	
	private ReportManager reportManager;
	private ReportingWizardPage page;

	public ReportingWizard(ReportManager reportManager) {
		setWindowTitle("EMF Quality Assurance - Smell Reporting -");
		this.reportManager = reportManager;
	}
	
	public ReportManager getSmellReporter() {
		return reportManager;
	}

	@Override
	public void addPages() {
		page = new ReportingWizardPage(this);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		reportManager.setDoc(page.btnDoc.getSelection());
		reportManager.setHtml(page.btnHtml.getSelection());
		reportManager.setOdp(page.btnOdp.getSelection());
		reportManager.setOds(page.btnOds.getSelection());
		reportManager.setOdt(page.btnOdt.getSelection());
		reportManager.setPdf(page.btnPdf.getSelection());
		reportManager.setPostcript(page.btnPostcript.getSelection());
		reportManager.setPpt(page.btnPpt.getSelection());
		reportManager.setXls(page.btnXls.getSelection());
		reportManager.setDesignName(page.getDesign());
		reportManager.setFileName(page.getDestination());		
		reportManager.doReport();
		return true;
	}
	
	@Override
	public boolean canFinish() {
		return page.isPageComplete();
	}

}
