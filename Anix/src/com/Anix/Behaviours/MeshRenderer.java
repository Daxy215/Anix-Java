package com.Anix.Behaviours;

import com.Anix.Annotation.HideFromInspector;
import com.Anix.Engine.Graphics.Mesh;
import com.Anix.GUI.Sprite;
import com.Anix.Main.Core;

public class MeshRenderer extends Behaviour {
	private static final long serialVersionUID = 5790026882509712256L;
	
	public String spriteName = "Quad.png";
	
	@HideFromInspector
	public transient boolean addedToRenderering = false;
	
	@Override
	public void awake() {
		Mesh mesh = null;
		Sprite sprite = null;
		
		for(int i = 0; i < Core.getSprites().size(); i++) {
			if(Core.getSprites().get(i).getName().equalsIgnoreCase(spriteName.toLowerCase())) {
				sprite = Core.getSprites().get(i);
				mesh = Core.meshManager.getMeshByPath(sprite.getPath());
				
				break;
			}
		}
		
		//Either doesn't exists
		//or uses a default path.
		if(sprite == null) {
			if(SpriteRenderer.class.getResource("/textures/" + spriteName) == null) {
				System.err.println("[ERROR] [SpriteRenderer] Couldn't locate a texture with the name of: " + spriteName);
				
				return;
			} else {
				mesh = Core.meshManager.getMeshByPath("/textures/" + spriteName);
				
				if(mesh == null) {
					mesh = new Mesh(new Sprite(spriteName, "/textures/" + spriteName, null));
					Core.meshManager.push(mesh);
				}
			}
		}
		
		gameObject.setMesh(mesh);
	}
	
	@Override
	public void render() {
		if(!addedToRenderering && Core.getMasterRenderer() != null) {
			Core.getMasterRenderer().addEntity(gameObject);
			
			addedToRenderering = true;
		}
	}
	
	@Override
	public void onValueChanged(String fieldName, String oldValue, String newValue) {
		start();
	}
	
	@Override
	public void onEnable() {
		gameObject.setIsEnabled(true);
	}
	
	@Override
	public void onDisable() {
		gameObject.setIsEnabled(false);
	}
	
	@Override
	public void onRemove() {
		//TODO: If mesh only has one entity, then destory.
		gameObject.shouldBeRemoved = true;
	}
	
	@Override
	public void onDestroy() {
		gameObject.shouldBeRemoved = true;
	}
}
