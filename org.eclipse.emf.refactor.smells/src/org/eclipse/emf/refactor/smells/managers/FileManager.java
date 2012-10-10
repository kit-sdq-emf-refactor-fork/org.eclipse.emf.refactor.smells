package org.eclipse.emf.refactor.smells.managers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Helper class for the access to the file system needed by EMF Smells.
 * 
 * @author Pawel Stepien
 *
 */

public class FileManager {
	
	/**
	 * Copies the target file to the new location giving it the new name.
	 * @param target - path of the file to be copied
	 * @param destination - target path of the copy process
	 * @param name - new name for the target file
	 */
	public static void copyFile(String target, String destination, String name) {
		File file = new File(destination);
		if (!file.exists()) {
			file.mkdirs();
		}
		File newFile = new File(destination + name);
		try {
			newFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		copy(target, destination + name);
	}

	/**
	 * Copies the file on the given path to the new path
	 * @param filePath - path of the file to be copied
	 * @param newFilePath - target path the file is copied to
	 */
	public static void copy(String filePath, String newFilePath) {
		try {
			BufferedInputStream is = new BufferedInputStream(
					new FileInputStream(filePath));
			new File(newFilePath).createNewFile();
			BufferedOutputStream os = new BufferedOutputStream(
					new FileOutputStream(newFilePath));
			int b;
			while ((b = is.read()) != -1) {
				os.write(b);
			}
			is.close();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
