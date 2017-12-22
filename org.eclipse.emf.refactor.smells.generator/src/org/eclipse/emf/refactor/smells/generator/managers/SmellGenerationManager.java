package org.eclipse.emf.refactor.smells.generator.managers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.codegen.jet.JETEmitter;
import org.eclipse.emf.codegen.jet.JETException;
import org.eclipse.emf.codegen.util.CodeGenUtil;
import org.eclipse.emf.refactor.smells.generator.Activator;
import org.eclipse.emf.refactor.smells.generator.core.MetricBasedModelSmellInfo;
import org.eclipse.emf.refactor.smells.generator.core.ModelSmellInfo;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

public class SmellGenerationManager {
	
	private static SmellGenerationManager instance;
	
	private static final String JAVA_TEMPLATE_CLASS_NAME = "JavaFinderClassTemplate";
	private static final String METRIC_TEMPLATE_CLASS_NAME = "MetricBasedFinderClassTemplate";
	protected static final  String TEMPLATE_DIR = "/templates";
	private static final String SOURCE_DIR = "/src/";
	protected static final  String TEMPLATE_FILE_EXTENSION = ".javajet";
	private static final String JAVA_FILE_EXTENSION = ".java";		
	protected static final String PLUGINSPATH = Platform.getInstallLocation().getURL().getPath() + "plugins/";
	protected static final String BUNDLEVERSION = "Bundle-Version";
	protected static String templateDirectory;
	protected static List<IClasspathEntry> classpathEntries;
	
	protected SmellGenerationManager() {
		templateDirectory = setTemplateDirectory();
		classpathEntries = setClassPathEntries();
		System.out.println("GenerationManager initialized!");
	}
	
	protected List<IClasspathEntry> setClassPathEntries() {
		ArrayList<IClasspathEntry> cpe = new ArrayList<IClasspathEntry>();
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
		// add org.eclipse.emf.refactor.smells.generator to class path
		String version = (String) bundle.getHeaders().get(BUNDLEVERSION);
		cpe.add(JavaCore.newLibraryEntry(new Path(PLUGINSPATH
				+ Activator.PLUGIN_ID + "_" + version + ".jar"), null, null));
		// add org.eclipse.emf.refactor.smells to class path
		bundle = Platform.getBundle(org.eclipse.emf.refactor.smells.Activator.PLUGIN_ID);
	    version = (String) bundle.getHeaders().get(BUNDLEVERSION);
	    cpe.add(JavaCore.newLibraryEntry(new Path(PLUGINSPATH + 
	    		org.eclipse.emf.refactor.smells.Activator.PLUGIN_ID + "_" + version + ".jar"), null, null));	
		return cpe;
	}

	public static SmellGenerationManager getInstance() {
		if (instance == null) {
			instance = new SmellGenerationManager();
		}
		return instance;
	}

	public static void createNewModelSmell(IProgressMonitor monitor,
			ModelSmellInfo modelSmellInfo, IProject newSmellTargetProject) {
		ManifestManager.updatePluginDependencies(modelSmellInfo);
		ManifestManager.updatePluginToSingleton(modelSmellInfo);
		createFinderClass(monitor, modelSmellInfo);		
		XMLPluginFileManager.createModelSmellEntry(modelSmellInfo.getProjectPath(), 
				modelSmellInfo.getName(), modelSmellInfo.getDescription(), 
				modelSmellInfo.getMetamodel(), modelSmellInfo.getId(),  
				modelSmellInfo.getPackage()+"."+modelSmellInfo.getClassName());
		try {
			newSmellTargetProject.refreshLocal(IProject.DEPTH_INFINITE, monitor);
			newSmellTargetProject.refreshLocal(IProject.DEPTH_INFINITE, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private static void createFinderClass(IProgressMonitor monitor,
			ModelSmellInfo modelSmellInfo) {
		String generatedCode = "";
		String templateName = "";
		if(modelSmellInfo instanceof MetricBasedModelSmellInfo){
			templateName = METRIC_TEMPLATE_CLASS_NAME;
		} else {
			templateName = JAVA_TEMPLATE_CLASS_NAME;
		}
		try {
			generatedCode = generateCode(monitor, templateName, modelSmellInfo);
		    saveCode(monitor, generatedCode, modelSmellInfo);
		} catch (JETException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	protected static void saveCode(IProgressMonitor monitor, String content, ModelSmellInfo modelSmellInfo) throws CoreException, JETException {
		IContainer container = findOrCreatePackage(monitor, modelSmellInfo);
		if (container == null) {
			throw new JETException("Could not find or create container for package " + modelSmellInfo.getPackage() + " in " + modelSmellInfo.getProjectName());
		}
		IFile file = container.getFile(new Path(modelSmellInfo.getClassName() + JAVA_FILE_EXTENSION));
		System.out.println("saving code into: "+file.getLocation());
		InputStream inputStream = new ByteArrayInputStream(content.getBytes());
		if (!file.exists()) {
			file.create(inputStream, false, monitor);
		} else {
			container.refreshLocal(1, monitor);
			file.setContents(inputStream, true, false, monitor);
		}
	}
	
	private static IContainer findOrCreatePackage(IProgressMonitor progressMonitor, ModelSmellInfo modelSmellInfo) throws CoreException {
		IPath outputPath = new Path(modelSmellInfo.getProjectName() + SOURCE_DIR + modelSmellInfo.getPackage().replace('.', '/'));
		IProgressMonitor subMonitor = new SubProgressMonitor(progressMonitor, 1);
		IPath localLocation = null; 
		IContainer container = CodeGenUtil.EclipseUtil.findOrCreateContainer(outputPath, true, localLocation, subMonitor);
		return container;
	}
	
	@SuppressWarnings("finally")
	private static String generateCode(IProgressMonitor monitor, String template, ModelSmellInfo modelSmellInfo) {
		String templatePath = templateDirectory + template + TEMPLATE_FILE_EXTENSION;
		ClassLoader classLoader = modelSmellInfo.getClass().getClassLoader();
		JETEmitter jetEmitter = new JETEmitter(templatePath, classLoader);
		jetEmitter.getClasspathEntries().addAll(classpathEntries);
		String result = "";
		IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
		try {
			result = jetEmitter.generate(subMonitor, new Object[] {modelSmellInfo});
		} catch (JETException e) {
			result = e.getMessage();
			e.printStackTrace();
		} finally {
			System.out.println(result);
			return result;
		}
	}

	private String setTemplateDirectory() {
		String directory = "";
		final Bundle bundle = Activator.getDefault().getBundle();
		try {
			directory = FileLocator.toFileURL(bundle.getEntry(TEMPLATE_DIR)).getFile();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return directory;
	}
}
