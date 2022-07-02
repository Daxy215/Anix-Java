package com.Anix.Math;

import java.io.Serializable;

public class Rect implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public float x = 0, y = 0, width = 0, height = 0;

	public Rect(float value) {
		this.x = value;
		this.y = value;
		this.width = value;
		this.height = value;
	}
	
	public Rect(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public void set(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public Rect addPosition(Rect r) {
		return new Rect(r.x + x, r.y + y, width, height);
	}
	
	public boolean Contains(Vector2f pos) {
		return pos.x > x && pos.x < x + width && pos.y > y && pos.y < y + height;
	}
	
	public boolean Intersects(Rect r) {
		if(x > r.x + r.width || x + width < r.x || y > r.y + r.height || y + height< r.y) return false;
		
		return true;
	}
	
	public Rect getIntersection(Rect r) {
		if(!Intersects(r)) return null;
		Vector2f v = new Vector2f(Math.max(x, r.x), Math.max(y, r.y));
		
		return new Rect(v.x, v.y, Math.min(x + width, r.x + r.width) - v.x, Math.min(y + height, r.y + r.height) - v.y);
	}
	
	@Override
	public String toString() {
		return "(" + x + " " + y + " " + width + " " + height + ")";
	}
}
