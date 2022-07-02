package com.Anix.GUI.Windows;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lwjgl.glfw.GLFW;

import com.Anix.Engine.Editor;
import com.Anix.GUI.UI;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.IO.ProjectSettings;
import com.Anix.Main.Core;
import com.Anix.Math.Color;
import com.Anix.Math.Vector2f;

public class MenuBar {
	/**
	 * Scene - Will display from a different position than the camera's position.<br>
	 * Game - Will display from the actual camera's position.<br>
	 */
	private enum ViewType {
		Scene, Game
	}
	
	private final int startX = 0, startY = 0;	
	private int width = 0, height = 25;
	private final float lineWidth = 1f, lineHeight = 1f;
	
	private Core core;
	
	public MenuBar(Core core) {
		this.core = core;
	}
	
	/**
	 * Scene - Will display from a different position than the camera's position.<br>
	 * Game - Will display from the actual camera's position.<br>
	 */
	public ViewType viewType = ViewType.Scene;
	
	public void update() {
		width = Application.getFullWidth();
		
		//Panel - Tool bar
		UI.drawButtonWithOutline(startX, startY, 0f, width, height, lineWidth, lineHeight, Color.gray, Color.black);
		
		if(UI.drawButton(startX + 8, startY + 2, -0.1f, 50, 20, "File", 7f, -0.5f, -0.1f, 0.5f, 0.5f, Color.black, Color.lightGray)
				&& Input.isMouseButtonDown(KeyCode.Mouse0)) {
			UI.addPopup(-0.2f, new Vector2f(130, 30), "FilePopup", Color.white, Color.black, new String[] {"Save", "New Project", "Export"}, this::Popup);
		}
		
		if(UI.drawButton(startX + 8 + 52, startY + 2, -0.1f, 50, 20, "Assets", 2, -0.5f, -0.1f, 0.5f, 0.5f, Color.black, Color.lightGray)
				&& Input.isMouseButtonDown(KeyCode.Mouse0)) {
			
		}
		
		if(Editor.isPlaying()) {
			viewType = ViewType.Game;
		} else {
			viewType = ViewType.Scene;
		}
		
		if(UI.drawButton(startX + ((width * 0.5f) - (50 * 1.5f) + (50 * 2)), startY + 2, -0.1f, 50, 20, "Game", 0, -0.5f, -0.1f, 0.5f, 0.5f, Color.black, (viewType == ViewType.Game ? Color.silver : Color.lightGray)) && Input.isMouseButtonDown(KeyCode.Mouse0)) {
			if(!Editor.isPlaying())
				core.getEditor().togglePlay();
		}
		
		if(UI.drawButton(startX + ((width * 0.5f) - (50 * 1.5f)), startY + 2, -0.1f, 50, 20, "Scene", 0, -0.5f, -0.1f, 0.5f, 0.5f, Color.black,
				(viewType == ViewType.Scene ? Color.silver : Color.lightGray)) && Input.isMouseButtonDown(KeyCode.Mouse0)) {
			if(Editor.isPlaying())
				core.getEditor().togglePlay();
		}
	}
	
	public void Popup(String f) {
		if(f.equalsIgnoreCase("save")) {
			core.getEditor().saveProject();
		} else if(f.equalsIgnoreCase("new project")) {
			String fullPath = System.getProperty("user.dir");
			
			File dirctory = new File(fullPath);
			File[] files = dirctory.listFiles();
			
			int engineIndex = -1;
			for(int i = 0; i < files.length; i++) {
				if(files[i].getName().equals("AnixLoader.jar")) {
					engineIndex = i;
				}
			}
			
			if(engineIndex == -1) {
				System.err.println("[ERROR] Couldn't find any versions of AnixLoader.");
				
				return;
			}
			
			try {
				Runtime.getRuntime().exec("cmd.exe /c cd \"" + fullPath + "\" & start cmd.exe /k \"java -cp AnixLoader.jar net.Anix.Main.Core");
				
				GLFW.glfwSetWindowShouldClose(Application.getWindow(), true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(f.equalsIgnoreCase("export")) {
			//https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html#available
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
					| UnsupportedLookAndFeelException e) {
				e.printStackTrace();
			}
			
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File("."));
			fc.setFileFilter(new FileNameExtensionFilter("Export Build", "jar"));
			fc.setFocusable(true);
			
			int result = fc.showSaveDialog(null);
			
			if(result == JFileChooser.APPROVE_OPTION) {
				File temp = fc.getSelectedFile();
				String path = temp.getAbsolutePath();
				
				if(!path.endsWith("jar")) {
					path += ".jar";
				}
				
				try {
					export(path);
				} catch(IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("No Selection ");
			}
		}
	}
	
	private void export(String path) throws IOException, URISyntaxException {
		File parentFile = new File(Core.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		parentFile.getParentFile().mkdirs();
		
		JarFile engineJar = new JarFile(parentFile);
		
		File f = new File(path);
		f.getParentFile().mkdirs();
		f.createNewFile();
		
		System.out.println("Exporting Dependencies...");
		Console.Log("[EXPORT] Exporting Dependencies...");
		
		JarOutputStream jos = new JarOutputStream(new FileOutputStream(f));
		Enumeration<JarEntry> entires = engineJar.entries();
		
		while(entires.hasMoreElements()) {
			JarEntry entry = entires.nextElement();
			InputStream is = engineJar.getInputStream(entry);
			
			jos.putNextEntry(new JarEntry(entry.getName()));
			writeBytes(is, jos);
		}
		
		Console.Log("[EXPORT] Successfully Exported Dependencies!");
		Console.Log("[EXPORT] Exporting Assets...");
		
		addAssets(path, jos);
		
		Console.Log("[EXPORT] Succesfully Exported Assets!");
		Console.Log("[EXPORT] Exporting Config Configuration...");
		
		jos.putNextEntry(new JarEntry("Project.Settings"));
		jos.write(("Name: " + ProjectSettings.gameName).getBytes());
		jos.write(("Game Size: ").getBytes());
		jos.closeEntry();
		
		Console.Log("[EXPORT] Successfully Exported Config Configuration!");
		System.out.println("Export Complete!");
		Console.Log("[EXPORT] Export complete!");
		
		jos.close();
		engineJar.close();
	}
	
	private void addAssets(String path, JarOutputStream jos) {
		System.out.println("Exporting to " + path + "..");
		
		String fullPath = System.getProperty("user.dir");
		String fileSeparator = System.getProperty("file.separator");
		
		writeFolder("Assets", new File(fullPath + fileSeparator + core.getProjectName() + fileSeparator + "Assets"), jos);
		writeFolder("Data", new File(fullPath + fileSeparator + core.getProjectName() + fileSeparator + "Data"), jos);
	}
	
	private void writeFolder(String parent, File f, JarOutputStream jos) {
		File[] files = f.listFiles();
		
		if(files == null) {
			return;
		}
		
		for(int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()) {
				System.out.println("Writing " + files[i].getName() + " as a folder..");
				writeFolder(parent + "\\" + files[i].getName(), files[i], jos);
				System.out.println("Successfully wrote " + files[i].getName() + " as a folder!");
				
				continue;
			}
			
			System.out.println("Writing " + files[i].getName() + "..");
			
			files[i].getParentFile().mkdirs();
			
			try {
				writeFile(parent, files[i], jos);
				System.out.println("Successfully wrote " + files[i].getName());
			} catch(FileNotFoundException e) {
				e.printStackTrace();
				System.out.println(files[i].getName() + " can't be found!");
			}
		}
	}
	
	private void writeFile(String parent, File f, JarOutputStream jos) throws FileNotFoundException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
		
		//String p = p;
		
		/*if(!f.getParentFile().getName().contains(parent)) {
			p = parent + "/" + f.getParentFile().getName();
		} else {
			p = f.getParentFile().getName();
		}*/
		
		try {
			jos.putNextEntry(new JarEntry(parent + "/" + f.getName()));
			writeBytes(in, jos);
		} catch(IOException e) {
			System.out.println(f.getName() + " could not write bytes!");
			
			e.printStackTrace();
		}
		
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeBytes(InputStream is, JarOutputStream jos) throws IOException {
		byte[] buffer = new byte[4096];
		int bytesRead = 0;
		
		while((bytesRead = is.read(buffer)) != -1) {
			jos.write(buffer, 0, bytesRead);
		}
		
		is.close();
		jos.flush();
		jos.closeEntry();
	}

	public int getStartX() {
		return startX;
	}

	public int getStartY() {
		return startY;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
