package org.eclipse.emf.refactor.smells.runtime.ui;

import java.util.LinkedList;

import org.eclipse.emf.refactor.smells.runtime.core.EObjectGroup;
import org.eclipse.emf.refactor.smells.runtime.core.ModelSmellResult;
import org.eclipse.emf.refactor.smells.runtime.core.ResultModel;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class ResultModelTreeViewerContentProvider implements
		ITreeContentProvider {

	private static Object[] EMPTY_ARRAY = new Object[0];
	
	@Override
	public void dispose() { }

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof LinkedList<?>){
			return ((LinkedList<ResultModel>) parentElement).toArray();
		}
		if(parentElement instanceof ResultModel)
			return sortModelSmellResultArray(((ResultModel)parentElement).getModelSmellResults().toArray());
		if(parentElement instanceof ModelSmellResult)
			return ((ModelSmellResult)parentElement).getEObjectGroups().toArray();
		if(parentElement instanceof EObjectGroup)
			return ((EObjectGroup)parentElement).getEObjects().toArray();
		else
			return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}
	
	private Object[] sortModelSmellResultArray(Object[] resultArray){
		Object[] sortedResults = resultArray;
		for(int i = 0; i < sortedResults.length; i++){
			for(int j = 0; j < sortedResults.length; j++){
				if(((ModelSmellResult) sortedResults[i]).getEObjectGroups().size() > ((ModelSmellResult) sortedResults[j]).getEObjectGroups().size()){
					ModelSmellResult temp = (ModelSmellResult) sortedResults[i];
					sortedResults[i] = sortedResults[j];
					sortedResults[j] = temp;
				}
			}
		}
		return sortedResults;
		
	}

}
