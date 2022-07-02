package com.Anix.GUI;

import org.lwjgl.opengl.GL11;

public class Sprite {
	private String name, path;
	
	private Texture texture = null;
	
	public Sprite(String name, String path, Texture texture) {
		this.name = name;
		this.path = path;
		this.texture = texture;
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