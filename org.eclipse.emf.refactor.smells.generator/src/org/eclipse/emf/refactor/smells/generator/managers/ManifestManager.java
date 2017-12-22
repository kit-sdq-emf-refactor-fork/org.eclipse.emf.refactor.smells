package org.eclipse.emf.refactor.smells.generator.managers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.refactor.smells.generator.core.MetricBasedModelSmellInfo;
import org.eclipse.emf.refactor.smells.generator.core.ModelSmellInfo;

/**
 * Helper class for updating the dependencies of the target project of
 * <br>- a smell plugin generation.
 * 
 * @author Matthias Burhenne
 * @author Pawel Stepien
 * @author René Hahn
 *
 */

public class ManifestManager {

	protected final static String ECORE = "org.eclipse.emf.ecore";
	protected final static String REFACTOR_SMELLS = "org.eclipse.emf.refactor.smells";
	private final static String REFACTOR_METRICS = "org.eclipse.emf.refactor.metrics";
	protected final static String REQ_BUNDLE = "Require-Bundle";
	protected final static String MANIFEST_FILE = "/META-INF/MANIFEST.MF";
	protected final static String SYMBOLIC_NAME = "Bundle-SymbolicName";
	protected final static String SINGLETON = "singleton:=true";
	
	/**
	 * Sets the dependencies of the target project by manipulating the MANIFEST.MF of
	 * <br>- the project accordingly.
	 * @param info - the <i>ModelSmellInfo</i> containing the data for the new smell
	 */
	public static void updatePluginDependencies(ModelSmellInfo info) 
	{
		Manifest manifest = getManifest(info);
		if(manifest != null)
		{
			Attributes att = manifest.getMainAttributes();
			String value = "";
			if(att.getValue(REQ_BUNDLE) != null)
			{
				value = att.getValue(REQ_BUNDLE);
			}
			if (!value.contains(ECORE))
				value = value + "," + ECORE;
			if (!value.contains(REFACTOR_SMELLS))
				value = value + "," + REFACTOR_SMELLS;
			if((info instanceof MetricBasedModelSmellInfo) && ! value.contains(REFACTOR_METRICS))
				value = value + "," + REFACTOR_METRICS;
			if(value.startsWith(","))
			{
				value = value.substring(1);
			}
			att.putValue(REQ_BUNDLE, value);
			writeManifest(info, manifest);
		}			
	}
	
	/**
	 * Sets the plugin project to a singleton. This is required due to the extension point.
	 * @param info - the <i>ModelSmellInfo</i> containing the data for the new smell
	 */
	public static void updatePluginToSingleton(ModelSmellInfo info)
	{
		Manifest manifest = getManifest(info);
		if(manifest != null)
		{
			Attributes attributes = manifest.getMainAttributes();
			String value = "";
			if(attributes.getValue(SYMBOLIC_NAME) != null)
			{
				value = attributes.getValue(SYMBOLIC_NAME);
				if(!value.contains(SINGLETON))
				{
					value += ";" + SINGLETON;
				}
				attributes.putValue(SYMBOLIC_NAME, value);
				writeManifest(info, manifest);
			}
		}
	}
	
	/**
	 * Gets the Manifest file from target project
	 * @param info
	 * @return Manifest file from project if found. Otherwise null
	 */
	private static Manifest getManifest(ModelSmellInfo info)
	{
		URL url = getURL(info);
		try 
		{
			InputStream is = url.openStream();
			Manifest mf = new Manifest(is);
			return mf;
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private static void writeManifest(ModelSmellInfo info, Manifest manifest)
	{
		URL url = getURL(info);
		try 
		{
			FileOutputStream out = new FileOutputStream(url.getFile());
			manifest.write(out);		
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	private static URL getURL(ModelSmellInfo info)
	{
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(info.getProjectName());
		try
		{
			URL newUrl = new URL(project.getLocationURI().toURL().toString() + MANIFEST_FILE);
			return newUrl;
		}
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
}
