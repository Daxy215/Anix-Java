package com.Anix.Engine.Graphics;

import java.io.Serializable;

import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;

public class Vertex implements Serializable, Cloneable {
	private static final long serialVersionUID = -5050702722506504792L;
	
	private Vector3f position;
	private Vector3f normal;
	private Vector2f textureCoord;
	
	public Vertex(Vector3f position, Vector3f normal, Vector2f textureCoord) {
		this.position = position;
		this.normal = normal;
		this.textureCoord = textureCoord;
		
	}
	
	@Override
	protected Vertex clone() {
		try {
			return (Vertex)super.clone();
		} catch(CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Vector3f getNormal() {
		return normal;
	}
	
	public Vector2f getTextureCoord() {
		return textureCoord;
	}
}
