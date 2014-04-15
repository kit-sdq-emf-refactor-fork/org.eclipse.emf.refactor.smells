package org.eclipse.emf.refactor.smells.henshin.managers;

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
import org.eclipse.emf.refactor.smells.generator.core.ModelSmellInfo;
import org.eclipse.emf.refactor.smells.generator.managers.XMLPluginFileManager;
import org.eclipse.emf.refactor.smells.henshin.Activator;
import org.eclipse.emf.refactor.smells.henshin.core.HenshinModelSmellInfo;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

public class HenshinGenerationManager {
	
	private static final String HENSHIN_TEMPLATE_CLASS_NAME = "HenshinFinderClassTemplate";
	private static HenshinGenerationManager instance;
	private static final  String TEMPLATE_DIR = "/templates";
	private static final String SOURCE_DIR = "/src/";
	private static final  String TEMPLATE_FILE_EXTENSION = ".javajet";
	private static final String JAVA_FILE_EXTENSION = ".java";
	private static final String PLUGINSPATH = Platform.getInstallLocation().getURL().getPath() + "plugins/";
	private static final String BUNDLEVERSION = "Bundle-Version";
	protected static String templateDirectory;
	protected static List<IClasspathEntry> classpathEntries;
	
	private HenshinGenerationManager() {
		templateDirectory = setTemplateDirectory();
		classpathEntries = setClassPathEntries();
		System.out.println("HenshinGenerationManager initialized!");
	}
	
	public static HenshinGenerationManager getInstance() {
		if (instance == null) {
			instance = new HenshinGenerationManager();
		}
		return instance;
	}
	
	protected List<IClasspathEntry> setClassPathEntries() {
		List<IClasspathEntry> cpe = new ArrayList<IClasspathEntry>();
		Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
	    // add org.eclipse.emf.refactor.smells.henshin to class path
	    String version = (String) bundle.getHeaders().get(BUNDLEVERSION);
	    cpe.add(JavaCore.newLibraryEntry(new Path(PLUGINSPATH + 
	    		Activator.PLUGIN_ID + "_" + version + ".jar"), null, null));
	    // add org.eclipse.emf.refactor.smells to class path
	    bundle = Platform.getBundle(org.eclipse.emf.refactor.smells.Activator.PLUGIN_ID);
	    version = (String) bundle.getHeaders().get(BUNDLEVERSION);
	    cpe.add(JavaCore.newLibraryEntry(new Path(PLUGINSPATH + 
	    		org.eclipse.emf.refactor.smells.Activator.PLUGIN_ID + "_" + version + ".jar"), null, null));	
	    return cpe;
	}
	
	public static void createNewModelSmell(IProgressMonitor monitor,
			HenshinModelSmellInfo modelSmellInfo, IProject targetProject) {
		System.out.println(modelSmellInfo);
		HenshinDependenciesManager.updateDependencies(modelSmellInfo);
		createCalculateClass(monitor, modelSmellInfo);		
		XMLPluginFileManager.createModelSmellEntry(modelSmellInfo.getProjectPath(), 
				modelSmellInfo.getName(), modelSmellInfo.getDescription(), 
				modelSmellInfo.getMetamodel(), modelSmellInfo.getId(),  
				modelSmellInfo.getPackage()+"."+modelSmellInfo.getClassName());
		try {
			targetProject.refreshLocal(IProject.DEPTH_INFINITE, monitor);
			targetProject.refreshLocal(IProject.DEPTH_INFINITE, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	private static void createCalculateClass(IProgressMonitor monitor, HenshinModelSmellInfo modelSmellInfo) {
		String generatedCode = "";
		String templateName = HENSHIN_TEMPLATE_CLASS_NAME;
		try {
//			MessageDialog.openError(null, null, "vor generateCode()");
			generatedCode = generateCode(monitor, templateName, modelSmellInfo);
//			MessageDialog.openError(null, null, "nach generateCode()");
		    saveCode(monitor, generatedCode, modelSmellInfo);
//		    MessageDialog.openError(null, null, "nach saveCode()");
		} catch (JETException e) {
//			MessageDialog.openError(null, null, e.getMessage());
			e.printStackTrace();
		} catch (CoreException e) {
//			MessageDialog.openError(null, null, e.getMessage());
			e.printStackTrace();
		}
	}
	
	protected String setTemplateDirectory() {
		String directory = "";
		final Bundle bundle = Activator.getDefault().getBundle();
		try {
			directory = FileLocator.toFileURL(bundle.getEntry(TEMPLATE_DIR)).getFile();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return directory;
	}
	
	protected static String generateCode(IProgressMonitor monitor, String template, HenshinModelSmellInfo modelSmellInfo) {
		String templatePath = templateDirectory + template + TEMPLATE_FILE_EXTENSION;
		ClassLoader classLoader = modelSmellInfo.getClass().getClassLoader();
		JETEmitter jetEmitter = new JETEmitter(templatePath, classLoader);
		jetEmitter.getClasspathEntries().addAll(classpathEntries);
		String result = "";
		IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
		try {
			result = jetEmitter.generate(subMonitor, new Object[] { modelSmellInfo });
		} catch (JETException e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return result;
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

}
