package com.Anix.SceneManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.Anix.Behaviours.Camera;
import com.Anix.Main.Core;

public class SceneManager {
	public static Scene currentScene;
	
	private static Core core;
	
	private static final List<Scene> scenes = new ArrayList<Scene>();
	
	public SceneManager(Core core) {
		SceneManager.core = core;
	}
	
	public static void loadScene(String name) {
		if(name == null || name.isEmpty()) {
			return;
		}
		
		Scene scene = null;
		
		for(int i = 0; i < scenes.size(); i++) {
			if(scenes.get(i).getName().trim().equals(name.trim())) {
				scene = scenes.get(i);
				
				break;
			}
		}
		
		if(scene != null) {
			if(scene == currentScene) {
				return;
			}
			
			scene.destroy();
			Camera.main = null;
			Core.updateAbleObjects.clear();
			
			currentScene = scene;
			
			core.getEditor().saveScene();
			core.getEditor().load(currentScene);
		} else {
			System.err.println("[ERROR] Couldn't find a scene with the name of " + name);
		}
	}
	
	public static void addScene(Scene scene) {
		for(int i = 0; i < scenes.size(); i++) {
			if(scenes.get(i).getName().equals(scene.getName())) {
				System.err.println("[ERROR] Scene with the name of " + scene.getName() + " already exists!");
				
 				return;
			}
		}
		
		scenes.add(scene);
		
		//if(scenes.size() == 1)
		//	loadScene(scene.getName());
	}
	
	public static final Scene getCurrentScene() {
		return currentScene;
	}
	
	public static final List<Scene> getScenes() {
		return Collections.unmodifiableList(scenes);
	}
}
