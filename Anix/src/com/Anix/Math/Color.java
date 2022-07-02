package com.Anix.Math;

import java.io.Serializable;

public class Color implements Serializable {
	private static final long serialVersionUID = 5125190708900329683L;
	
	public float r, g, b;
	
	public transient static final Color white = new Color(1);
	public transient static final Color red = new Color(1, 0, 0);
	public transient static final Color redBurgundy = new Color(0.50196078431f, 0, 0.12549019607f);
	public transient static final Color green = new Color(0, 1, 0);
	public transient static final Color blue = new Color(0, 0, 1);
	public transient static final Color cyan = new Color(0, 1, 1);
	public transient static final Color gray = new Color(0.5f);
	public transient static final Color lightGray = new Color(0.82745098039f);
	public transient static final Color silver = new Color(0.75f);
	public transient static final Color black = new Color(0);
    public transient static final Color WHITE = white;
    public transient static final Color LIGHT_GRAY = lightGray;
    public transient static final Color GRAY = gray;
    public transient static final Color darkGray  = new Color(0.25098039215f);
    public transient static final Color DARK_GRAY = darkGray;
    public transient static final Color BLACK = black;
    public transient static final Color RED = red;
    public transient static final Color darkRed = new Color(0.54509803921f, 0, 0);
    public transient static final Color DARK_RED = darkRed;
    public transient static final Color pink      = new Color(1, 0.6862745098f, 0.6862745098f);
    public transient static final Color PINK = pink;
    public transient static final Color orange    = new Color(1, 0.78431372549f, 0);
    public transient static final Color ORANGE = orange;
    public transient static final Color yellow    = new Color(1, 1, 0);
    public transient static final Color YELLOW = yellow;
    public transient static final Color darkYellow = new Color(0.96470588235f, 0.74509803921f, 0);
    public transient static final Color DARK_YELLOW = darkYellow;
    public transient static final Color GREEN = green;
    public transient static final Color magenta   = new Color(1, 0, 1);
    public transient static final Color MAGENTA = magenta;
    public transient static final Color CYAN = cyan;
    public transient static final Color BLUE = blue;
	
	public Color() {
		
	}
	
	public Color(float value) {
		if(value > 1) {
			value = value / 256;
		}
		
		this.r = value;
		this.g = value;
		this.b = value;
	}
	
	public Color(float r, float g, float b) {
		if(r > 1) {
			r = r / 256;
		}
		
		if(g > 1) {
			g = g / 256;
		}
		
		if(b > 1) {
			b = b / 256;
		}
		
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public float getRGB() {
		return r + g + b;
	}
	
	public float getRed() {
		return r;
	}
	
	public float getGreen() {
		return g;
	}
	
	public float getBlue() {
		return b;
	}
	
	public java.awt.Color covertToJavaColor() {
		try {
			return new java.awt.Color(r, g, b);
		} catch(IllegalArgumentException e) {
			System.err.println("[ERORR] Values exceeds 255: Red: " + (r * 255) + " Green: " + (g*255) + " blue: " + (b*255));
			
			return null;
		}
	}
	
	@Override
	public String toString() {
		return r + " " + g + " " + b;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(b);
		result = prime * result + Float.floatToIntBits(g);
		result = prime * result + Float.floatToIntBits(r);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Color other = (Color) obj;
		if (Float.floatToIntBits(b) != Float.floatToIntBits(other.b))
			return false;
		if (Float.floatToIntBits(g) != Float.floatToIntBits(other.g))
			return false;
		if (Float.floatToIntBits(r) != Float.floatToIntBits(other.r))
			return false;
		return true;
	}
}
