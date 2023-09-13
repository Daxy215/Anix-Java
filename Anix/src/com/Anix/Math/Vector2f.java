package com.Anix.Math;

import java.io.Serializable;

public class Vector2f implements Serializable {
	private static final long serialVersionUID = -7754560098612613287L;
	
	public float x, y;
	
	public static Vector2f zero = new Vector2f();
	
 	public Vector2f() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2f(float value) {
		this.x = value;
		this.y = value;
	}
	
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2f mul(Vector2f other) {
		this.x = x * other.x;
		this.y = y * other.y;
		
		return this;
	}
	
	public Vector2f mul(float value) {
		this.x = x * value;
		this.y = y * value;
		
		return this;
	}
	
	public void sub(float value) {
		this.x -= value;
		this.y -= value;
	}
	
	public double dot(Vector2f other) {
		return x * other.x + y * other.y;
	}
	
	public double magnitude() {
		return Math.sqrt(x * x + y * y);
	}
	
	public Vector2f normalize() {
		float mag = (float) magnitude();
		
		if(mag == 0) {
			return new Vector2f(0, 0);
		}
		
		return new Vector2f(x / mag, y / mag);
	}
	
	public static Vector2f add(Vector2f vector1, Vector2f vector2) {
		return new Vector2f(vector1.getX() + vector2.getX(), vector1.getY() + vector2.getY());
	}
	
	public Vector2f add(float x, float y) {
		this.x += x;
		this.y += y;
		
		return this;
	}
	
	public Vector2f add(Vector2f other) {
		x += other.x;
		y += other.y;
		
		return this;
	}
	
	public static Vector2f sub(Vector2f vector1, Vector2f vector2) {
		return new Vector2f(vector1.getX() - vector2.getX(), vector1.getY() - vector2.getY());
	}
	
	public static Vector2f multiply(Vector2f vector1, Vector2f vector2) {
		return new Vector2f(vector1.getX() * vector2.getX(), vector1.getY() * vector2.getY());
	}
	
	public static Vector2f multiply(Vector2f vector1, float value) {
		return new Vector2f(vector1.getX() * value, vector1.getY() * value);
	}
	
	public static Vector2f divide(Vector2f vector1, Vector2f vector2) {
		return new Vector2f(vector1.getX() / vector2.getX(), vector1.getY() / vector2.getY());
	}
	
	public static Vector2f divide(Vector2f vector1, float value) {
		return new Vector2f(vector1.getX() / value, vector1.getY() / value);
	}
	
	public float length() {
		return (float)Math.sqrt(getX() * getX() + getY() * getY());
	}
	
	public static float length(Vector2f vector) {
		return (float)Math.sqrt(vector.getX() * vector.getX() + vector.getY() * vector.getY());
	}
	
	public static Vector2f normalized(Vector2f vector) {
		float len = Vector2f.magnitude(vector);
		
		return Vector2f.divide(vector, new Vector2f(len));
	}
	
	public static float dot(Vector2f vector1, Vector2f vector2) {
		return vector1.getX() * vector2.getX() + vector1.getY() * vector2.getY(); 
	}
	
	public static float magnitude(Vector2f a) {
		float result = (float) Math.sqrt(a.x * a.x + a.y * a.y);
		
		return result;
	}
	
	public static Vector2f moveTowards(Vector2f from, Vector2f to, float speed) {
	    // Find the direction and distance from 'from' to 'to'
		Vector2f direction = Vector2f.sub(to, from);
	    float distance = (float) direction.magnitude();
	    
	    // If the distance is already less than or equal to the speed, just return 'to'
	    if (distance <= speed) {
	        return to;
	    }
	    
	    // Otherwise, move towards 'to' by the specified speed
	    Vector2f movement = direction.normalize().mul(speed);
	    return from.add(movement);
	}
	
	public Vector2f copy() {
		return new Vector2f(x, y);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		
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
		
		Vector2f other = (Vector2f) obj;
		
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		return x + " " + y;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}
}
