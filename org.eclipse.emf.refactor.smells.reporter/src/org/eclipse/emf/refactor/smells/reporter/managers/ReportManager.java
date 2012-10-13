package org.eclipse.emf.refactor.smells.reporter.managers;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EXCELRenderOption;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IGetParameterDefinitionTask;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.refactor.smells.reporter.Activator;
import org.eclipse.emf.refactor.smells.runtime.core.ResultModel;
import org.eclipse.emf.refactor.smells.runtime.managers.XMLResultsManager;
import org.eclipse.emf.refactor.smells.runtime.ui.ResultModelTreeView;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

public class ReportManager {

	private List<String> internalDesigns = new ArrayList<String>();
	private List<String> externalDesigns = new ArrayList<String>();
	private List<String> designs = new ArrayList<String>();
	private String fileName = "";
	private String designName = "";
	private Resource resource;
	private Shell shell;
	private IProject project;
	private boolean html, pdf, postscript, doc, ppt, xls, odp, ods, odt;
	
	public static final  String DESIGNS_FILE_EXTENSION = ".rptdesign";
	public static final  String DESIGNS_DIR = "/designs";
	private static final  String DESIGNS = "designs";
	private static String designsDirectory = "";
	
	private static final String LOG_DIR =  "/log";
	private static final String DESIGN_PARAMETER = "XmlUrl"; 
	private static final String XML_FILE_EXTENSION  = ".xml";
	private static final String HTML_FILE_EXTENSION = ".html";
	private static final String PDF_FILE_EXTENSION = ".pdf";
	private static final String POSTSCRIPT_FILE_EXTENSION = ".ps";
	private static final String DOC_FILE_EXTENSION = ".doc";
	private static final String EXCEL_FILE_EXTENSION = ".xls";
	private static final String PPT_FILE_EXTENSION = ".ppt";
	private static final String ODP_FILE_EXTENSION = ".odp";
	private static final String ODS_FILE_EXTENSION = ".ods";
	private static final String ODT_FILE_EXTENSION = ".odt";
	
	private LinkedList<ResultModel> resultModels;
	
	public ReportManager(Shell shell, LinkedList<ResultModel> resultModels) {
		this.shell = shell;
		this.resultModels = resultModels;
		this.resource = getResourceFromResults(resultModels);
		System.out.println("resuorce set");
		setDesignsDirectory();
		System.out.println("designs directory set");
		loadInternalDesigns();
		System.out.println("internal designs loaded");
		addExternalDesigns();
		System.out.println("external designs added");
	}	
	
	private Resource getResourceFromResults(List<ResultModel> resultModels) {
		if (resultModels.isEmpty()) {
			return null;
		} else {
			System.out.println("ResultModel: " + resultModels.get(0));
			System.out.println("ModelSmellResult: " + resultModels.get(0).getModelSmellResults());
			System.out.println("EObjectGroup: " + resultModels.get(0).getModelSmellResults().get(0).getEObjectGroups().get(0));
			System.out.println("EObject: " + resultModels.get(0).getModelSmellResults().get(0).getEObjectGroups().get(0).getEObjects().get(0));
			System.out.println("eRessource: " + resultModels.get(0).getModelSmellResults().get(0).getEObjectGroups().get(0).getEObjects().get(0).eResource());
			return resultModels.get(0).getModelSmellResults().get(0).getEObjectGroups().get(0).getEObjects().get(0).eResource();
		}
	}
	
	public IProject getProject() {
		return project;
	}

	public List<String> getDesigns() {
		return designs;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setDesignName(String designName) {
		this.designName = designName;
	}

	private void addExternalDesigns() {
		if (resource != null) {
			System.out.println("SmellReporter::addExternalDesigns() 1");
			String projectName = resource.getURI().segment(0);	
			if (projectName.equals("resource")) {
				projectName = resource.getURI().segment(1);
			}
			System.out.println("SmellReporter::addExternalDesigns() 2");
			IWorkspace workspace = ResourcesPlugin.getWorkspace();		
			System.out.println("SmellReporter::addExternalDesigns() 3");
			project = workspace.getRoot().getProject(projectName);
			System.out.println("projectName: " + projectName);
			System.out.println("project: " + project);
			System.out.println("SmellReporter::addExternalDesigns() 4");
			IFolder folder = project.getFolder(DESIGNS);
			System.out.println("SmellReporter::addExternalDesigns() 5");
			if (folder.exists()) {
				System.out.println("SmellReporter::addExternalDesigns() 6");
				File dir = new File(folder.getLocationURI());
				System.out.println("SmellReporter::addExternalDesigns() 7");
				File[] fileArray = dir.listFiles();
				System.out.println("SmellReporter::addExternalDesigns() 8");
				if (fileArray != null) { 
					for (int i = 0; i < fileArray.length; i++) {
						String fileName = fileArray[i].getName();
						if (fileName.endsWith(DESIGNS_FILE_EXTENSION)) {
							externalDesigns.add(fileName);
						}
					}
				}
			}
			synchronizeDesigns();
		}
	}

	private void synchronizeDesigns() {
		String error = "";
		String lineSeparator = System.getProperty("line.separator");
		for (String str : externalDesigns) {
			if (designs.contains(str)) {
				error += lineSeparator + str;
			} else {
				designs.add(str);
			}
		}
		if (! error.isEmpty()) {
			String message = "The following designs (in your project) can not be added since they already exist:" + error;
			MessageDialog.openInformation(shell, "EMF Quality Assurance - Smell Reporting -", message);
		}
	}

	private void setDesignsDirectory() {
		final Bundle bundle = Activator.getDefault().getBundle();
		try {
			designsDirectory = FileLocator.toFileURL(bundle.getEntry(DESIGNS_DIR)).getFile();
		} catch (final IOException e) {
			System.out.println("error: " + e.getCause());
			e.printStackTrace();
		}
	}

	private void loadInternalDesigns() {
		File dir = new File(designsDirectory);
		File[] fileArray = dir.listFiles();
		if (fileArray != null) { 
			for (int i = 0; i < fileArray.length; i++) {
				String fileName = fileArray[i].getName();
				if (fileName.endsWith(DESIGNS_FILE_EXTENSION)) {
					this.internalDesigns.add(fileName);
					this.designs.add(fileName);
				}
			}
		}
	}

	public void addExternalDesign(String des) {
		if (! externalDesigns.contains(des)) {
			externalDesigns.add(des);
			designs.add(des);
		}
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	public void setPdf(boolean pdf) {
		this.pdf = pdf;
	}

	public void setPostcript(boolean postcript) {
		this.postscript = postcript;
	}

	public void setDoc(boolean doc) {
		this.doc = doc;
	}

	public void setPpt(boolean ppt) {
		this.ppt = ppt;
	}

	public void setXls(boolean xls) {
		this.xls = xls;
	}

	public void setOdp(boolean odp) {
		this.odp = odp;
	}

	public void setOds(boolean ods) {
		this.ods = ods;
	}

	public void setOdt(boolean odt) {
		this.odt = odt;
	}

	@SuppressWarnings("unchecked")
	public void doReport() {
		// start
		Cursor oldCursor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getCursor();
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		fileName += "_" +  dateFormat.format(date);
		// create xml file
		String xmlFileName = fileName + XML_FILE_EXTENSION;
		XMLResultsManager.saveResults(xmlFileName, resultModels, shell, false);
		// create BIRT engine
		EngineConfig config = new EngineConfig();
		String logDirectory = getDirectory(fileName) + LOG_DIR;
		config.setLogConfig(logDirectory, Level.FINE);
		IReportEngineFactory factory = (IReportEngineFactory) Platform
				.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
		IReportEngine engine = factory.createReportEngine(config);
		engine.changeLogLevel(Level.WARNING);
		// load BIRT report design
		IReportRunnable design;
		String designFileName = getDesignFileName();
		try {
			design = engine.openReportDesign(designFileName);
			IRunAndRenderTask runAndRenderTask = engine.createRunAndRenderTask(design);
			runAndRenderTask.getAppContext().put(
					EngineConstants.APPCONTEXT_CLASSLOADER_KEY, 
					ResultModelTreeView.class.getClassLoader());
			// Parameter passing
			IGetParameterDefinitionTask paramTask = engine.createGetParameterDefinitionTask(design);
			paramTask.setValue(DESIGN_PARAMETER, xmlFileName);
			HashMap<String, Object> parameterValues = paramTask.getParameterValues( );
			runAndRenderTask.setParameterValues(parameterValues);
			paramTask.close();
			if (runAndRenderTask.validateParameters()) {
				runTask(runAndRenderTask);				
			} else {
				String message = "EMF Smell Reporting Error: Invalid Parameter Value '" + DESIGN_PARAMETER + "'";
				MessageDialog.openInformation(shell, "EMF Quality Assurance - Smell Reporting -", message);
			}
			runAndRenderTask.close();
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					"EMF Quality Assurance - Smell Reporting -", "Reporting successfully finished.");
		} catch (EngineException e) {
			e.printStackTrace();
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					"EMF Quality Assurance - Smell Reporting -", "Reporting not finished successfully.");
		} finally {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setCursor(oldCursor);
		}
		engine.destroy();
		// end
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setCursor(oldCursor);
	}

	private void runTask(IRunAndRenderTask runAndRenderTask) throws EngineException {
		if (pdf) {
			runPDF(runAndRenderTask);
		}
		if (html) {
			runHTML(runAndRenderTask);
		}
		if (doc) {
			runDOC(runAndRenderTask);
		}
		if (postscript) {
			runPostscript(runAndRenderTask);
		}
		if (xls) {
			runEXCEL(runAndRenderTask);
		}
		if (ppt) {
			runPPT(runAndRenderTask);
		}
		if (odp) {
			runODP(runAndRenderTask);
		}
		if (ods) {
			runODS(runAndRenderTask);
		}
		if (odt) {
			runODT(runAndRenderTask);
		}
	}
	
	private void runODT(IRunAndRenderTask runAndRenderTask) throws EngineException {
		RenderOption renderOption = new RenderOption();
		String outputFileName = fileName + ODT_FILE_EXTENSION;
		renderOption.setOutputFileName(outputFileName);
		renderOption.setOutputFormat("odt");
		runAndRenderTask.setRenderOption(renderOption);
		runAndRenderTask.run();
	}
	
	private void runODS(IRunAndRenderTask runAndRenderTask) throws EngineException {
		RenderOption renderOption = new RenderOption();
		String outputFileName = fileName + ODS_FILE_EXTENSION;
		renderOption.setOutputFileName(outputFileName);
		renderOption.setOutputFormat("ods");
		runAndRenderTask.setRenderOption(renderOption);
		runAndRenderTask.run();
	}

	private void runODP(IRunAndRenderTask runAndRenderTask) throws EngineException {
		RenderOption renderOption = new RenderOption();
		String outputFileName = fileName + ODP_FILE_EXTENSION;
		renderOption.setOutputFileName(outputFileName);
		renderOption.setOutputFormat("odp");
		runAndRenderTask.setRenderOption(renderOption);
		runAndRenderTask.run();
	}

	private void runPPT(IRunAndRenderTask runAndRenderTask) throws EngineException {
		RenderOption renderOption = new RenderOption();
		String outputFileName = fileName + PPT_FILE_EXTENSION;
		renderOption.setOutputFileName(outputFileName);
		renderOption.setOutputFormat("ppt");
		runAndRenderTask.setRenderOption(renderOption);
		runAndRenderTask.run();
	}

	private void runEXCEL(IRunAndRenderTask runAndRenderTask) throws EngineException {
		EXCELRenderOption xlsOptions = new EXCELRenderOption();
		String outputFileName = fileName + EXCEL_FILE_EXTENSION;
		xlsOptions.setOutputFileName(outputFileName);
		xlsOptions.setOutputFormat("xls");
		runAndRenderTask.setRenderOption(xlsOptions);
		runAndRenderTask.run();
	}

	private void runPostscript(IRunAndRenderTask runAndRenderTask) throws EngineException {
		RenderOption renderOption = new RenderOption();
		String outputFileName = fileName + POSTSCRIPT_FILE_EXTENSION;
		renderOption.setOutputFileName(outputFileName);
		renderOption.setOutputFormat("postscript");
		runAndRenderTask.setRenderOption(renderOption);
		runAndRenderTask.run();
	}

	private void runDOC(IRunAndRenderTask runAndRenderTask) throws EngineException {
		RenderOption renderOption = new RenderOption();
		String outputFileName = fileName + DOC_FILE_EXTENSION;
		renderOption.setOutputFileName(outputFileName);
		renderOption.setOutputFormat("doc");
		runAndRenderTask.setRenderOption(renderOption);
		runAndRenderTask.run();
	}

	private void runHTML(IRunAndRenderTask runAndRenderTask) throws EngineException {
		HTMLRenderOption htmlOptions = new HTMLRenderOption();
		String outputFileName = fileName + HTML_FILE_EXTENSION;
		htmlOptions.setOutputFileName(outputFileName);
		htmlOptions.setOutputFormat("html");
		htmlOptions.setEmbeddable(false);
		runAndRenderTask.setRenderOption(htmlOptions);
		runAndRenderTask.run();
	}

	private void runPDF(IRunAndRenderTask runAndRenderTask) throws EngineException {
		PDFRenderOption pdfOptions = new PDFRenderOption();
		String outputFileName = fileName + PDF_FILE_EXTENSION;
		pdfOptions.setOutputFileName(outputFileName);
		pdfOptions.setOutputFormat("pdf");
		runAndRenderTask.setRenderOption(pdfOptions);
		runAndRenderTask.run();			
	}

	private String getDesignFileName() {
		String designFileName = "";
		if (internalDesigns.contains(designName)) {
			designFileName = designsDirectory + designName;
		} else {
			String projectName = resource.getURI().segment(0);				
			IWorkspace workspace = ResourcesPlugin.getWorkspace();				
			project = workspace.getRoot().getProject(projectName);
			IFolder folder = project.getFolder(DESIGNS);
			designFileName = folder.getLocationURI().getPath() + "/" + designName;;
		}
		return designFileName;
	}

	private String getDirectory(String file) {
		int i = file.lastIndexOf("/");
		String name = file.substring(0, i);
		return name;
	}
}
