package com.Anix.GUI.Windows;

import java.util.List;

import com.Anix.Behaviours.Camera;
import com.Anix.Behaviours.LightSource;
import com.Anix.Behaviours.MeshRenderer;
import com.Anix.Behaviours.SpriteRenderer;
import com.Anix.Engine.Editor;
import com.Anix.GUI.GUI;
import com.Anix.IO.Application;
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
	private final int startX = 0, startY = 25;
	private int width = 250, height;
	
	public static GameObject selectedObject, draggedObject;

	public void init() {

	}

	public void render() {
		height = Application.getFullHeight() - 200;
		
		ImGui.setNextWindowPos(startX, startY);
		ImGui.setNextWindowSize(width, height);
		
		ImGui.begin("Hierarchy", GUI.defaultFlags);
		ImGui.pushStyleVar(ImGuiStyleVar.ButtonTextAlign, 0, 0.5f);

		if(ImGui.getIO().getWantCaptureMouse()) {
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

		ImGui.popStyleVar();
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
			
			ImGui.pushID(object.uuid.toString());
			boolean canDraw = object.getParent() != null && index == 0;
			
			boolean open = false;
			
			if(!canDraw)
				open = ImGui.treeNodeEx(object.getName(), flags);
			
			//On right click basically
			if(ImGui.beginPopupContextItem()) {
				ImGui.text("This is a ga,e object yay =.=");
				
				ImGui.endPopup();
			}
			
			if(!object.isEnabled()) {
				ImGui.popStyleColor();
			}
			
			if(ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(0)) {
				//TODO: Focus on object.
				System.err.println("double clicked");
			}

			if(ImGui.isItemHovered() && ImGui.isMouseClicked(0)) {
				selectedObject = object;
			}
			
			if(!canDraw && ImGui.beginDragDropTarget()) {
				Object o = ImGui.acceptDragDropPayload("GameObject", ImGuiDragDropFlags.None);
				
				if(o != null) {
					System.err.println("AOYOYOYOYOO " + (o == null));
	
					draggedObject.setParent(object);
				}
				
				ImGui.endDragDropTarget();
			}
			
			int src_flags = ImGuiDragDropFlags.SourceNoDisableHover; // Keep the source displayed as hovered
			src_flags |= ImGuiDragDropFlags.SourceNoHoldToOpenOthers; // Because our dragging is local, we disable the feature of opening foreign treenodes/tabs while dragging
			//src_flags |= ImGuiDragDropFlags.SourceNoPreviewTooltip; // Hide the tooltip
			
			if(!canDraw && ImGui.beginDragDropSource(src_flags)) {
				//??
				//if(!(src_flags & ImGuiDragDropFlags.SourceNoPreviewTooltip)) {
				ImGui.text(object.getName());
				//}
				
				ImGui.setDragDropPayload("GameObject", object.uuid);
				draggedObject = object;
				
				ImGui.endDragDropSource();
			}
			
			ImGui.popID();
			
			if(object.hasChildren() && open) {
				drawObjects(object.getChildren(), index + 1);
				try {
					ImGui.treePop();
				} catch(Exception e) {
					System.err.println(object.getName() + " - " + e.getMessage());
				}
			}
		}
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
