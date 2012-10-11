package org.eclipse.emf.refactor.smells.configuration.managers;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.refactor.smells.configuration.core.Configuration;
import org.eclipse.emf.refactor.smells.core.MetricBasedModelSmellFinderClass;
import org.eclipse.emf.refactor.smells.core.ModelSmell;
import org.eclipse.emf.refactor.smells.managers.XMLManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility class for the access of the .emfsmell.xml of single project's
 * 
 * @author Matthias Burhenne
 * @author Pawel Stepien
 *
 */

public class XMLProjectFileManager extends XMLManager {
	private static final String REF_CONFIG = ".emfsmell.xml";
	private static final String CONFIGURATOR_TAG = "smellConfiguration";
	private static final String LIMIT_TAG = "metric_limit";
	
	/**
	 * Writes the information from the given <i>Configuration</i> back to the
	 * <br>- project's .emfsmell.xml
	 * @param configuraton - the <i>Configuration</i> object containing the data to be written
	 */
	public static void saveConfigurator(Configuration configuraton) {
		final DocumentBuilder builder = createDocumentBuilder();
		if (builder != null){
			final Document doc = builder.newDocument();		
			final Element root = doc.createElement(CONFIGURATOR_TAG);
			doc.appendChild(root);
			LinkedList<Double> selectedLimits = configuraton.getLimitsForSelectedSmells();
			int i = 0;
			for (ModelSmell smell : configuraton.getSelectedModelSmells()){
				Element smellEntry = doc.createElement(MODELSMELL_TAG);
				Element smellId = doc.createElement(MODELSMELL_ID_TAG);
				smellId.setTextContent(smell.getId());
				smellEntry.appendChild(smellId);
				if(smell.getFinderClass() instanceof MetricBasedModelSmellFinderClass){
					Element smellMetricLimit = doc.createElement(LIMIT_TAG);
					smellMetricLimit.setTextContent(selectedLimits.get(i).toString());
					smellEntry.appendChild(smellMetricLimit);
				}
				root.appendChild(smellEntry);
				i++;
			}				
			final Transformer transformer = createTransformer();
			final DOMSource source = new DOMSource(doc);		
			final java.net.URI uri = configuraton.getProject().getLocationURI();
			StreamResult result = new StreamResult(uri + "/" + REF_CONFIG);			
			try {
				transformer.transform(source, result);
			} catch (final TransformerException e) {
				e.printStackTrace();
			}		
		}
	}
	
	/**
	 * Loads the configuration of a project from its .emfsmell.xml and
	 * returns it as an object of type <i>Configuration</i>.
	 * @param project - target project
	 * @return <i>Configuration</i> object for the target project
	 */
	public static Configuration loadConfigurator(IProject project){
		if(!refConfigExists(project)) {
			return null;}
		Configuration configuration = new Configuration(project);
		final DocumentBuilder builder = createDocumentBuilder();
		if (builder != null){
			Document doc = null;
			try {
				java.net.URI uri = project.getLocationURI();
				doc = builder.parse(uri + "/" + REF_CONFIG);
			} catch (final SAXException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			if (doc != null) {
				Element root = doc.getDocumentElement();					
				NodeList entryList = root.getElementsByTagName(MODELSMELL_TAG);
				LinkedList<String> smellIdList = new LinkedList<String>();
				for (int i=0; i < entryList.getLength(); i++){
					Element entry = (Element) entryList.item(i);
					NodeList nodeList = entry.getElementsByTagName(MODELSMELL_ID_TAG);
					Element smellId = (Element) nodeList.item(0);
					smellIdList.add(smellId.getTextContent());
					
					NodeList limitList = entry.getElementsByTagName(LIMIT_TAG);
					if(!(limitList.getLength() < 1)){
						Element smellLimit = (Element) limitList.item(0);
						configuration.setLimit(smellId.getTextContent(), Double.parseDouble(smellLimit.getTextContent()));
					}
				}
				configuration.setSelection(smellIdList);
				}
			}		
		configuration.toString();
		return configuration;
	}	
	
	private static boolean refConfigExists(IProject project) {
		java.net.URI uri = project.getLocationURI();
		String path = uri.getPath() + "/" + REF_CONFIG;
		File file = new File(path);
		return file.exists();
	}
	
	
}
