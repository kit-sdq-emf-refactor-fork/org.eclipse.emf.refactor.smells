package org.eclipse.emf.refactor.smells.henshin.managers;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.interpreter.EGraph;
import org.eclipse.emf.henshin.interpreter.Engine;
import org.eclipse.emf.henshin.interpreter.Match;
import org.eclipse.emf.henshin.interpreter.RuleApplication;
import org.eclipse.emf.henshin.interpreter.impl.EGraphImpl;
import org.eclipse.emf.henshin.interpreter.impl.EngineImpl;
import org.eclipse.emf.henshin.interpreter.impl.RuleApplicationImpl;
import org.eclipse.emf.henshin.interpreter.util.InterpreterUtil;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;

public class HenshinRuntimeManager {

	private final static String MAIN_RULE = "mainRule";

	public static LinkedList<LinkedList<EObject>> run(EObject rootElement, String henshinFilePath) {		
		LinkedList<LinkedList<EObject>> result = new LinkedList<LinkedList<EObject>>();
		EObject root = rootElement;
		Module module = 
				(Module) HenshinFileManager.loadFile(henshinFilePath);			
		EGraph graph = new EGraphImpl();
		graph.addTree(root);		
		Engine engine = new EngineImpl();
		Rule rule = (Rule) module.getUnit(MAIN_RULE);
		
		RuleApplication application = new RuleApplicationImpl(engine, graph, rule, null);
		
		List<Match> matches = InterpreterUtil.findAllMatches(engine,
				application.getRule(), application.getEGraph(),
				application.getPartialMatch());
		
		for (Match match : matches) {
			LinkedList<EObject> eObjects = new LinkedList<EObject>();
			eObjects.addAll(match.getNodeTargets());
			result.add(eObjects);
		}
		
		return  result;
	}
	
	public static boolean isModelSmellFile(String path){
		Module module = (Module) HenshinFileManager.loadFile(path);
		Rule rule = (Rule) module.getUnit(MAIN_RULE);
		return  (rule != null);
	}
}

