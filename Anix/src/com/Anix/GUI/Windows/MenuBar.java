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

import com.Anix.IO.Application;
import com.Anix.IO.ProjectSettings;
import com.Anix.Main.Core;

import imgui.ImGui;
import imgui.flag.ImGuiStyleVar;

public class MenuBar {
	private int width = 0, height = 25;
	
	private Core core;
	
	public MenuBar(Core core) {
		this.core = core;
	}
	
	public void render() {
		ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, Application.getFullWidth(), 6);
		if (ImGui.beginMainMenuBar()) {
	        if (ImGui.beginMenu("File")) {
	        	if(ImGui.menuItem("Save")) {
	    			core.getEditor().saveProject();
	        	}
	        	
	        	if(ImGui.menuItem("New Project")) {
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
	    				Runtime.getRuntime().exec(new String[] { "cmd.exe /c cd \"" + fullPath + "\" & start cmd.exe /k \"java -cp AnixLoader.jar net.Anix.Main.Core" });
	    				
	    				GLFW.glfwSetWindowShouldClose(Application.getWindow(), true);
	    			} catch (IOException e) {
	    				e.printStackTrace(System.err);
	    			}
	        	}
	        	
	        	if(ImGui.menuItem("Export")) {
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
	        	
	            ImGui.endMenu();
	        }
	        
			ImGui.popStyleVar();
	        
	        //68 Button width - 88 idk :D
			ImGui.dummy(Application.getFullWidth() * 0.5f - (88*2) + (88*0.25f), 0);
			
	        if(ImGui.button("Play", 68, height)) {
	        	core.getEditor().setIsPlaying(true);
	        }
	        
	        if(ImGui.button("Stop playing", 68, height)) {
	        	core.getEditor().setIsPlaying(false);
	        }
	        
	        ImGui.endMainMenuBar();
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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
