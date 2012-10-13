package org.eclipse.emf.refactor.smells.henshin.managers;

import java.io.File;
import java.io.FileFilter;

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
}
