package com.Anix.GUI;

import java.io.Serializable;

public class Texture implements Serializable {
	private static final long serialVersionUID = 270553545980885235L;
	
	private int width, height, id;
	
	public Texture(int width, int height, int id) {
		this.width = width;
		this.height = height;
		this.id = id;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getId() {
		return id;
	}

	public int getTextureID() {
		return id;
	}
}
