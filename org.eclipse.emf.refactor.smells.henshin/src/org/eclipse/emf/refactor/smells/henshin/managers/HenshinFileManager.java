package org.eclipse.emf.refactor.smells.henshin.managers;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.refactor.smells.managers.FileManager;

public class HenshinFileManager extends FileManager {
	
	private final static String HENSHIN_EXT = ".henshin";
	 
	public static File[] getAllHenshinFiles(String path) {
		File file = new File(path);
		FileFilter ff = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if (pathname.getName().endsWith(HENSHIN_EXT)) {
					return true;
				} else {
					return false;
				}
			}
		};
		return file.listFiles(ff);
	}
	
	public static EObject loadFile(String filename) {
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.getResource(URI.createFileURI(filename), true);
		return resource.getContents().get(0);
	}
}
