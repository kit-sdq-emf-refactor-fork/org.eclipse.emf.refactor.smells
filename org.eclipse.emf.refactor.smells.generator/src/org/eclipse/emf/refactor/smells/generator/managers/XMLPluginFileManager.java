package org.eclipse.emf.refactor.smells.generator.managers;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.refactor.smells.core.ExtensionPointTags;
import org.eclipse.emf.refactor.smells.managers.XMLManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility class for the access of the target project's plugin.xml during the generation process
 * 
 * @author Pawel Stepien
 *
 */

public class XMLPluginFileManager extends XMLManager {

	public static final String PLUGIN_FILE = "/plugin.xml";
	public static final String PLUGIN_TAG = "plugin";
	public static final String EXTENSION_TAG = "extension";
	public static final String POINT_ATTR_TAG = "point";
	
	/**
	 * Creates the necessary entries in the plugin.xml of the target project
	 * @param project - target project's name
	 * @param name - name of the new smell plugin
	 * @param description - description of the new smell plugin
	 * @param metamodel - uri of the new plugin's metamodel (as String)
	 * @param id - id of the new smell plugin
	 * @param finderClass - name of the finderclass of the new plugin
	 */
	
	public static void createModelSmellEntry(String project, String name,
			String description, String metamodel, String id, String finderClass) {
		final DocumentBuilder builder = createDocumentBuilder();
		final String path = project + PLUGIN_FILE;
		Document doc = null;
		final File file = new File(path);
		if (!file.exists())
			createPluginFile(project);
		if (builder != null) {
			Element root;
			try {
				doc = builder.parse(path);
			} catch (final SAXException e) {
				e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			if (doc != null) {
				root = doc.getDocumentElement();
				Node extensionPoint = root.getElementsByTagName(EXTENSION_TAG).item(0); 
				Node newEntry = createModelSmellElement(doc, name, id, description,	metamodel, finderClass);
				extensionPoint.appendChild(newEntry);
			}
		}
		final Transformer transformer = createTransformer();
		final DOMSource source = new DOMSource(doc);
		final StreamResult result = new StreamResult(path);
		try {
			transformer.transform(source, result);
		} catch (final TransformerException e) {
			e.printStackTrace();
		}
	}
	
	public static LinkedList<String> readSmellIDs(IProject project){
		LinkedList<String> iDs = new LinkedList<String>();
		String fileName = "plugin.xml";
		IFile pluginFile = project.getFile(fileName);
		if(pluginFile.exists()){
			final DocumentBuilder builder = createDocumentBuilder();
			try{
				String path = pluginFile.getLocation().toString();
				Document doc = builder.parse(path);
				Element root = doc.getDocumentElement();
				NodeList childList = root.getElementsByTagName(EXTENSION_TAG);
				for(int i = 0; i < childList.getLength(); i++){
					Node node = childList.item(i);
					String extensionName = node.getAttributes().getNamedItem(POINT_ATTR_TAG).getNodeValue();
					if(extensionName.equals(ExtensionPointTags.EXTENSION_POINT_NAME)){
						NodeList modelSmellExtensionNodes = node.getChildNodes();
						for(int j = 0; j < modelSmellExtensionNodes.getLength(); j++){
							Node attributeNode = modelSmellExtensionNodes.item(j);
							String nodeName = attributeNode.getNodeName();
							if(nodeName != MODELSMELL_TAG) continue;
							NamedNodeMap attributes = attributeNode.getAttributes();
							Node idNode = attributes.getNamedItem(ExtensionPointTags.MODELSMELL_ID_TAG);
							iDs.add(idNode.getNodeValue());
						}						
					}					
				}
			}catch(SAXException ex){
				ex.printStackTrace();
			}catch(IOException ex){
				ex.printStackTrace();
			}			
		}
		return iDs;
	}

	private static void createPluginFile(String project) {
		final DocumentBuilder builder = createDocumentBuilder();
		final String path = project + PLUGIN_FILE;
		Document doc = null;
		Element root;
		if (builder != null) {
			doc = builder.newDocument();
			root = doc.createElement(PLUGIN_TAG );
			doc.appendChild(root);
			final Element extension = doc.createElement(ExtensionPointTags.EXTENSION_POINT_TAG);
			extension.setAttribute(POINT_ATTR_TAG, ExtensionPointTags.EXTENSION_POINT_NAME);
			root.appendChild(extension);
		}
		final Transformer transformer = createTransformer();
		final DOMSource source = new DOMSource(doc);
		final StreamResult result = new StreamResult(path);
		try {
			transformer.transform(source, result);
		} catch (final TransformerException e) {
			e.printStackTrace();
		}
	}

	private static Element createModelSmellElement(Document doc, String name,
			String id, String description, String metamodel, String finderClass) {
		final Element modelsmell = doc.createElement(ExtensionPointTags.MODELSMELL_TAG);
		modelsmell.setAttribute(ExtensionPointTags.MODELSMELL_NAME_TAG, name);
		modelsmell.setAttribute(ExtensionPointTags.MODELSMELL_ID_TAG, id);
		modelsmell.setAttribute(ExtensionPointTags.MODELSMELL_DESCRIPTION_TAG, description);
		modelsmell.setAttribute(ExtensionPointTags.MODELSMELL_METAMODEL_TAG, metamodel);
		modelsmell.setAttribute(ExtensionPointTags.MODELSMELL_FINDER_CLASS_TAG, finderClass);
		return modelsmell;
	}
}
