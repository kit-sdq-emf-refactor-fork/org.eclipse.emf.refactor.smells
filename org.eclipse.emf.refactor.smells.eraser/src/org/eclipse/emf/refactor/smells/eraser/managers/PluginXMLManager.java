package org.eclipse.emf.refactor.smells.eraser.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.refactor.refactoring.core.Refactoring;
import org.eclipse.emf.refactor.smells.core.ModelSmell;
import org.eclipse.emf.refactor.smells.eraser.core.ExtensionPointTags;
import org.eclipse.emf.refactor.smells.eraser.utils.ModelRefactoringStub;
import org.eclipse.emf.refactor.smells.eraser.utils.ModelSmellStub;
import org.eclipse.emf.refactor.smells.eraser.utils.ProjectEntries;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility class wrapping the access to the plugin.xml of eclipse plugin projects
 * 
 * @author Matthias Burhenne
 *
 */

public class PluginXMLManager {
	

	private static final String PLUGIN_XML_FILE_NAME = "plugin.xml";
	private static final String PLUGIN_FILE = "/plugin.xml";
	private static final String PLUGIN_TAG = "plugin";
	private static final String EXTENSION_TAG = "extension";
	private static final String POINT_ATTR_TAG = "point";
	
	private static final String EXTENSION_POINT_ID_MODELSMELL = org.eclipse.emf.refactor.smells.core.ExtensionPointTags.EXTENSION_POINT_NAME;
	private static final String MODELSMELL_TAG = org.eclipse.emf.refactor.smells.core.ExtensionPointTags.MODELSMELL_TAG;
	private static final String MODELSMELL_NAME_TAG = org.eclipse.emf.refactor.smells.core.ExtensionPointTags.MODELSMELL_NAME_TAG;
	private static final String MODELSMELL_ID_TAG = org.eclipse.emf.refactor.smells.core.ExtensionPointTags.MODELSMELL_ID_TAG;
	private static final String MODELSMELL_DESCRIPTION_TAG = org.eclipse.emf.refactor.smells.core.ExtensionPointTags.MODELSMELL_DESCRIPTION_TAG;
	private static final String MODELSMELL_METAMODEL_TAG = org.eclipse.emf.refactor.smells.core.ExtensionPointTags.MODELSMELL_METAMODEL_TAG;
	
	private static final Object EXTENSION_POINT_ID_EMF_REFACTORING = org.eclipse.emf.refactor.refactoring.core.ExtensionPointTags.EXTENSION_POINT_NAME;
	private static final String REFACTORING_TAG = org.eclipse.emf.refactor.refactoring.core.ExtensionPointTags.REFACTORING_TAG;
	

	/**
	 * Reads the entries for the Extension Points of EMF Fix from the target project's plugin.xml.
	 * 
	 * @param project - target Project (IProject)
	 * @return ProjectEntries containing the relation definitions
	 */
	public static ProjectEntries getRelationEntries(IProject project){
		ProjectEntries entries = new ProjectEntries(project);		
		
		Set<ModelSmellStub> smellsInWorkspace = EraseManager.getInstance().getAllSmellStubsFromWorkspace();
		Set<ModelRefactoringStub> refactoringsInWorkspace = EraseManager.getInstance().getAllRefactoringStubsFromWorkspace();
		
		final DocumentBuilder builder = createDocumentBuilder();
		final String path = project.getLocation().toString() + PLUGIN_FILE;
		Document doc = null;
		final File file = new File(path);
		if (!file.exists())
			return entries;
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
				NodeList extensionPoints 
					= root.getElementsByTagName(EXTENSION_TAG);
				for(int i = 0; i < extensionPoints.getLength(); i++){
					if(extensionPoints.item(i).getAttributes().getNamedItem(POINT_ATTR_TAG).getNodeValue()
							.equals(ExtensionPointTags.EXTENSION_POINT_NAME_SMELL_TO_REFACTORINGS)){
						NodeList smellToRefactoringsExtensionEntries = ((Element)extensionPoints.item(i))
							.getElementsByTagName(ExtensionPointTags.SMELL_TO_REFACTORING_TAG);
						for(int j = 0; j < smellToRefactoringsExtensionEntries.getLength(); j++){
								Node smellIdEntry = smellToRefactoringsExtensionEntries.item(j);
								String smellID = smellIdEntry.getAttributes().getNamedItem(ExtensionPointTags.SMELL_ID_TAG).getNodeValue();
								
								ModelSmell smell = EraseManager.getInstance().getSmell(smellID);
								ModelSmellStub smellStub = null;
								if(smell != null){
									smellStub = ModelSmellStub.convertModelSmell(smell);
								}
								if(smell == null){
									for(ModelSmellStub stub : smellsInWorkspace){
										if(stub.getId().equals(smellID)){
											smellStub = stub;
										}
									}
								}
									NodeList refactoringIDEntries =  ((Element)smellIdEntry)
										.getElementsByTagName(ExtensionPointTags.SMELL_TO_REFACTORINGS_REFACTORING_ELEMENT_TAG);
									for(int k = 0; k < refactoringIDEntries.getLength(); k++){
										String refactoringID = refactoringIDEntries.item(k).getAttributes().getNamedItem(ExtensionPointTags.REFACTORING_ID_TAG).getNodeValue();
										Refactoring refactoring = null;
										ModelRefactoringStub refactoringStub = null;
										try{
											refactoring = EraseManager.getInstance().getRefactoring(refactoringID);
											refactoringStub = ModelRefactoringStub.convertRefactoring(refactoring);
										}catch(IllegalArgumentException ex){
											for(ModelRefactoringStub stub : refactoringsInWorkspace){
												if(stub.getId().equals(refactoringID)){
													refactoringStub = stub;
												}
											}											
										}
										if(refactoringStub != null && smellStub != null){
											entries.addFixingRefactoring(smellStub, refactoringStub);
										}else{
											entries.addDanglingFixingRelation(smellID, refactoringID);
										}
										
									}
						}
					}
				}
				for(int i = 0; i < extensionPoints.getLength(); i++){
					if(extensionPoints.item(i).getAttributes().getNamedItem(POINT_ATTR_TAG).getNodeValue()
							.equals(ExtensionPointTags.EXTENSION_POINT_NAME_REFACTORING_TO_SMELLS)){
						NodeList refactoringToSmellsExtensionEntries = ((Element)extensionPoints.item(i))
							.getElementsByTagName(ExtensionPointTags.REFACTORING_TO_SMELL_TAG);
						for(int j = 0; j < refactoringToSmellsExtensionEntries.getLength(); j++){
							Node refactoringEntry = refactoringToSmellsExtensionEntries.item(j);
							String refactoringID = refactoringEntry.getAttributes().getNamedItem(ExtensionPointTags.REFACTORING_ID_TAG).getNodeValue();
							Refactoring refactoring = null;
							ModelRefactoringStub refactoringStub = null;
							try{
								refactoring = EraseManager.getInstance().getRefactoring(refactoringID);
								refactoringStub = ModelRefactoringStub.convertRefactoring(refactoring);
							}catch(IllegalArgumentException ex){
//								ex.printStackTrace();
								for(ModelRefactoringStub stub : refactoringsInWorkspace){
									if(stub.getId().equals(refactoringID)){
										refactoringStub = stub;
									}
								}
							}
								NodeList smellIDEntries = ((Element)refactoringEntry)
									.getElementsByTagName(ExtensionPointTags.REFACTORING_TO_SMELLS_SMELL_ELEMENT_TAG);
								for(int k = 0; k < smellIDEntries.getLength(); k++){
									String smellID = smellIDEntries.item(k).getAttributes().getNamedItem(ExtensionPointTags.SMELL_ID_TAG).getNodeValue();
									ModelSmell smell = EraseManager.getInstance().getSmell(smellID);
									ModelSmellStub smellStub = null;
									if(smell != null){
										smellStub = ModelSmellStub.convertModelSmell(smell);
									}
									if(smellStub == null){
										for(ModelSmellStub stub : smellsInWorkspace){
											if(stub.getId().equals(smellID)){
												smellStub = stub;
											}
										}
									}
									if(smellStub != null && refactoringStub != null){
										entries.addCausedSmell(refactoringStub, smellStub);
									}else{
										entries.addDanglingCausedSmellsRelation(refactoringID, smellID);
									}
								}
							
							
							
							
						}
					}
				}
			}
		}
		//TODO: Implement reading from plugin.xml
		return entries;
	}
	
	/**
	 * Writes the information from the provided ProjectEntries to the target project's plugin.xml
	 * 
	 * @param entries -  the information on the relations
	 * @param project - the target project
	 * @param saveDanglingEntries - information on whether dangling relations should be saved (true) or discarded (false)
	 */
	public static void saveProjectEntries(ProjectEntries entries, IProject project, boolean saveDanglingEntries){
			final DocumentBuilder builder = createDocumentBuilder();
			final String path = project.getLocation().toString() + PLUGIN_FILE;
			Document doc = null;
			final File file = new File(path);
			if (!file.exists())
				createPluginFile(project.getLocation().toString());
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
					NodeList smellToRefactoringsNodes = root
								.getElementsByTagName(EXTENSION_TAG);
					List<Node> nodesToDelete = new ArrayList<Node>();
					for(int i = 0; i < smellToRefactoringsNodes.getLength(); i++){
						Node node = smellToRefactoringsNodes.item(i);
						if(node.getAttributes().getNamedItem(POINT_ATTR_TAG).getNodeValue().equals(ExtensionPointTags.EXTENSION_POINT_NAME_SMELL_TO_REFACTORINGS))
//							node.getParentNode().removeChild(node);
							nodesToDelete.add(node);
						else if(node.getAttributes().getNamedItem(POINT_ATTR_TAG).getNodeValue().equals(ExtensionPointTags.EXTENSION_POINT_NAME_REFACTORING_TO_SMELLS))
							nodesToDelete.add(node);
//							node.getParentNode().removeChild(node);
					}
					for(Node node : nodesToDelete){
						node.getParentNode().removeChild(node);
					}
					
					Element smellToRefactoringsNode = doc.createElement(EXTENSION_TAG);
					smellToRefactoringsNode.setAttribute(POINT_ATTR_TAG, ExtensionPointTags.EXTENSION_POINT_NAME_SMELL_TO_REFACTORINGS);
					boolean smellToRefactoringsFilled = false;
					for(ModelSmellStub smellStub : entries.getAllSmellsInFixingRelations()){
						Set<ModelRefactoringStub> refactoringStubs = entries.getFixingRefactorings(smellStub);
						if(refactoringStubs != null && !refactoringStubs.isEmpty()){
							Element smellToRefactoringsElement = doc.createElement(ExtensionPointTags.SMELL_TO_REFACTORING_TAG);
							smellToRefactoringsElement.setAttribute(ExtensionPointTags.SMELL_ID_TAG, smellStub.getId());
							smellToRefactoringsNode.appendChild(smellToRefactoringsElement);
							for(ModelRefactoringStub refactoringStub : refactoringStubs){
								Element refactoringElement = doc.createElement(ExtensionPointTags.SMELL_TO_REFACTORINGS_REFACTORING_ELEMENT_TAG);
								refactoringElement.setAttribute(ExtensionPointTags.REFACTORING_ID_TAG, refactoringStub.getId());
								smellToRefactoringsElement.appendChild(refactoringElement);
							}
							smellToRefactoringsFilled = true;
						}
					}
					boolean danglingFixingRelationsAdded = false;
					if(saveDanglingEntries){
						danglingFixingRelationsAdded = addDanglingFixingRelations(smellToRefactoringsNode, entries, doc);
					}
					if(smellToRefactoringsFilled || danglingFixingRelationsAdded){
						root.appendChild(smellToRefactoringsNode);
					}
					
					NodeList refactoringToSmellsNodes = root
							.getElementsByTagName(EXTENSION_TAG);
					for(int i = 0; i < refactoringToSmellsNodes.getLength(); i++){
//						Node node = refactoringToSmellsNodes.item(i);
//						if(node.getAttributes().getNamedItem(POINT_ATTR_TAG).getNodeValue().equals(ExtensionPointTags.EXTENSION_POINT_NAME_REFACTORING_TO_SMELLS))
//							node.getParentNode().removeChild(node);
					}
					Element refactoringToSmellsNode = doc.createElement(EXTENSION_TAG);
					refactoringToSmellsNode.setAttribute(POINT_ATTR_TAG, ExtensionPointTags.EXTENSION_POINT_NAME_REFACTORING_TO_SMELLS);
					boolean refactoringToSmellsFilled = false;
					for(ModelRefactoringStub refactoringStub : entries.getAllRefactoringsInCausingRelations()){
						Set<ModelSmellStub> smellStubs = entries.getCausedSmells(refactoringStub);
						if(smellStubs != null && !smellStubs.isEmpty()){
							Element refactoringToSmellsElement = doc.createElement(ExtensionPointTags.REFACTORING_TO_SMELL_TAG);
							refactoringToSmellsElement.setAttribute(ExtensionPointTags.REFACTORING_ID_TAG, refactoringStub.getId());
							refactoringToSmellsNode.appendChild(refactoringToSmellsElement);
							for(ModelSmellStub smell : smellStubs){
								Element smellElement = doc.createElement(ExtensionPointTags.REFACTORING_TO_SMELLS_SMELL_ELEMENT_TAG);
								smellElement.setAttribute(ExtensionPointTags.SMELL_ID_TAG, smell.getId());
								refactoringToSmellsElement.appendChild(smellElement);
							}
							refactoringToSmellsFilled = true;
						}
					}
					boolean danglingCausesAddded = false;
					if(saveDanglingEntries){
						danglingCausesAddded = addDanglingCausingRelations(refactoringToSmellsNode, entries, doc);
					}
					if(refactoringToSmellsFilled || danglingCausesAddded){
						root.appendChild(refactoringToSmellsNode);
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
	}
	
	
	/*
	 * Writes dangling relations from the entries to the document for relations between refactorings and caused smells
	 */
	private static boolean addDanglingCausingRelations(Element refactoringToSmellsNode, ProjectEntries entries, Document doc) {
		boolean entriesAdded = false;
		for(String refactoringID : entries.getDanglingCausedSmells().keySet()){
			Set<String> smellIDs = entries.getDanglingCausedSmells().get(refactoringID);
			if(!smellIDs.isEmpty()){
				Element refactoringToSmellsElement = doc.createElement(ExtensionPointTags.REFACTORING_TO_SMELL_TAG);
				refactoringToSmellsElement.setAttribute(ExtensionPointTags.REFACTORING_ID_TAG, refactoringID);
				refactoringToSmellsNode.appendChild(refactoringToSmellsElement);
				for(String smellID : smellIDs){
					Element smellElement = doc.createElement(ExtensionPointTags.REFACTORING_TO_SMELLS_SMELL_ELEMENT_TAG);
					smellElement.setAttribute(ExtensionPointTags.SMELL_ID_TAG, smellID);
					refactoringToSmellsElement.appendChild(smellElement);
				}
				entriesAdded = true;
			}
		}
		return entriesAdded;
	}

	/*
	 * Writes dangling relations from the entries to the document for relations between smells and fixing refactorings
	 */
	private static boolean addDanglingFixingRelations(Element smellToRefactoringsNode, ProjectEntries entries, Document doc) {
		boolean entriesAdded = false;
		for(String smellID : entries.getDanglingFixingRefactorings().keySet()){
			Set<String> refactoringIDs = entries.getDanglingFixingRefactorings().get(smellID);
			if(!refactoringIDs.isEmpty()){
				Element smellToRefactoringsElement = doc.createElement(ExtensionPointTags.SMELL_TO_REFACTORING_TAG);
				smellToRefactoringsElement.setAttribute(ExtensionPointTags.SMELL_ID_TAG, smellID);
				smellToRefactoringsNode.appendChild(smellToRefactoringsElement);
				for(String refactoringID : refactoringIDs){
					Element refactoringElement = doc.createElement(ExtensionPointTags.SMELL_TO_REFACTORINGS_REFACTORING_ELEMENT_TAG);
					refactoringElement.setAttribute(ExtensionPointTags.REFACTORING_ID_TAG, refactoringID);
					smellToRefactoringsElement.appendChild(refactoringElement);
				}
				entriesAdded = true;
			}
		}
		return entriesAdded;
	}

	/*
	 * Helper method for creating a document transformer
	 */
	private static Transformer createTransformer(){
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
	
	/*
	 * Helper method for creating a document builder
	 */
	private  static DocumentBuilder createDocumentBuilder() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (final ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Method for creating a plugin.xml file in case it does not exist
	 */
	private static void createPluginFile(String project) {
		final DocumentBuilder builder = createDocumentBuilder();
		final String path = project + PLUGIN_FILE;
		Document doc = null;
		Element root;
		if (builder != null) {
			doc = builder.newDocument();
			root = doc.createElement(PLUGIN_TAG );
			doc.appendChild(root);
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
	
	/**
	 * Creates ModelSmellStub objects from the information provided in the target project's plugin.xml.
	 * 
	 * @param project - the target project
	 * @return Set of ModelSmellStub objects
	 */
	public static Set<ModelSmellStub> getSmellStubsFromPluginXML(IProject project){
		Set<ModelSmellStub> smellStubs = new HashSet<ModelSmellStub>();
		String fileName = PLUGIN_XML_FILE_NAME;
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
					if(extensionName.equals(EXTENSION_POINT_ID_MODELSMELL)){
						NodeList modelSmellExtensionNodes = node.getChildNodes();
						for(int j = 0; j < modelSmellExtensionNodes.getLength(); j++){
							Node attributeNode = modelSmellExtensionNodes.item(j);
							String nodeName = attributeNode.getNodeName();
							if(nodeName != MODELSMELL_TAG)
								continue;
							NamedNodeMap attributes = attributeNode.getAttributes();
							Node idNode = attributes.getNamedItem(MODELSMELL_ID_TAG);
							Node nameNode = attributes.getNamedItem(MODELSMELL_NAME_TAG);
							Node descNode = attributes.getNamedItem(MODELSMELL_DESCRIPTION_TAG);
							Node metamodelNode = attributes.getNamedItem(MODELSMELL_METAMODEL_TAG);
							smellStubs.add(new ModelSmellStub(
									idNode.getNodeValue(),
									nameNode.getNodeValue(),
									descNode.getNodeValue(),
									metamodelNode.getNodeValue()
									)
							);
						}
						
					}
				}	
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return smellStubs;
	}
	
	/**
	 * Creates ModelRefactoringStub objects from the information provided in the target project's plugin.xml.
	 * 
	 * @param project - the target project
	 * @return Set of ModelRefactoringStub objects
	 */
	public static Set<ModelRefactoringStub> getRefactoringStubsFromPluginXML(IProject project){
		Set<ModelRefactoringStub> refactoringStubs = new HashSet<ModelRefactoringStub>();
		String fileName = PLUGIN_XML_FILE_NAME;
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
					if(extensionName.equals(EXTENSION_POINT_ID_EMF_REFACTORING)){
						NodeList modelRefactoringExtensionNodes = node.getChildNodes();
						for(int j = 0; j < modelRefactoringExtensionNodes.getLength(); j++){
							Node attributeNode = modelRefactoringExtensionNodes.item(j);
							String nodeName = attributeNode.getNodeName();
							if(nodeName != REFACTORING_TAG)
								continue;
							NamedNodeMap attributes = attributeNode.getAttributes();
							Node idNode = attributes.getNamedItem("id");
							Node nameNode = attributes.getNamedItem("menulabel");
							Node namespaceNode = attributes.getNamedItem("namespaceUri");
							refactoringStubs.add(new ModelRefactoringStub(idNode.getNodeValue(),
									nameNode.getNodeValue(),
									namespaceNode.getNodeValue()
									)
							);
						}
					}
				
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return refactoringStubs;
	}
}
