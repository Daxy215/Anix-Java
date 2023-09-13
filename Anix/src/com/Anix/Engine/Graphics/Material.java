package com.Anix.Engine.Graphics;

import java.io.Serializable;

import com.Anix.Annotation.Scriptable;
import com.Anix.Math.Color;

@Scriptable
public class Material implements Serializable {
	private static final long serialVersionUID = 9136794400485996252L;
	
	public Color color = Color.white;
	public Shader shader;
	
	public Material() {
		this.shader = Shader.defaultShader;
	}
	
	public Material(Color color) {
		this.color = color;
		this.shader = Shader.defaultShader;
	}
	
	public Material(Shader shader) {
		this.shader = shader;
	}
	
	public Material(Color color, Shader shader) {
		this.color = color;
		this.shader = shader;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Shader getShader() {
		return shader;
	}
	
	public void setShader(Shader shader) {
		this.shader = shader;
	}
}
