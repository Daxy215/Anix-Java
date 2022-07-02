package com.Anix.Math;

import java.io.Serializable;

public class Vector2i implements Serializable {
	private static final long serialVersionUID = -4175676413099672002L;
	
	public int x, y;
	
	public Vector2i() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public static Vector2i add(Vector2i vector1, Vector2i vector2) {
		return new Vector2i(vector1.getX() + vector2.getX(), vector1.getY() + vector2.getY());
	}
	
	public void add(int x, int y) {
		x += x;
		y += y;
	}
	
	public static Vector2i sub(Vector2i vector1, Vector2i vector2) {
		return new Vector2i(vector1.getX() - vector2.getX(), vector1.getY() - vector2.getY());
	}
	
	public static Vector2i multiply(Vector2i vector1, Vector2i vector2) {
		return new Vector2i(vector1.getX() * vector2.getX(), vector1.getY() * vector2.getY());
	}
	
	public static Vector2i divide(Vector2i vector1, Vector2i vector2) {
		return new Vector2i(vector1.getX() / vector2.getX(), vector1.getY() / vector2.getY());
	}
	
	public static int length(Vector2i vector) {
		return (int)Math.sqrt(vector.getX() * vector.getX() + vector.getY() * vector.getY());
	}
	
	public static Vector2i normlize(Vector2i vector) {
		int len = Vector2i.length(vector);
		
		return Vector2i.divide(vector, new Vector2i(len, len));
	}
	
	public static int dot(Vector2i vector1, Vector2i vector2) {
		return vector1.getX() * vector2.getX() + vector1.getY() * vector2.getY(); 
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
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
		
		Vector2i other = (Vector2i) obj;
		
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		return x + " " + y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
