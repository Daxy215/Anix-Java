package com.Anix.GUI.Windows;

import java.util.List;

import com.Anix.Behaviours.Camera;
import com.Anix.Behaviours.SpriteRenderer;
import com.Anix.GUI.GUI;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;
import com.Anix.SceneManager.Scene;
import com.Anix.SceneManager.SceneManager;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiDragDropFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;

public final class Hierachy {
	private final float startX = 0, startY = 25;
	private float width = 250, height;
	
	public static GameObject selectedObject, draggedObject;
	private GUI gui;
	
	public Hierachy(GUI gui) {
		this.gui = gui;
	}
	
	public void init() {
		
	}
	
	public void render() {
		height = Application.getHeight();
		
		ImGui.setNextWindowPos(startX, startY);
		ImGui.setNextWindowSize(width, height);
		ImGui.setNextWindowSizeConstraints(250/*Min width*/, -1.0f, Application.getFullWidth() - gui.getInspector().getWidth() - /*Distance between screens - Padding*/ 20, -1.0f);
		
		ImGui.begin("Hierarchy", GUI.defaultFlags);
		ImGui.pushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0, 0.5f);
		
		if(ImGui.getIO().getWantCaptureMouse() && ImGui.isWindowHovered()) {
			//TODO: Check if any of the gameObjects were hovered.
			if(ImGui.isMouseClicked(1)) {
				ImGui.openPopup("HierarchyOptions");
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
			if(Input.isKeyDown(KeyCode.Delete)) {
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
			
			int flags = ImGuiTreeNodeFlags.OpenOnArrow;
			flags |= ImGuiTreeNodeFlags.FramePadding;
			
			if(selectedObject == object) {
				flags |= ImGuiTreeNodeFlags.Selected;
			}
			
			if(!object.hasChildren()) {
				flags |= ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen;
			}
			
			if(!object.isEnabled()) {
				ImGui.pushStyleColor(ImGuiCol.Text, 0.5f, 0.5f, 0.5f, 0.4f);
			}
			
			boolean canDraw = object.getParent() != null && index == 0;
			
			if(!canDraw) {
				String name = object.getName().isEmpty() ? "##" : object.getName();
				
				if(ImGui.treeNodeEx(name, flags)) {
					//Single click - Select object
					if(ImGui.isItemHovered() && ImGui.isMouseClicked(0)) {
						System.err.println("clicked on " + object.getName());
						selectedObject = object;
					}
					
					//Double clicked - Focus
					if(ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(0)) {
						if(Camera.main != null) {
							Camera.main.gameObject.setPosition(object.getPosition().x, object.getPosition().y);
						}
					}
					
					if(object.hasChildren()) {
						drawObjects(object.getChildren(), index + 1);
						
						ImGui.treePop();
					}
				}
			}
			
			//Dragging and dropping handling.
			if(ImGui.beginDragDropTarget()) {
				Object o = ImGui.acceptDragDropPayload("GameObject", ImGuiDragDropFlags.None);
				
				if(o != null) {
					System.err.println("Dragged " + draggedObject.getName() + " and dropped it ontop of " + object.getName());
					draggedObject.setParent(object);
				}
				
				ImGui.endDragDropTarget();
			}
			
			int src_flags = ImGuiDragDropFlags.SourceNoDisableHover; // Keep the source displayed as hovered
			src_flags |= ImGuiDragDropFlags.SourceNoHoldToOpenOthers; // Because our dragging is local, we disable the feature of opening foreign treenodes/tabs while dragging

			if(ImGui.beginDragDropSource(src_flags)) {
				ImGui.text(object.getName());
				
				ImGui.setDragDropPayload("GameObject", object.uuid);
				draggedObject = object;
				
				System.err.println("dragging " + object.getName());
				
				ImGui.endDragDropSource();
			}
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
			
			if(!object.isEnabled()) {
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
		selectedObject = obj;
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
