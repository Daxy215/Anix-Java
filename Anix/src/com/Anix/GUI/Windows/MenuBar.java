package com.Anix.GUI.Windows;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lwjgl.glfw.GLFW;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Engine.Editor;
import com.Anix.IO.Application;
import com.Anix.IO.ProjectSettings;
import com.Anix.Main.Core;
import com.Anix.Objects.GameObject;
import com.Anix.SceneManager.SceneManager;

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
	        
			//TODO: Center them.
	        //68 Button width - 88 idk :D
			ImGui.dummy(Application.getFullWidth() * 0.5f - (88*2) + (88*0.25f), 0);
			
	        if(ImGui.button("Play", 68, height)) {
	        	core.getEditor().setIsPlaying(true);
	        }
	        
	        if(ImGui.button("Stop playing", 68, height)) {
	        	core.getEditor().setIsPlaying(false);
	        }
	        
	        if(ImGui.button("Compile", 64, height)) {
	        	try {
					Editor.complier.compile();
				} catch (Exception e) {
					e.printStackTrace();
				}
	        	
	        	for(int i = 0; i < SceneManager.currentScene.getGameObjects().size(); i++) {
	        		GameObject obj = SceneManager.currentScene.getGameObjects().get(i);
	        		
	        		for(int j = obj.getBehaviours().size() - 1; j > 0; j--) {
	        			Behaviour b = obj.getBehaviours().get(j);
	        			
	        			obj.removeBehaviour(b);
	        			
	        			Behaviour nb = Editor.getBehaviour(b.getName());
	        			
	        			Field[] fields = b.getAllFields();
	        			for(int k = 0; k < fields.length; k++) {
	        				try {
	        					Field f = nb.getClass().getDeclaredField(fields[k].getName());
	        					f.setAccessible(true); // make private fields accessible
	        					Object value = fields[k].get(b);
	        					
	        					if(value == null)
	        						continue;
	        					
	        					copyProperty(nb, f.getName(), value);
	        					
	        					//Field f = nb.getClass().getField(fields[k].getName());
								//f.setAccessible(true);
								
								//copyProperty(nb, f.getName(), fields[k].get(obj));
								//f.set(nb, fields[k].get(obj));
							} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
								System.err.println("found: " + e.getMessage());
								//e.printStackTrace(System.err);
								continue;
							}
	        			}
	        			
	        			obj.addBehaviour(nb);
	        		}
	        	}
	        }
	        
	        ImGui.endMainMenuBar();
	    }
	}
	
	private static void copyProperty(Object dest, String propName, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
	    Class<?> clazz = dest.getClass();
	    Method setter = findSetter(clazz, propName, value.getClass());
	    if (setter != null) {
	        setter.invoke(dest, value);
	    } else {
	        Field field = findField(clazz, propName);
	        
	        if (field != null) {
	            field.setAccessible(true);
	            field.set(dest, value);
	        } else {
	            throw new NoSuchFieldException("Could not find property " + propName + " in class " + clazz.getName());
	        }
	    }
	}

	private static Method findSetter(Class<?> clazz, String propName, Class<?> valueType) {
	    String setterName = "set" + propName.substring(0, 1).toUpperCase() + propName.substring(1);
	    Method[] methods = clazz.getMethods();
	    for (Method method : methods) {
	        if (method.getName().equals(setterName) && method.getParameterCount() == 1 && method.getParameterTypes()[0].isAssignableFrom(valueType)) {
	            return method;
	        }
	    }
	    return null;
	}
	
	private static Field findField(Class<?> clazz, String propName) {
	    try {
	        return clazz.getDeclaredField(propName);
	    } catch (NoSuchFieldException e) {
	        Class<?> superClass = clazz.getSuperclass();
	        
	        if (superClass != null) {
	            return findField(superClass, propName);
	        } else {
	            return null;
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
	
	private static void copyFilesToTempDir(File tempDir) throws IOException {
        // Copy your files to the temporary directory
        // You can use Java's file copy methods or any other preferred method
        // For example:
        File sourceFile = new File("path/to/source/file");
        File destFile = new File(System.getProperty("java.io.tmpdir"), "destination_file");
        Files.copy(sourceFile.toPath(), destFile.toPath());
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
	
	private static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                deleteDirectory(file);
            }
        }
        dir.delete();
    }
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
