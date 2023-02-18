package com.Anix.GUI;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class Sprite {
	private String name, path;
	
	private Texture texture = null;
	
	public static List<Sprite> sprites = new ArrayList<>();
	
	public Sprite(String name, String path, Texture texture) {
		this.name = name;
		this.path = path;
		this.texture = texture;
		
		if(!sprites.contains(this))
			sprites.add(this);
	}
	
	public static Sprite getSprite(String name) {
		for(int i = 0; i < sprites.size(); i++)
			if(sprites.get(i).name.equalsIgnoreCase(name.toLowerCase()))
				return sprites.get(i);
		
		return null;
	}
	
	public void destroy() {
		if(texture != null)
			GL11.glDeleteTextures(texture.getId());
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		if(texture == null) {
			this.texture = null;
			
			return;
		}
		
		if(this.texture == texture || this.texture != null && this.texture.getId() == texture.getId())
			return;
		
		this.texture = texture;
	}
	
}