package org.eclipse.emf.refactor.smells.managers;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

/**
 * Superclass for all the necessary accesses to xml files by EMF Smells.
 * 
 * @author Matthias Burhenne
 * @author Pawel Stepien
 *
 */

public class XMLManager {
	
	protected static final String MODELSMELL_TAG = "modelsmell";
	protected static final String MODELSMELL_ID_TAG = "modelsmellId";
	protected  static final String MODELSMELL_NAME_TAG = "modelsmellName";	
	
	protected static Transformer createTransformer(){
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT,"yes");
			return transformer;
		} catch (final TransformerConfigurationException e) {
			e.printStackTrace();
			return null;
		}		
	}
		
	protected  static DocumentBuilder createDocumentBuilder() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (final ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}
}
