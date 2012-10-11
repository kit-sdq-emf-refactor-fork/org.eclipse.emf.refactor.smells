package org.eclipse.emf.refactor.smells.configuration.ui;

import java.util.LinkedList;

import org.eclipse.emf.refactor.smells.core.ModelSmell;
import org.eclipse.swt.widgets.TableItem;

/**
 * Mapping for the property page.
 * 
 * @author Matthias Burhenne
 *
 */

public class TableItemsMapping {

	private TableItemMapping[] tableItems;

	/**
	 * Constructor to create a new <i>TableItemsMapping</i> object.
	 * 
	 * @param inputList
	 */
	public TableItemsMapping(LinkedList<ModelSmell> inputList) {
		LinkedList<TableItemMapping> tableItemsList = new LinkedList<TableItemMapping>();
		for (ModelSmell smell : inputList) {
			tableItemsList.add(new TableItemMapping(smell));
		}
		tableItems = new TableItemMapping[tableItemsList.size()];
		for (int i = 0; i < tableItemsList.size(); i++) {
			tableItems[i] = tableItemsList.get(i);
		}
	}

	public int getSize() {
		return tableItems.length;
	}

	/**
	 * Sets the mapping for a table item to a smell object
	 * 
	 * @param smell - the smell object that is supposed to be mapped
	 * @param item - the table item which is mapping to the smell
	 */
	public void setItemForSmell(ModelSmell smell, TableItem item) {
		int index = getTableItemIndex(smell);
		setItem(index, item);
	}

	public ModelSmell getModelSmell(int index){
		return tableItems[index].getModelSmell();
	}
	
	public TableItem getTableItem(int index) {
		return tableItems[index].getItem();
	}

	public TableItem getTableItem(ModelSmell smell) {
		return tableItems[getTableItemIndex(smell)].getItem();
	}

	private void setItem(int index, TableItem item) {
		tableItems[index].setItem(item);
	}

	private int getTableItemIndex(ModelSmell smell) {
		for (int i = 0; i < tableItems.length; i++) {
			if (tableItems[i].getModelSmell().equals(smell))
				return i;
		}
		return -1;
	}

	private class TableItemMapping {
		private TableItem item;
		private ModelSmell smell;

		private TableItemMapping(ModelSmell smell) {
			this.smell = smell;
			this.item = null;
		}

		private void setItem(TableItem item) {
			this.item = item;
		}

		private TableItem getItem() {
			return item;
		}

		private ModelSmell getModelSmell() {
			return smell;
		}

	}
}
