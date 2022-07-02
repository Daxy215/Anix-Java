package com.Anix.SceneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.Anix.Behaviours.Camera;
import com.Anix.Engine.Editor;
import com.Anix.GUI.Windows.Assets.Folder;
import com.Anix.IO.ProjectSettings;
import com.Anix.Math.MathD;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

public class Scene {
	private String name;
	private int index;
	
	private Folder folder;
	
	private List<GameObject> gameObjects = new ArrayList<GameObject>();
	
	public Scene(String name, Folder folder) {
		this.name = name;
		this.index = SceneManager.getScenes().size();
		this.folder = folder;
	}
	
	public void update() {
		for(int i = 0; i < gameObjects.size(); i++) {
			if(gameObjects.get(i).shouldBeRemoved) {
				gameObjects.remove(i);
				i--;
			}
		}
	}
	
	public void addObject(GameObject gameObject) {
		if(!Editor.canAddObjects && ProjectSettings.isEditor) {
			System.err.println("cant :( " + gameObject.getName());
			
			return;
		}
		
		if(gameObject == null) {
			System.err.println("null");
			return;
		}
		
		if(gameObject.shouldBeRemoved) {
			System.err.println("Should be r4emoved? " + gameObject.getName());
			return;
		}
		
		gameObjects.add(gameObject);
	}
	
	public GameObject getGameObjectByUUID(UUID uuid) {
		for(int i = gameObjects.size() - 1; i >= 0; i--) {
			if(gameObjects.get(i) == null) {
				gameObjects.remove(i);
				i--;
				
				continue;
			}
			
			if(gameObjects.get(i).shouldBeRemoved) {
				gameObjects.remove(i);
				i--;
				continue;
			}
			
			if(gameObjects.get(i).uuid.equals(uuid))
				return gameObjects.get(i);
		}
		
		return null;
	}
	
	public GameObject getGameObjectAt(Vector2f position) {
		for(int i = gameObjects.size() - 1; i >= 0; i--) {
			GameObject obj = gameObjects.get(i);
			
			if(obj.shouldBeRemoved) {
				gameObjects.remove(i);
				i--;
				continue;
			}
			
			if(gameObjects.get(i).getBehaviour(Camera.class) != null) //If the camera, then skip
				continue;
			
			if(obj.getPosition().getXY().equals(position)) {
				return obj;
			}
		}
		
		return null;
	}
	
	public GameObject getGameObjectUsingCollision(Vector2f position) {
		GameObject obj;
		Vector3f pos;
		Vector2f scale;
		float x, y;
		
		for(int i = 0; i < gameObjects.size(); i++) {
			if(gameObjects.get(i).getBehaviour(Camera.class) != null) //If the camera, then skip
				continue;
			
			obj = gameObjects.get(i);
			
			if(obj.shouldBeRemoved) {
				gameObjects.remove(i);
				i--;
				continue;
			}
			
			if(MathD.distanceBetweenVector2(obj.getPosition().getXY(), position) > 5)
				continue;
			
			pos = obj.getPosition();
			scale = new Vector2f(obj.getScale().x * 0.5f, obj.getScale().y * 0.5f);
			
			x = pos.x - position.x;
			y = pos.y - position.y;
			
			if(x >= -scale.x && x <= scale.x) {
				if(y >= -scale.y && y <= scale.y) {
					return obj;
				}
			}
		}
		
		return null;
	}
	
	public GameObject getGameObjectAt(Vector3f position) {
		for(int i = gameObjects.size() - 1; i >= 0; i--) {
			GameObject obj = gameObjects.get(i);
			
			if(gameObjects.get(i).getBehaviour(Camera.class) != null) //If the camera, then skip
				continue;
			
			if(obj.getPosition().equals(position)) {
				return obj;
			}
		}
		
		return null;
	}
	
	public GameObject getGameObjectAt(Vector3f position, Vector3f alt) {
		for(int i = gameObjects.size() - 1; i >= 0; i--) {
			GameObject obj = gameObjects.get(i);
			
			if(obj.getBehaviour(Camera.class) != null) //If the camera, then skip
				continue;
			
			Vector3f pos = obj.getPosition();
			
			if((pos.x - position.x) < alt.x) {
				if((pos.y - position.y) < alt.y) {
					if((pos.z - position.z) < alt.z) {
						return obj;
					}
				}
			}
		}
		
		return null;
	}
	
	public void destroy() {
		for(int i = 0; i < gameObjects.size(); i++) {
			gameObjects.get(i).destroy();
		}
		
		gameObjects.clear();
	}
	
	public final String getName() {
		return name;
	}
	
	public final int getIndex() {
		return index;
	}
	
	public final Folder getFolder() {
		return folder;
	}
	
	public List<GameObject> getGameObjects() {
		return gameObjects;
	}
}
