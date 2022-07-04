package com.Anix.GUI.Windows;

import static org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_inputBox;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.Anix.Engine.Editor;
import com.Anix.Engine.Graphics.Material;
import com.Anix.GUI.Texture;
import com.Anix.GUI.UI;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Main.Core;
import com.Anix.Math.Color;
import com.Anix.Math.Vector2f;
import com.Anix.Objects.GameObject;
import com.Anix.SceneManager.SceneManager;

public final class Assets {
	public static class Folder {
		private String name, absolutePath;
		
		private Folder parentFolder = null;
		private Texture texture;

		public List<Folder> subFolders = new ArrayList<Folder>();

		public Folder(String name, String absolutePath, Texture texture) {
			this.name = name;
			this.absolutePath = absolutePath;
			this.texture = texture;
		}

		public String getName() {
			return name;
		}

		public String getAbsolutePath() {
			return absolutePath;
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
	
	private final int startX = 0, startY = 0;
	private int width = 0, height = 250;
	private final float lineWidth = 1f, lineHeight = 1;
	
	private boolean canBeRemoved;
	
	private Folder inFolder, selectedFolder;
	private Core core;
	
	private List<Folder> folders;
	
	public Assets(Core core) {
		this.core = core;
		
		folders = new ArrayList<Folder>();
	}
	
	public void render() {
		width = Application.getFullWidth();
		
		//Panel
		if(UI.drawButtonWithOutline(startX, startY + Application.getFullHeight() - height, -0.1f, width, height, lineWidth, lineHeight,
				Color.gray, Color.black)/* && !Editor.isPlaying()*/) {
			if(Input.isMouseButtonDown(KeyCode.Mouse1)) {
				UI.addPopup(-0.2f, new Vector2f(130, 30), "Pro", Color.white, Color.black, new String[] {"New Folder", "New Script", "New Material", "New Shader", "New Scene"}, this::Popup);
			}
			
			//If dragged a gameObject into the assets panel.
			if(core.getDraggedObject() != null && core.getDraggedObject() instanceof GameObject && Input.isMouseButtonUp(KeyCode.Mouse0)) {
				String path = "";
				
				if(inFolder != null) {
					if(inFolder.parentFolder != null) {
						path = inFolder.parentFolder.getAbsolutePath() + "\\";
					} else {
						path = inFolder.getAbsolutePath() + "\\";
					}
				} else {
					String fullPath = System.getProperty("user.dir");
					String fileSeparator = System.getProperty("file.separator");
					
					path = fullPath + fileSeparator + core.getProjectName() + fileSeparator + "Assets" + fileSeparator;
				}
				
				core.getEditor().addFolder(((GameObject)core.getDraggedObject()).getName() + ".gameobject", inFolder);
				
				File file = new File(path + "\\" + ((GameObject)core.getDraggedObject()).getName() + ".gameobject");
				file.getParentFile().mkdirs();
				
				if(!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				try {
					FileOutputStream fos = new FileOutputStream(file, false);
					ObjectOutputStream stream = new ObjectOutputStream(fos);
					
					core.getEditor().writeGameObject(((GameObject)core.getDraggedObject()), stream);
					
					stream.flush();
					stream.close();
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				core.setDraggedObject(null);
			}
		} else {
			if(core.getDraggedObject() != null && Input.isMouseButtonUp(KeyCode.Mouse0)) {
				core.setDraggedObject(null);
			}
		}
		
		if(inFolder != null) {
			String path = inFolder.getName();
			
			Folder curFolder = inFolder;
			
			while(curFolder != null) {
				path += "/" + curFolder.getName();
				
				curFolder = curFolder.parentFolder;
			}
			
			String[] fullPath = path.split("/");
			
			path = "";
			
			for(int i = fullPath.length - 1; i > 0; i--) {
				path += "/" + fullPath[i];
			}
			
			//Selected folder path
			if(UI.drawButton(startX, startY + Application.getFullHeight() - height + (lineHeight * 2) + (core.getGUI().getAssetsMenuBar().getHeight()), -0.2f,
					width, 15, path, 0, -(UI.getFontMatrics().getHeight() * 0.18f), -0.1f, 0.5f, 0.5f, Color.black, Color.lightGray)
					&& Input.isMouseButtonDown(KeyCode.Mouse0)) {
				if(inFolder != null) {
					if(inFolder.parentFolder != null) {
						inFolder = inFolder.parentFolder;
					} else {
						inFolder = null;
					}
				}
			}
		}
		
		if(inFolder == null) {
			for(int i = 0; i < folders.size(); i++) {
				drawFolder(folders.get(i), startX + (lineWidth * 2) + i * 68, startY + Application.getFullHeight() - (height - 20) + (core.getGUI().getAssetsMenuBar().getHeight() * 1.5f));
			}
		} else {
			for(int i = 0; i < inFolder.subFolders.size(); i++) {
				drawFolder(inFolder.subFolders.get(i), startX + (lineWidth * 2) + i * 68, Application.getFullHeight() - (height - 20) + (core.getGUI().getAssetsMenuBar().getHeight() * 1.5f));
			}
		}
		
		if(core.getDraggedObject() instanceof Folder && Input.isMouseButtonUp(KeyCode.Mouse0)) {
			core.setDraggedObject(null);
		}
	}
	
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
		String name = "";
		
		if(folder.getAbsolutePath().contains("/")) {
			folder.absolutePath = folder.getAbsolutePath().replace("/", "\\");
		}
		
		if(folder.getAbsolutePath().contains("\\")) {
			String[] fullName = folder.getAbsolutePath().split("\\\\");
			
			name = fullName[fullName.length - 1];
		} else {
			name = folder.getAbsolutePath();
		}
		
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

	public boolean canBeRemoved() {
		return canBeRemoved;
	}

	public List<Folder> getFolders() {
		return folders;
	}
}
