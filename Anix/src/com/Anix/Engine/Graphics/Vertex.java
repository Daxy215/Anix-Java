package com.Anix.Engine.Graphics;

import java.io.Serializable;

import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;

public class Vertex implements Serializable, Cloneable {
	private static final long serialVersionUID = -5050702722506504792L;
	
	private Vector3f position;
	private Vector3f normal;
	private Vector2f textureCoord;
	
	public Vertex() {
		
	}
	
	public Vertex(Vector3f position, Vector3f normal, Vector2f textureCoord) {
		this.position = position;
		this.normal = normal;
		this.textureCoord = textureCoord;
		
	}
	
	@Override
	protected Vertex clone() {
		//return (Vertex)super.clone();
		return new Vertex(position.copy(), normal.copy(), textureCoord.copy());
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setNormal(Vector3f normal) {
		this.normal = normal;
	}

	public void setTextureCoord(Vector2f textureCoord) {
		this.textureCoord = textureCoord;
	}

	public Vector3f getNormal() {
		return normal;
	}
	
	public Vector2f getTextureCoord() {
		return textureCoord;
	}
}
