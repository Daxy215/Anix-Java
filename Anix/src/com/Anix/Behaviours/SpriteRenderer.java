package com.Anix.Behaviours;

import com.Anix.Annotation.HideFromInspector;
import com.Anix.Annotation.Type;
import com.Anix.Engine.Graphics.Material;
import com.Anix.Engine.Graphics.Mesh;
import com.Anix.GUI.Sprite;
import com.Anix.Main.Core;

public class SpriteRenderer extends Behaviour {
	private static final long serialVersionUID = 2L;
	
	@Type(values = {"png", "jpg"})
	public String spriteName = "Default.png";
	private String value;
	
	public Material material = new Material();
	
	@HideFromInspector
	public transient boolean addedToRenderering = false;
	
	@Override
	public void awake() {
		value = spriteName;
		
		updateSprite();
		
		if(Core.getMasterRenderer() != null && Camera.main != null) {
			Core.getMasterRenderer().addEntity(gameObject);
			
			addedToRenderering = true;
		}
		
		requestRender();
	}
	
	@Override
	public void render() {
		if(gameObject == null)
			return;
		
		if(value != spriteName) {
			value = spriteName;
			updateSprite();
		}
		
		if(gameObject.getMesh() != null && gameObject.getMesh().getMaterial() != null && !gameObject.getMesh().getMaterial().equals(material))
			gameObject.getMesh().setMaterial(material);
		
		if(!addedToRenderering && Core.getMasterRenderer() != null && Camera.main != null) {
			Core.getMasterRenderer().addEntity(gameObject);
			
			addedToRenderering = true;
		}
	}
	
	@Override
	public void onEnable() {
		gameObject.setIsEnabled(true);
	}
	
	@Override
	public void onValueChanged(String fieldName, String oldValue, String newValue) {
		if(fieldName.equals("spriteName")) {
			updateSprite();
		}
	}
	
	@Override
	public void onDisable() {
		gameObject.setIsEnabled(false);
	}
	
	@Override
	public void onRemove() {
		//TODO: If mesh only has one entity, then destory.
		gameObject.setMesh(null);
	}
	
	@Override
	public void onDestroy() {
		gameObject.setMesh(null);
	}
	
	private void updateSprite() {
		if(gameObject == null) {
			return;
		}
		
		Mesh mesh = null;
		
		/*if(SpriteRenderer.class.getResource("/textures/" + spriteName) != null) {
			//System.err.println("[ERROR] [SpriteRenderer] Couldn't locate a texture with the name of: " + spriteName);
			mesh = Core.meshManager.getMeshByPath("/textures/" + spriteName);
			
			if(mesh == null) {
				mesh = new Mesh(new Sprite(spriteName, "/textures/" + spriteName, null));
				Core.meshManager.addMesh(mesh);
			}
		}
		*/
		Sprite sprite = Sprite.getSprite(spriteName);
		
		if(sprite != null) {
			mesh = Core.meshManager.getMeshByPath(sprite.getPath());
			
			if(mesh == null) {
				mesh = new Mesh(sprite);
				
				Core.meshManager.addMesh(mesh);
			}
		}
		
		if(sprite == null) {
			if(SpriteRenderer.class.getResourceAsStream("/textures/" + spriteName) == null) {
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
		
		if(mesh != null) {
			mesh.setHasBeenDestroied(false);
			mesh.setMaterial(material);
		}
		
		gameObject.setMesh(mesh);
	}
	
	public String getSpriteName() {
		return spriteName;
	}

	public void setSpriteName(String spriteName) {
		this.spriteName = spriteName;
		
		updateSprite();
	}
}
