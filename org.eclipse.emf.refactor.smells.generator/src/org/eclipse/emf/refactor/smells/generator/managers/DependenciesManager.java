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
 *
 */

public class DependenciesManager {

	protected final static String ECORE = "org.eclipse.emf.ecore";
	protected final static String REFACTOR_SMELLS = "org.eclipse.emf.refactor.smells";
	private final static String REFACTOR_METRICS = "org.eclipse.emf.refactor.metrics";
	protected final static String REQ_BUNDLE = "Require-Bundle";
	protected final static String MANIFEST_FILE = "/META-INF/MANIFEST.MF";
	
	/**
	 * Sets the dependencies of the target project by manipulating the MANIFEST.MF of
	 * <br>- the project accordingly.
	 * @param info - the  <i>ModelSmellInfo</i> containing the data for the new smell
	 */
	public static void updateDependencies(ModelSmellInfo info) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(info.getProjectName());
		try {
			URL newUrl = new URL(project.getLocationURI().toURL().toString()
					+ MANIFEST_FILE);
			InputStream is = newUrl.openStream();
			Manifest mf = new Manifest(is);
			Attributes att = mf.getMainAttributes();
			String value = att.getValue(REQ_BUNDLE);
			if (!value.contains(ECORE))
				value = value + "," + ECORE;
			if (!value.contains(REFACTOR_SMELLS))
				value = value + "," + REFACTOR_SMELLS;
			if((info instanceof MetricBasedModelSmellInfo) && ! value.contains(REFACTOR_METRICS))
				value = value + "," + REFACTOR_METRICS;
			att.putValue(REQ_BUNDLE, value);
			FileOutputStream out = new FileOutputStream(newUrl.getFile());
			mf.write(out);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
