package com.Anix.GUI.Windows;

import com.Anix.Behaviours.Camera;
import com.Anix.Behaviours.SpriteRenderer;
import com.Anix.Engine.UndoManager.Action;
import com.Anix.GUI.UI;
import com.Anix.IO.Application;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.IO.ProjectSettings;
import com.Anix.IO.ProjectSettings.ProjectType;
import com.Anix.IO.Time;
import com.Anix.Main.Core;
import com.Anix.Math.RayCast;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;
import com.Anix.SceneManager.Scene;
import com.Anix.SceneManager.SceneManager;

public final class SceneViewer {
	private GameObject selectedObject;
	
	private Action action;
	
	private Core core;
	
	public SceneViewer(Core core) {
		this.core = core;
		
		action = new Action(2, this::onUndo);
	}
	
	public void update() {
		Scene currentScene = SceneManager.getCurrentScene();
		
		if(UI.drawButton(Application.getStartX(), Application.getStartY(), Application.getWidth(), Application.getHeight())/* && !Editor.isPlaying()*/) {
			if(currentScene != null && Camera.main != null) {
				if(ProjectSettings.projectType.equals(ProjectType.D2)) {
					Camera.main.gameObject.getPosition().z -= Input.getScrollY();
					
					//2D Movement
					if(Input.isMouseButton(KeyCode.Mouse2)) {
						float dx = (float)(Input.getMouseX() - Input.getLastMouseX()) * 0.008f;
						float dy = (float)(Input.getMouseY() - Input.getLastMouseY()) * 0.008f;
						
						Camera.main.gameObject.addPosition(dx, -dy, 0);
					}
					
					Vector2f pos = Camera.main.convertScreenToWorldSpace();
					
					if(Input.isMouseButtonDown(KeyCode.Mouse0)) {
						GameObject obj = SceneManager.getCurrentScene().getGameObjectUsingCollision(pos);
						
						if(obj != null) {
							if(obj.getBehaviour(SpriteRenderer.class) == null)
								return;
							
							System.err.println("moving: " + obj.getName());
							action.addData(obj);
							action.addData(obj.getPosition().copy());
							
							selectedObject = obj;
							
							core.setLastSelectedObject(obj);
							core.getGUI().getHierachy().setSelectedObject(obj);
						} else {
							core.setLastSelectedObject(null);
							core.getGUI().getHierachy().setSelectedObject(null);
						}
					} else if(Input.isMouseButtonUp(KeyCode.Mouse0)) {
						selectedObject = null;
					}
					
					if(Input.isMouseButton(KeyCode.Mouse0) && selectedObject != null) {
						selectedObject.setPosition(pos.x, pos.y);
					}
				} else { //Free Camera :D
					float x = (float)Math.sin(Math.toRadians(Camera.main.gameObject.getRotation().getY())) * 50 * Time.deltaTime;
					float y = (float)Math.cos(Math.toRadians(Camera.main.gameObject.getRotation().getY())) * 50 * Time.deltaTime;
					
					if(Input.isMouseButtonDown(KeyCode.Mouse0)) {
						GameObject hit = RayCast.rayCast(10);
						
						if(hit != null) {
							core.setLastSelectedObject(hit);
							core.getGUI().getHierachy().setSelectedObject(hit);
						}
					}
					
					if(Input.isKey(KeyCode.W)) {
						Camera.main.gameObject.addPosition(-x, 0, -y);
					} else if(Input.isKey(KeyCode.S)) {
						Camera.main.gameObject.addPosition(x, 0, y);
					}
					
					if(Input.isKey(KeyCode.A)) {
						Camera.main.gameObject.addPosition(-y, 0, x);
					} else if(Input.isKey(KeyCode.D)) {
						Camera.main.gameObject.addPosition(y, 0, -x);
					}
					
					if(Input.isKey(KeyCode.Space)) {
						Camera.main.gameObject.addPosition(0, 100 * Time.deltaTime);
					} else if(Input.isKey(KeyCode.LeftShift)) {
						Camera.main.gameObject.addPosition(0, -(100 * Time.deltaTime));	
					}
					
					//Rotation
					if(Input.isMouseButton(KeyCode.Mouse1)) {
						float dx = (float)(Input.getMouseX() - Input.getLastMouseX()) * 0.1f;
						float dy = (float)(Input.getMouseY() - Input.getLastMouseY()) * 0.1f;
						
						Camera.main.gameObject.rotate(-dy, -dx, 0);
					}
				}
			}
		}
		
		/*if(Input.isMouseButtonUp(KeyCode.Mouse1)) {
				core.setLastSelectedObject(null);
				core.getGUI().getHierachy().setSelectedObject(null);
			}*/
	}
	
	public void render() {
		if(!ProjectSettings.isEditor) {
			UI.drawImageInverted(core.getFrameBuffer().getReflectionTexture(), 0, 0, 0.5f, Application.getFullWidth(), Application.getFullHeight());
		} else {
			UI.drawImageInverted(core.getFrameBuffer().getReflectionTexture(), Application.getStartX(), Application.getStartY(), 0.5f, Application.getWidth(), Application.getHeight());			
		}
	}
	
	private void onUndo(Object[] data) {
		GameObject obj = (GameObject) data[0];
		Vector3f prevPos = (Vector3f) data[1];
		
		obj.setPosition(prevPos);
	}
}
