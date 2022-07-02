package com.Anix.GUI.Windows;

import static org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_inputBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import com.Anix.Behaviours.Camera;
import com.Anix.Behaviours.LightSource;
import com.Anix.Behaviours.MeshRenderer;
import com.Anix.Behaviours.SpriteRenderer;
import com.Anix.Engine.Editor;
import com.Anix.GUI.Texture;
import com.Anix.GUI.UI;
import com.Anix.GUI.UI.Toggle;
import com.Anix.GUI.Windows.Assets.Folder;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Main.Core;
import com.Anix.Math.Color;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;
import com.Anix.SceneManager.Scene;
import com.Anix.SceneManager.SceneManager;

public final class Hierachy {
	private int index = 0, previousToggleIndex;
	private int scrollX;
	
	private final int startX = 0, startY = 50;
	private int width = 250, height, buttonHeight = 25;
	private final float lineWidth = 1f, lineHeight = 1f;
	
	private long lastScroll;
	private boolean clicked = false;
	private Texture on, off;
	
	private GameObject previousObject;
	private static GameObject selectedObject;
	
	private Core core;
	
	private List<Byte> parentToggles = new ArrayList<Byte>();
	
	public Hierachy(Core core) {
		this.core = core;
	}
	
	public void init() {
		off = UI.loadTexture("resources/GUI/toggle-off.png");
		on = UI.loadTexture("resources/GUI/toggle-on.png");
	}
	
	Color c = new Color(0.65f);
	
	public void update() {
		height = Application.getFullHeight() - (core.getGUI().getAssets().getStartY() + core.getGUI().getAssets().getHeight()) - startY;
		
		UI.drawButtonWithOutline(startX, startY - 25, 0.1f, width, 25, lineWidth, lineHeight, "Hierachy", 0, 0, -0.1f, 0.5f, 0.5f, Color.black, c, Color.black);
		
		boolean isHoveringPanel = UI.drawButtonWithOutline(startX, startY, 0.3f, width, height, lineWidth, lineHeight, Color.gray, Color.black);
		
		Scene currentScene = SceneManager.getCurrentScene();
		
		if(currentScene != null) {
			if(currentScene.getGameObjects().size() > parentToggles.size()) {
				int difference = Math.abs(currentScene.getGameObjects().size() - parentToggles.size());
				
				for(int i = 0; i < difference; i++) {
					parentToggles.add((byte) 0);
				}
			}
			
			for(int i = 0; i < currentScene.getGameObjects().size(); i++) {
				GameObject obj = currentScene.getGameObjects().get(i);
				
				if(obj == null || obj.shouldBeRemoved) {
					currentScene.getGameObjects().remove(i);
					
					i--;
					index--;
					
					continue;
				}
				
				if(/*!Editor.isPlaying() && */selectedObject != null && Camera.main != null) {
					if(selectedObject.uuid == null) {
						return;
					}
					
					if(!selectedObject.uuid.equals(Camera.main.gameObject.uuid)) {
						Vector3f pos = Camera.main.convertWorldToScreenSpace(selectedObject.getPosition());
						
						UI.drawline(pos.x, pos.y, pos.z, pos.x + 100, pos.y, pos.z, Color.red, 5);
						UI.drawline(pos.x, pos.x, pos.z, pos.x, pos.y + 100, pos.z, Color.blue, 5);
						UI.drawline(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z + 100, Color.green, 5);
					}
				}
				
				if(!obj.hasParent()) {
					drawObject(obj, index, 0, i);
				}
			}
		}
		
		index = 0;
		
		//Panel
		if(isHoveringPanel) {
			if(Input.isMouseButtonDown(KeyCode.Mouse1) && !clicked) {
				UI.addPopup(-0.2f, new Vector2f(130, 30), "Hie", Color.white, Color.black,
						new String[] {"2D Objects<Empty GameObject;New GameObject>", "3D Objects<GameObject>", "UI<Button;Text>", "LightSource", "New Camera"}, this::Popup);
			}
			
			//If the user dropped dragged object to top,
			//of nothing, then set parent to nothing.
			if(core.getDraggedObject() != null && !clicked && core.getDraggedObject() instanceof GameObject) {
				if(Input.isMouseButtonUp(KeyCode.Mouse0)) {
					GameObject obj = (GameObject)core.getDraggedObject();
					
					if(obj.hasParent()) {
						obj.setParent(null);
					}
				}
			}
			
			//If the user is dragging an object and,
			//releases left mouse button into nothing,
			//then stop dragging, even if ontop of a gameObject.
			if(core.getDraggedObject() instanceof GameObject) {
				if(Input.isMouseButtonUp(KeyCode.Mouse0)) {
					core.setDraggedObject(null);
				}
			}
			
			//TODO: Make any changes to this prefabs applies to the folder's prefab.
			if(core.getDraggedObject() instanceof Folder && Input.isMouseButtonUp(KeyCode.Mouse0)) {
				File file = new File(((Folder)core.getDraggedObject()).getAbsolutePath());
				
				try {
					if(file.getAbsolutePath().contains(".")) {
						String extention = file.getAbsolutePath().split("\\.")[1];
						
						if(extention.equalsIgnoreCase("gameobject")) {
							FileInputStream fis = new FileInputStream(file);
							ObjectInputStream stream = new ObjectInputStream(fis);
							
							GameObject objj = (GameObject)Editor.readObjectFromFile(stream);
							
							//try {
								boolean temp = Editor.canAddObjects;
								Editor.canAddObjects = true;
								currentScene.addObject(objj.clone());
								Editor.canAddObjects = temp;
							/*} catch (CloneNotSupportedException e) {
								e.printStackTrace();
							}*/
							
							core.getGUI().getInspector().linkObject(objj, file, null);
							
							stream.close();
							fis.close();
						} else if(extention.equalsIgnoreCase("png") || extention.equalsIgnoreCase("jpg")) {
							String[] path = file.getAbsolutePath().replace('\\','/').split("\\.")[0].split("/");
							String name = path[path.length - 1];
							
							Texture t = UI.loadTexture(file.getAbsolutePath());
							Vector2f scale = UI.getScale(t.getWidth(), t.getHeight(), 0.75f);
							SpriteRenderer sr = new SpriteRenderer();
							
							sr.spriteName = name;
							
							GameObject go = new GameObject(name, new Vector3f(), new Vector3f(), new Vector3f(scale.x, scale.y, 1));
							go.addBehaviour(sr);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				core.setDraggedObject(null);
			}
			
			if(Input.isKeyDown(KeyCode.Delete) && !Editor.isPlaying()) {
				if(selectedObject != null) {
					selectedObject.destroy();
					
					selectedObject = null;
				}
			}
			
			if(Input.getScrollY() != 0) {
				scrollX += Input.getScrollY() * 8;
				
				lastScroll = System.currentTimeMillis();
			}
		}
		
		if(System.currentTimeMillis() - lastScroll > 2000) {
			scrollX = Math.max(scrollX - 4, 0);
		}
		
		clicked = false;
	}
	
	private void drawObject(GameObject obj, int index, int childIndex, int l) {
		float x = 0, y = 0, z = 0.2f;
		
		if(obj.equals(core.getDraggedObject())) {
			x = (float)Input.getMouseX();
			y = (float)Input.getMouseY();
		} else {
			x = 0;
			y = index * 30;
		}
		
		this.index++;
		
		float xp = (childIndex * 30) + startX + x + (lineWidth * 2);
		float yp = startY + y + (lineHeight * 2) + scrollX;
		
		if(parentToggles.size() < l + childIndex) {
			int difference = (l + childIndex) - parentToggles.size();
			
			for(int i = 0; i < difference; i++)
				parentToggles.add((byte) 0);
		}
		
		if(obj.hasChildren()) {
			if(parentToggles.get(l+childIndex) == 1) {
				for(int i = 0; i < obj.getChildren().size(); i++) {
					drawObject(obj.getChildren().get(i), index + 1, childIndex + 1, l+i);
					
					if(previousObject != null && previousObject.hasChildren() 
							&& parentToggles.get(previousToggleIndex) == 1 && (!obj.hasParent() || !obj.getParent().equals(previousObject))) {
						index += previousObject.getChildren().size();
					}
					
					index++;
				}
			}
		}
		
		//If this is the object that's being dragged.
		if(obj.equals(core.getDraggedObject())) {
			xp = x;
			yp = y;
			z = -0.5f;
		} else {
			//If it's outside, don't draw
			if(yp > (height + (buttonHeight * 2)) || (yp - buttonHeight) < 0) {
				return;
			}
		}
		
		boolean isPrefab = false;//Files.exists(Paths.get(Editor.getWorkSpaceDirectory() + "Data\\" + obj.uuid + ".bin"));
		
		boolean isHovering = UI.drawButton(xp, yp, z, width - (lineWidth * 4) - (childIndex * 30), buttonHeight,
				obj.getName() + (isPrefab ? " (Prefab) " : "") + (obj.hasChildren() ? "(" + obj.getChildren().size() + ")" : ""),
						(obj.hasChildren() ? 30 : 0), 0, -0.1f, 0.5f, 0.5f, Color.black,
				obj.equals(selectedObject) ? Color.cyan : Color.lightGray);
		
		if(obj.hasChildren()) {
			Toggle toggle = UI.toggle(off, on, xp + 4, yp + 4, z - 0.1f, 16, 16, parentToggles.get(l+childIndex) == 0 ? false : true);
			
			if(toggle.isHovering() && Input.isMouseButtonDown(KeyCode.Mouse0)) {
				clicked = true;
				parentToggles.set(l+childIndex, parentToggles.get(l+childIndex) == 0 ? (byte)1 : (byte)0);
			}
		}
		
		//If clicked and dragged while hovering over a gameObject.
		if(!clicked && isHovering && core.getDraggedObject() == null && !Editor.isPlaying()) {
			if(core.getDraggedObject() == null && Input.isMouseButton(KeyCode.Mouse0) && Input.isDragging()) {
				core.setDraggedObject(obj);
			}
		}
		
		//If dragged ontop of another gameobject.
		if(core.getDraggedObject() != null && !clicked && isHovering && !core.getDraggedObject().equals(obj) &&
				core.getDraggedObject() instanceof GameObject) {
			if(Input.isMouseButtonUp(KeyCode.Mouse0)) {
				((GameObject)core.getDraggedObject()).setParent(obj);
				core.setDraggedObject(null);
			}
		}
		
		if(Input.isKey(KeyCode.LeftControl) && selectedObject != null && !Editor.isPlaying()) {
			if(Input.isKeyUp(KeyCode.R)) { //Rename Object.				
				String newObjectName = tinyfd_inputBox("Renaming object!", "What would you like to rename your object to?", "");
				
				if(newObjectName != null) {
					selectedObject.setName(newObjectName);
				}
			}
			
			if(Input.isKeyUp(KeyCode.D)) { //Duplicate Object.
				//try {
					GameObject cloneObj = selectedObject.clone();
					
					SceneManager.getCurrentScene().addObject(cloneObj);
				/*} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}*/
			}
		}
		
		//If only clicked while hovering over the gameObject.
		if(core.getDraggedObject() != obj && isHovering && Input.isMouseButtonDown(KeyCode.Mouse0) && !Input.isDragging()) {
			if(!obj.equals(selectedObject)) {
				setSelectedObject(obj);
				core.setLastSelectedObject(obj);
			}
			
			clicked = true;
		}
		
		previousToggleIndex = l+childIndex; 
		previousObject = obj;
	}
	
	public void Popup(String s) {
		if(SceneManager.getCurrentScene() == null) {
			return;
		}
		
		if(!Editor.isPlaying()) {
			Editor.canAddObjects = true;
		}
		
		if(s.equals("Empty GameObject")) {
			new GameObject("Empty GameObject", new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
		} else if(s.equals("New GameObject")) {
			GameObject gameObject = new GameObject("New GameObject", new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
			
			gameObject.addBehaviour(new SpriteRenderer());
		} else if(s.equals("New Camera")) {
			GameObject gameObject = new GameObject("Camera", new Vector3f(0, 0, 5), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
			
			gameObject.addBehaviour(new Camera());
			//Editor.createCamera(currentScene);
		} else if(s.equals("Button")) {
			GameObject obj = new GameObject("Button", new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
			
			obj.addBehaviour(new com.Anix.Behaviours.Button());
		} else if(s.equals("Text")) {
			// GameObject obj = new GameObject("Text", new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), null);
		} else if(s.equals("LightSource")) {
			GameObject obj = new GameObject("LightSource", new Vector3f(0, 0, 5), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
			
			obj.addBehaviour(new LightSource());
		}
		
		if(s.equals("GameObject")) {
			GameObject obj = new GameObject("New GameObject");
			
			obj.addBehaviour(new MeshRenderer());
		}
	}
	
	public GameObject getSelectedObject() {
		return selectedObject;
	}
	
	public void setSelectedObject(GameObject obj) {
		selectedObject = obj;
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
