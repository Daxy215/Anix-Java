package com.Anix.GUI.Windows;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.Anix.Engine.Editor;
import com.Anix.Engine.Graphics.Material;
import com.Anix.GUI.GUI;
import com.Anix.GUI.Texture;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Main.Core;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiWindowFlags;
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
		
		public String getExtension() {
			String name = getName();
			
			return name.substring(name.lastIndexOf(".") + 1);
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
		
		height = 250 - (core.getGUI().getAssetsMenuBar().getHeight() * 2);
	}
	
	private String popup = "";
	private boolean openNewPopup;
	private ImString folderName = new ImString("", 256);
	
	public void render() {
		startY = Application.getHeight() + (core.getGUI().getAssetsMenuBar().getHeight() * 2);
		
		ImGui.setNextWindowPos(startX, startY);
		ImGui.setNextWindowSize(Application.getFullWidth(), height);
		//ImGui.setNextWindowSizeConstraints(-1.0f, 250/*Min height*/, -1.0f, Application.getFullHeight() - core.getGUI().getMenuBar().getHeight() - /*Distance between screens - Padding*/ 20);
		
		ImGui.begin("##Assets", GUI.defaultFlags | ImGuiWindowFlags.NoDecoration);
		
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
		
		if (ImGui.beginPopup("AssetsOptions")) {
			if (ImGui.menuItem("New Folder")) {
				
				ImGui.closeCurrentPopup();
				
				popup = "Folder";
				openNewPopup = true;
			}
			
			if (ImGui.menuItem("New Script")) {
				ImGui.closeCurrentPopup();
				
				popup = "Script";
				openNewPopup = true;
				
			}
			
			if (ImGui.menuItem("New Material")) {
				ImGui.closeCurrentPopup();
				
				popup = "Material";
				openNewPopup = true;
			}
			
			if (ImGui.menuItem("New Shader")) {
				ImGui.closeCurrentPopup();
				
				popup = "Shader";
				openNewPopup = true;
			}
			
			if (ImGui.menuItem("New Scene")) {
				ImGui.closeCurrentPopup();
				
				popup = "Scene";
				openNewPopup = true;
			}
			
			ImGui.endPopup();
	    }
		
		if(openNewPopup) {
			ImGui.openPopup(popup);
			
			openNewPopup = false;
		}
		
		if(ImGui.beginPopup(popup)) {
			//ImGui.text(popup + " name");
			
			//folderName.set(popup);
			ImGui.inputTextWithHint("##", popup + " name..", folderName);
			
			if(Input.isKeyDown(KeyCode.Return)) {
				if(folderName.get().length() > 0) {
					switch(popup.toLowerCase()) {
					case "folder":
						core.getEditor().addFolder(folderName.get(), inFolder);
						
						break;
					case "script":
						Folder f = core.getEditor().addFolder(folderName.get() + ".java", inFolder);
						
						createScript(f);
						
						break;
					case "material":
						Folder f2 = core.getEditor().addFolder(folderName.get() + ".material", inFolder);
						
						createMaterial(f2);
						
						break;
					case "shader":
						Folder f3 = core.getEditor().addFolder(folderName.get() + ".glsl", inFolder);
						
						createShader(f3);
						
						break;
					case "scene":
						core.getEditor().addFolder(folderName.get() + ".scene", inFolder);
						
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
		
		//height = (int) ImGui.getWindowHeight();
		
		ImGui.end();
	}
	
	//Used for text calculation - A class variable to save memory.
	private ImVec2 vec2 = new ImVec2();
	private ImString s = new ImString("", 187);
	private float folderWidth = 64, folderHeight = 64;
	
	private void drawFolders(List<Folder> folders) {
		int width = 90;
		int amount = Application.getFullWidth() / width;
		
		ImGui.columns(amount, "##", false);
		
		for(int i = 0; i < folders.size(); i++) {
			Folder folder = folders.get(i);
			
			ImGui.setColumnWidth(ImGui.getColumnIndex(), width);
			
			ImGui.pushID(s.get());
			
			//If folder was pressed - There is no use for it atm.
			if(ImGui.imageButton(folder.getTexture().getId(), 
					folderWidth, folderHeight)) {}
			
	        if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(0)) {
	        	if(folder.isDirectory()) {
	        		inFolder = folder;
	        	} else { //Open the file
	        		if(!folder.getName().split("\\.")[1].equalsIgnoreCase("scene")) {
		        		if(Desktop.isDesktopSupported()) {
		        			try {
								Desktop.getDesktop().open(folder);
							} catch (IOException e) {
								e.printStackTrace();
							}
		        		}
	        		}
	        	}
	        }
			
			ImGui.sameLine();
			ImGui.calcTextSize(vec2, folder.getName());
			
			//Move to under of the folder, for it's name.
			ImGui.setCursorPos(ImGui.getCursorPosX() - folderWidth - 20, ImGui.getCursorPosY() + folderHeight + 5);
			
			//Setting the text input name
			s.set(folder.getName());
			
			//Text input size
			ImGui.pushItemWidth(80);
			
			if(ImGui.inputText("##", s)) {
				if(s.get().length() > 0) {
					Path source = Paths.get(folder.getAbsolutePath());
					
					try {
						source = Files.move(source, source.resolveSibling(s.get()));
					} catch (IOException e) {
						e.printStackTrace(System.err);
					}
					
					folders.set(i, new Folder(source.toString(), folder.getTexture()));
				}
			}
			
			if(ImGui.beginDragDropTarget()) {
				Object o = ImGui.acceptDragDropPayload("Folder", ImGuiDragDropFlags.None);
				
				if(o != null) {
					System.err.println("AOYOYOYOYOO " + (o.toString()));
					if(core.getDraggedObject() instanceof Folder) {
						core.setDraggedObject(null);
					}
				}
				
				ImGui.endDragDropTarget();
			}
			
			int src_flags = ImGuiDragDropFlags.SourceNoDisableHover; // Keep the source displayed as hovered
			src_flags |= ImGuiDragDropFlags.SourceNoHoldToOpenOthers; // Because our dragging is local, we disable the feature of opening foreign treenodes/tabs while dragging
			//src_flags |= ImGuiDragDropFlags.SourceNoPreviewTooltip; // Hide the tooltip
			
			if(ImGui.beginDragDropSource(src_flags)) {
				//??
				//if(!(src_flags & ImGuiDragDropFlags.SourceNoPreviewTooltip)) {
				ImGui.text(folder.getName());
				//}
				
				ImGui.setDragDropPayload("Folder", folder.getName());
				core.setDraggedObject(folder);
				
				ImGui.endDragDropSource();
			}
			
			ImGui.popItemWidth();
			ImGui.popID();
			
			//Move back, for padding.
			ImGui.setCursorPos(ImGui.getCursorPosX(), ImGui.getCursorPosY() - 50);
			
			ImGui.nextColumn();
		}
		
		ImGui.columns(1);
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
	
	public static boolean isValidPath(String path) {
	    try {
	        Paths.get(path);
	    } catch (InvalidPathException | NullPointerException ex) {
	        return false;
	    }
	    
	    return true;
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
