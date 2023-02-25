package com.Anix.GUI.Windows;

import java.util.List;

import com.Anix.Behaviours.Camera;
import com.Anix.Behaviours.SpriteRenderer;
import com.Anix.GUI.GUI;
import com.Anix.GUI.Texture;
import com.Anix.GUI.UI;
import com.Anix.GUI.Windows.Assets.Folder;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Main.Core;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;
import com.Anix.SceneManager.Scene;
import com.Anix.SceneManager.SceneManager;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiHoveredFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;

public final class Hierarchy {
	private int selectedObjectIndex;
	private final float startX = 0, startY = 25;
	private float width = 250, height;
	
	public static GameObject selectedObject;//, draggedObject;
	private Core core;
	
	public Hierarchy(Core core) {
		this.core = core;
	}
	
	public void init() {
		
	}
	
	public void render() {
		height = Application.getHeight();
		
		ImGui.setNextWindowPos(startX, startY);
		ImGui.setNextWindowSize(width, height);
		ImGui.setNextWindowSizeConstraints(250/*Min width*/, -1.0f, Application.getFullWidth() - core.getGUI().getInspector().getWidth() - /*Distance between screens - Padding*/ 20, -1.0f);
		
		ImGui.begin("Hierarchy", GUI.defaultFlags);
		ImGui.pushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0, 0.5f);
		
		if(ImGui.isWindowHovered(ImGuiHoveredFlags.AllowWhenBlockedByActiveItem)) {
			//TODO: Check if any of the gameObjects were hovered.
			if(ImGui.isMouseClicked(1)) {
				ImGui.openPopup("HierarchyOptions");
			}
			
			if(Input.isMouseButtonUp(KeyCode.Mouse0)) {
				if(core.getDraggedObject() instanceof Folder) {
					Folder folder = (Folder)core.getDraggedObject();
					
					String extension = folder.getExtension();
					
					if(extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpg")) {
						Texture t = UI.loadTexture(folder.getAbsolutePath());
						Vector2f scale = UI.getScale(t.getWidth(), t.getHeight(), 0.75f);
						SpriteRenderer sr = new SpriteRenderer();
						
						sr.spriteName = folder.getName();
						
						GameObject go = new GameObject(folder.getName().substring(0, extension.length() - 1), new Vector3f(), new Vector3f(), new Vector3f(scale.x, scale.y, 1));
						go.addBehaviour(sr);
					}
					
					core.setDraggedObject(null);
				}
			}
			
			if(ImGui.isKeyDown(KeyCode.UpArrow)) {
				if(selectedObjectIndex != -1 && selectedObjectIndex - 1 >= 0) {
					setSelectedObject(--selectedObjectIndex);
				}
			}
			
			if(ImGui.isKeyDown(KeyCode.DownArrow)) {
				if(selectedObjectIndex != -1 && selectedObjectIndex + 1 < SceneManager.getCurrentScene().getGameObjects().size()) {
					setSelectedObject(++selectedObjectIndex);
				}
			}
		}
		
		if (ImGui.beginPopup("HierarchyOptions")) {
			if (ImGui.menuItem("Create Empty")) {
				new GameObject("Empty GameObject", new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));  
			}
			
			ImGui.separator();
			
	        if (ImGui.beginMenu("3D Object")) {
	            if (ImGui.menuItem("Cube")) {
	            	GameObject gameObject = new GameObject("New GameObject", new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
	            	
	    			gameObject.addBehaviour(new SpriteRenderer());
	            }
	            
				if (ImGui.beginMenu("Light")) {
					if (ImGui.menuItem("Directional Light")) {
						
					}
					
					ImGui.endMenu();
				}
				
				ImGui.endMenu();
			}
	        
	        ImGui.separator();
	        
	        if(ImGui.menuItem("Camera")) {
	        	GameObject gameObject = new GameObject("Camera", new Vector3f(0, 0, 5), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1));
	        	
				gameObject.addBehaviour(new Camera());
	        }
	        
			ImGui.endPopup();
	    }
		
		Scene curScene =  SceneManager.getCurrentScene();
		
		if(curScene != null) {
			drawObjects(curScene.getGameObjects(), 0);
		}
		
		if(selectedObject != null) {
			if(ImGui.isKeyDown(KeyCode.Delete)) {
				selectedObject.destroy();
			}
		}
		
		ImGui.popStyleVar();
		
		width = ImGui.getWindowWidth();
		
		ImGui.end();
	}
	
	private void drawObjects(List<GameObject> objects, int index) {
		for(int i = 0; i < objects.size(); i++) {
			GameObject object = objects.get(i);
			
			if(object.uuid == null)
				continue;
			
			int flags = ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.FramePadding;
			
			if(selectedObject == object) {
				flags |= ImGuiTreeNodeFlags.Selected;
			}
			
			//if(!object.hasChildren()) {
				flags |= ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen;
			//}
			
			boolean didIt = false;
			
			if(!object.isEnabled()) {
				ImGui.pushStyleColor(ImGuiCol.Text, 0.5f, 0.5f, 0.5f, 0.4f);
				didIt = true;
			}
			
			boolean canDraw = false;//object.getParent() != null && index == 0;
			
			if(!canDraw) {
				String name = object.getName().isEmpty() ? "##" : object.getName();
				
				if(ImGui.treeNodeEx(name, flags)) {
					/*if(object.hasChildren()) {
						drawObjects(object.getChildren(), index + 1);
						
						ImGui.treePop();
					}*/
				}
				
				//Single click - Select object
				if(ImGui.isItemHovered() && ImGui.isMouseClicked(0)) {
					setSelectedObject(object, i);
				}
			}
			
			//Double clicked - Focus
			if(ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(0)) {
				if(Camera.main != null) {
					Camera.main.gameObject.setPosition(object.getPosition().x, object.getPosition().y);
				}
			}
			
			//Dragging and dropping handling.
			/*if(ImGui.beginDragDropTarget()) {
				Object o = ImGui.acceptDragDropPayload("GameObject", ImGuiDragDropFlags.None);
				
				if(o != null) {
					System.err.println("Dragged " + draggedObject.getName() + " and dropped it ontop of " + object.getName());
					draggedObject.setParent(object);
				}
				
				ImGui.endDragDropTarget();
			}*/
			
			//int src_flags = ImGuiDragDropFlags.SourceNoDisableHover; // Keep the source displayed as hovered
			//src_flags |= ImGuiDragDropFlags.SourceNoHoldToOpenOthers; // Because our dragging is local, we disable the feature of opening foreign treenodes/tabs while dragging

			/*if(ImGui.beginDragDropSource(src_flags)) {
				ImGui.text(object.getName());
				
				ImGui.setDragDropPayload("GameObject", object.uuid);
				//draggedObject = object;
				core.setDraggedObject(object);
				
				ImGui.endDragDropSource();
			}*/
			//End dragging and dropping handling.
			
			ImGui.pushID(object.uuid.toString());
			
			//On right click basically
			if(ImGui.beginPopupContextItem()) {
				if(ImGui.button("Delete")) {
					object.destroy();
				}
				
				if(ImGui.button("Duplicate")) {
					GameObject o = object.clone();
					
					SceneManager.getCurrentScene().addObject(o);
				}
				
				ImGui.endPopup();
			}
			
			if(didIt) {
				ImGui.popStyleColor();
			}
			
			ImGui.popID();
			
			/*if(canDraw && object.hasChildren() && open) {
				//drawObjects(object.getChildren(), index + 1);
				
				ImGui.treePop();
			}*/
		}
	}
	
	public GameObject getSelectedObject() {
		return selectedObject;
	}
	
	public void setSelectedObject(GameObject obj) {
		if(obj == null) {
			selectedObjectIndex = -1;
			selectedObject = null;
			
			return;
		}
		
		for(int i = 0; i < SceneManager.getCurrentScene().getGameObjects().size(); i++) {
			if(SceneManager.getCurrentScene().getGameObjects().get(i).uuid == obj.uuid) {
				selectedObjectIndex = i;
				
				break;
			}
			
			selectedObjectIndex = -1;
		}
		
		if(selectedObjectIndex == -1) {
			Console.LogErr("[ERROR] Couldn't find the object being selected.");
			
			return;
		}
		
		selectedObject = obj;
	}
	
	public void setSelectedObject(GameObject obj, int index) {
		selectedObjectIndex = index;
		selectedObject = obj;
	}
	
	public void setSelectedObject(int index) {
		setSelectedObject(SceneManager.getCurrentScene().getGameObjects().get(index), index);
	}

	public float getStartX() {
		return startX;
	}

	public float getStartY() {
		return startY;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}
}
