package com.Anix.GUI.Windows;

import static org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_inputBox;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.Anix.Engine.Editor;
import com.Anix.Engine.Graphics.Material;
import com.Anix.GUI.GUI;
import com.Anix.GUI.Texture;
import com.Anix.GUI.UI;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Main.Core;
import com.Anix.Math.Color;
import com.Anix.SceneManager.SceneManager;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.type.ImString;

public final class Assets {
	public static class Folder extends File {
		private static final long serialVersionUID = 5698977623695708210L;

		private Folder parentFolder = null;
		private Texture texture;
		
		public List<Folder> subFolders = new ArrayList<Folder>();
		
		public Folder(String absolutePath, Texture texture) {
			super(absolutePath);
			
			this.texture = texture;
		}
		
		public Folder getParentFolder() {
			return parentFolder;
		}
		
		public void setParentFolder(Folder parentFolder) {
			this.parentFolder = parentFolder;
		}
		
		public Texture getTexture() {
			return texture;
		}
	}
	
	private int startX = 0, startY = 0;
	private int width = 0, height = 250;
	
	private Folder inFolder, selectedFolder;
	private Core core;
	
	private List<Folder> folders;
	
	public Assets(Core core) {
		this.core = core;
		
		folders = new ArrayList<Folder>();
	}
	
	private String popup = "";
	private boolean openNewPopup;
	private ImString folderName = new ImString("", 256);
	
	public void render() {
		startY = Application.getHeight();

		ImGui.setNextWindowPos(startX, startY);
		ImGui.setNextWindowSize(Application.getFullWidth(), height);

		ImGui.begin("##", GUI.defaultFlags);
		
		if(inFolder != null) {
			if(ImGui.button(inFolder.getAbsolutePath())) {
				inFolder = inFolder.parentFolder;
			}
		}
		
		if(inFolder == null)
			drawFolders(folders);
		else
			drawFolders(inFolder.subFolders);
		
		if(ImGui.getIO().getWantCaptureMouse() && ImGui.isWindowHovered()) {
			//TODO: Check if any of the gameObjects were hovered.
			if(ImGui.isMouseClicked(1)) {
				ImGui.openPopup("AssetsOptions");
			}
		}
		
		//"New Folder", "New Script", "New Material", "New Shader", "New Scene"
		if (ImGui.beginPopup("AssetsOptions")) {
			if (ImGui.menuItem("New Folder")) {
				core.getEditor().addFolder("New Folder", inFolder);
			}
			
			if (ImGui.menuItem("New Script")) {
				//Folder f = core.getEditor().addFolder("test.java", inFolder);
				
				//createScript(f);
				ImGui.closeCurrentPopup();
				
				popup = "script";
				openNewPopup = true;
				
			}
			
			if (ImGui.menuItem("New Material")) {
				ImGui.closeCurrentPopup();
				
				popup = "material";
				openNewPopup = true;
			}
			
			if (ImGui.menuItem("New Shader")) {
				ImGui.closeCurrentPopup();
				
				popup = "shader";
				openNewPopup = true;
			}
			
			if (ImGui.menuItem("New Scene")) {
				ImGui.closeCurrentPopup();
				
				popup = "scene";
				openNewPopup = true;
			}
			
			ImGui.endPopup();
	    }
		
		if(openNewPopup) {
			ImGui.openPopup(popup);
			
			openNewPopup = false;
		}
				
		if(ImGui.beginPopup(popup)) {
			ImGui.inputText("##", folderName);
			
			if(Input.isKeyDown(KeyCode.Return)) {
				if(folderName.get().length() > 0) {
					switch(popup) {
					case "script":
						Folder f = core.getEditor().addFolder(folderName.get() + ".java", inFolder);
						
						createScript(f);
						
						break;
					default:
						System.err.println("Couldn't find a popup name of " + popup);
						
						break;
					}
				}
				
				ImGui.closeCurrentPopup();
			}
			
			ImGui.endPopup();
		}
		
		ImGui.end();
	}

	//Used for text calculation - A class variable to safe memory.
	private ImVec2 vec2 = new ImVec2();
	private ImString s = new ImString("", 187);
	
	private void drawFolders(List<Folder> folders) {
		int width = 90;
		int amount = Application.getFullWidth() / width;
		
		ImGui.columns(amount, "##", false);
		
		for(int i = 0; i < folders.size(); i++) {
			Folder folder = folders.get(i);
			
			ImGui.setColumnWidth(ImGui.getColumnIndex(), width);
			
			if(ImGui.imageButton(folder.getTexture().getId(), 
					folder.getTexture().getWidth(), folder.getTexture().getHeight())) {
				
			}
			
	        if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(0)) {
	        	if(folder.isDirectory()) {
	        		inFolder = folder;
	        	}
	        }
			
			ImGui.sameLine();
			ImGui.calcTextSize(vec2, folder.getName());
			
			//Move to under of the folder, for it's name.
			ImGui.setCursorPos(ImGui.getCursorPosX() - folder.getTexture().getWidth() - 20, ImGui.getCursorPosY() + folders.get(0).getTexture().getHeight() + 5);
			
			ImGui.pushID(s.get());
			
			s.set(folder.getName());
			ImGui.pushItemWidth(100);
			if(ImGui.inputText("##", s)) {
				if(s.get().length() > 0) {
					//folder.renameTo(new File(folder.getParent() + "\\" + s.get()));
					Path source = Paths.get(folder.getAbsolutePath());
					
					try {
						source = Files.move(source, source.resolveSibling(s.get()));
					} catch (IOException e) {
						e.printStackTrace(System.err);
					}
					
					folders.set(i, new Folder(source.toString(), folder.getTexture()));
				}
			}
			
			ImGui.popItemWidth();
			ImGui.popID();
			
			//Move back, for padding.
			ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY() - 50);
			
			ImGui.nextColumn();
		}
		
		ImGui.columns(1);
	}

	@SuppressWarnings("unused")
	private void drawFolder(Folder folder, float x, float y) {
		if(folder.equals(core.getDraggedObject())) {
			x = (float)Input.getMouseX();
			y = (float)Input.getMouseY();
		}
		
		String name = folder.getName();
		float txtSize = 0.50f;
		
		if(name.length() > 7) {
			name = name.substring(0, 7);
			txtSize = 0.40f;
			name += "..";
		}
		
		if(UI.drawButton(x, y, -0.2f, 64, 64, name,4, 60, -0.2f, txtSize, txtSize,
				 folder.equals(selectedFolder) ? Color.cyan : Color.black, Color.white,
				folder.getTexture())/* && !Editor.isPlaying()*/) {
			if(core.getDraggedObject() == null && Input.isMouseButton(KeyCode.Mouse0) && Input.isDragging()) {
				File f = new File(folder.getAbsolutePath());
				
				if(f.isFile()) {
					core.setDraggedObject(folder);
				}
			}
			
			if(folder.equals(selectedFolder) && Input.doubleClicked()) {
				selectedFolder = null;
				
				File f = new File(folder.getAbsolutePath());
				
				if(f.isDirectory()) {
					inFolder = folder;
				} else {
					if(folder.getName().endsWith(".scene")) {
						core.getEditor().saveProject();
						
						String sceneName = folder.getName().substring(0, folder.getName().length() - 6); //6 - .scene
						
						SceneManager.loadScene(sceneName);
					} else {
						if(Desktop.isDesktopSupported()) {
							try {
								Desktop.getDesktop().edit(new File(folder.getAbsolutePath()));
							} catch (Exception e) {
								if(e.getMessage().contains("No application is associated with the specified file for this operation")) {
									//TODO: Make error log
									System.err.println("[ERROR] Couldn't find an application to open this type of file; " + folder.getName());
								} else {
									System.err.println(e.getMessage());
								}
							}
						} else {
							//TODO: Make error log
							System.err.println("[ERROR] Desktop is not supported! This game engine is made for Windows only!");
						}
					}
				}
			} else if(!Input.isDragging() && Input.isMouseButtonUp(KeyCode.Mouse0)) {
				selectedFolder = folder;
				
				//if(folder.getAbsolutePath().endsWith(".java")) {
					core.getGUI().getHierachy().setSelectedObject(null);
					core.setLastSelectedObject(folder);
				//}
			}
		}
	}
	
	public void Popup(String s) {
		if(s.equals("New Folder")) {
			String folderName = tinyfd_inputBox("Folder name!", "What would you like to name the folder?", "");
			
			if(folderName == null)
				return;
			
			core.getEditor().addFolder(folderName, inFolder);
		} else if(s.equals("New Script")) {
			String folderName = tinyfd_inputBox("Script name!", "What would you like to name the Script?", "");
			
			if(folderName == null)
				return;
			
			Folder f = core.getEditor().addFolder(folderName + ".java", inFolder);
			
			createScript(f);
		} else if(s.equals("New Material")) {
			String folderName = tinyfd_inputBox("Material name!", "What would you like to name the Material?", "");
			
			if(folderName == null)
				return;
			
			Folder f = core.getEditor().addFolder(folderName + ".material", inFolder);
			
			createMaterial(f);
		} else if(s.equals("New Shader")) {
			String folderName = tinyfd_inputBox("Shader name!", "What would you like to name the Shader?", "");
			
			if(folderName == null)
				return;
			
			Folder f = core.getEditor().addFolder(folderName + ".glsl", inFolder);
			
			createShader(f);
		} else if(s.equals("New Scene")) {
			String folderName = tinyfd_inputBox("Scene name!", "What would you like to name the Scene?", "");
			
			if(folderName == null) {
				return;
			}
			
			core.getEditor().addFolder(folderName + ".scene", inFolder);
		}
	}
	
	public Folder getFolder(String name) {
		for(int i = 0; i < folders.size(); i++) {
			if(folders.get(i).getName().equals(name)) {
				return folders.get(i);
			}
		}
		
		return null;
	}
	
	public Folder getFolderUsingPath(String path) {
		for(int i = 0; i < folders.size(); i++) {
			System.err.println("Found: " + folders.get(i).getAbsolutePath());
			if(folders.get(i).getAbsolutePath().equals(path)) {
				return folders.get(i);
			} else {
				for(int j = 0; j < folders.get(i).subFolders.size(); j++) {
					System.err.println("Found2: " + folders.get(i).subFolders.get(j).getAbsolutePath());
					if(folders.get(i).subFolders.get(j).getAbsolutePath().equals(path)) {
						return folders.get(i).subFolders.get(j);
					}
				}
			}
		}
		
		return null;
	}
	
	private void createScript(Folder folder) {
		String name = folder.getName();
		
		//To remove .java
		name = name.contains(".") ? name.split("\\.")[0] : name;
		
		String script = "import com.Anix.Behaviours.Behaviour;\r\n"
				+ "\r\n"
				+ "public class " + name + " extends Behaviour {\r\n"
				+ "	/*\r\n"
				+ "	* Please ignore this tyvm :)\r\n"
				+ "	*/\r\n"
				+ "	private static final long serialVersionUID = 1L;"
				+ "	\r\n"
				+ "	\r\n"
				+ "	@Override\r\n"
				+ "	//Will be called on game start\r\n"
				+ "	public void start() {\r\n"
				+ "		//TODO: Code here..\r\n"
				+ "	}\r\n"
				+ "	\r\n"
				+ "	@Override\r\n"
				+ "	//Will be called once per tick\r\n"
				+ "	public void update() {\r\n"
				+ "		//TODO: Code here..\r\n"
				+ "	}\r\n"
				+ "}";
		
		try {
			FileWriter fw = new FileWriter(folder.getAbsolutePath());
			fw.write(script);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void createMaterial(Folder folder) {
		try {
			Editor.writeObjectToFile(new Material(), new ObjectOutputStream(new FileOutputStream(folder.getAbsolutePath(), false)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void createShader(Folder folder) {
		String shader =
				"#version 460 core\r\n"
				+ "\r\n"
				+ "//Vertex Shader\r\n"
				+ "\r\n"
				+ "layout(location = 0) in vec3 position;\r\n"
				+ "layout(location = 1) in vec2 textureCoord;\r\n"
				+ "\r\n"
				+ "out vec2 passTextureCoord;\r\n"
				+ "\r\n"
				+ "uniform mat4 model;\r\n"
				+ "uniform mat4 view;\r\n"
				+ "uniform mat4 projection;\r\n"
				+ "\r\n"
				+ "void main() {\r\n"
				+ "	gl_Position = projection * view * model * vec4(position, 1.0);\r\n"
				+ "	passTextureCoord = textureCoord;\r\n"
				+ "}\r\n"
				+ "\r\n"
				+ "#version 330 core\r\n"
				+ "\r\n"
				+ "//Fragment Shader\r\n"
				+ "\r\n"
				+ "in vec2 passTextureCoord;\r\n"
				+ "\r\n"
				+ "out vec4 outColor;\r\n"
				+ "\r\n"
				+ "uniform vec3 color;\r\n"
				+ "uniform sampler2D tex;\r\n"
				+ "\r\n"
				+ "void main() {\r\n"
				+ "	outColor = texture(tex, passTextureCoord) * vec4(color.xyz, 1);\r\n"
				+ "}";
		
		try {
			FileWriter fw = new FileWriter(folder.getAbsolutePath());
			fw.write(shader);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static File createTempFile(String path) {
	    File temp = new File(path);
	    
	    if (temp.exists()) {
	        temp.delete();
	    }

	    try {
	        temp.createNewFile();
	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }

	    return temp;
	}
	
	public static File createTempDirectory(String fileName) {
	    File parent = new File(System.getProperty("java.io.tmpdir"));   

	    File temp = new File(parent, fileName);

	    if (temp.exists()) {
	        temp.delete();
	    }

	    temp.mkdir();

	    return temp;
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

	public Folder getInFolder() {
		return inFolder;
	}
	
	public Folder getSelectedFolder() {
		return selectedFolder;
	}
	
	public List<Folder> getFolders() {
		return folders;
	}
}
