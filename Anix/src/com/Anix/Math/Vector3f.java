package com.Anix.Math;

import java.io.Serializable;

public class Vector3f implements Serializable {
	private static final long serialVersionUID = 212874680227942175L;
	
	public float x, y, z;
	
	public Vector3f() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Vector3f(float value) {
		this.x = value;
		this.y = value;
		this.z = value;
	}
	
	public Vector3f(Vector3f o) {
		this.x = o.x;
		this.y = o.y;
		this.z = o.z;
	}
	
	public Vector3f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(Vector3f position) {
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;
	}
	
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3f add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		
		return this;
	}
	
	public Vector3f add(Vector3f other) {
		this.x += other.x;
		this.y += other.y;
		this.z += other.z;
		
		return this;
	}
	
	public Vector3f add(Vector3i other) {
		this.x += other.x;
		this.y += other.y;
		this.z += other.z;
		
		return this;
	}
	
	public static Vector3f add(Vector3f vector1, Vector3f vector2) {
		return new Vector3f(vector1.getX() + vector2.getX(), vector1.getY() + vector2.getY(), vector1.getZ() + vector2.getZ());
	}
	
	public static Vector3f add(Vector3i vector1, Vector3f vector2) {
		return new Vector3f(vector1.getX() + vector2.getX(), vector1.getY() + vector2.getY(), vector1.getZ() + vector2.getZ());
	}
	
	public static Vector3f add(Vector3f vector1, Vector3i vector2) {
		return new Vector3f(vector1.getX() + vector2.getX(), vector1.getY() + vector2.getY(), vector1.getZ() + vector2.getZ());
	}
	
	public static Vector3f add(Vector3f vector1, Vector2f vector2) {
		return new Vector3f(vector1.getX() + vector2.getX(), vector1.getY() + vector2.getY(), vector1.getZ());
	}
	
	public static Vector3f add(Vector2f vector1, Vector3f vector2) {
		return new Vector3f(vector1.getX() + vector2.getX(), vector1.getY() + vector2.getY(), vector2.getZ());
	}
	
	public static Vector3f add(Vector3f vector1, float vector2) {
		return new Vector3f(vector1.getX() + vector2, vector1.getY() + vector2, vector1.getZ() + vector2);
	}
	
	public static Vector3i add(Vector3i vector1, Vector3i vector2) {
		return new Vector3i(vector1.getX() + vector2.getX(), vector1.getY() + vector2.getY(), vector1.getZ() + vector2.getZ());
	}
	
	public Vector3f minus(Vector3f other) {
		this.x -= other.x;
		this.y -= other.y;
		this.z -= other.z;
		
		return this;
	}
	
	public static Vector3f minus(Vector3f vector1, Vector3f vector2) {
		return new Vector3f(vector1.getX() - vector2.getX(), vector1.getY() - vector2.getY(), vector1.getZ() - vector2.getZ());
	}
	
	public static Vector3f sub(Vector3f vector1, Vector3f vector2) {
		return new Vector3f(vector1.getX() - vector2.getX(), vector1.getY() - vector2.getY(), vector1.getZ() - vector2.getZ());
	}
	
	public Vector3f sub(Vector3f other) {
		return new Vector3f(x - other.x, y - other.y, z - other.z);
	}
	
	public static Vector3f multiply(Vector3f vector1, Vector3f vector2) {
		return new Vector3f(vector1.getX() * vector2.getX(), vector1.getY() * vector2.getY(), vector1.getZ() * vector2.getZ());
	}
	
	public static Vector3f multiply(Vector3f vector1, float vector2) {
		return new Vector3f(vector1.getX() * vector2, vector1.getY() * vector2, vector1.getZ() * vector2);
	}
	
	public static Vector3i multiply(Vector3i vector1, int vector2) {
		return new Vector3i(vector1.getX() * vector2, vector1.getY() * vector2, vector1.getZ() * vector2);
	}
	
	public Vector3f multiply(float other) {
		x *= other;
		y *= other;
		z *= other;
		
		return this;
	}
	
	public Vector3f multiply(Vector3f other) {
		x *= other.x;
		y *= other.y;
		z *= other.z;
		
		return this;
	}
	
	public static Vector3f divide(Vector3f vector1, Vector3f vector2) {
		return new Vector3f(vector1.getX() / vector2.getX(), vector1.getY() / vector2.getY(), vector1.getZ() / vector2.getZ());
	}
	
	public static Vector3f divide(Vector3f vector1, float vector2) {
		return new Vector3f(vector1.getX() / vector2, vector1.getY() / vector2, vector1.getZ() / vector2);
	}
	
	public static float length(Vector3f vector) {
		return (float)Math.sqrt(vector.getX() * vector.getX() + vector.getY() * vector.getY() + vector.getZ() * vector.getZ());
	}
	
	public Vector3f normalize() {
		float len = Vector3f.length(this);
		
		this.x /= len;
		this.y /= len;
		this.z /= len;
		
		return this;
	}
	
	public static Vector3f normalize(Vector3f vector) {
		float len = Vector3f.length(vector);
		
		return Vector3f.divide(vector, new Vector3f(len, len, len));
	}
	
	public float dot(Vector3f b) {
		return dot(this, b);
	}
	
	public static float dot(Vector3f vector1, Vector3f vector2) {
		return vector1.getX() * vector2.getX() + vector1.getY() * vector2.getY() + vector1.getZ() * vector2.getZ();
	}
	
	public Vector3f cross(Vector3f b) {
		return cross(this, b);
	}
	
	public static Vector3f cross(Vector3f a, Vector3f b) {
		return new Vector3f(
				a.y * b.z - a.z * b.y,
				a.z * b.x - a.x * b.z,
				a.x * b.y - a.y * b.x);
	}
	
	public float magnitude() {
		return magnitude(this);
	}
	
	public static float magnitude(Vector3f a) {
		return (float) Math.sqrt(Math.pow(a.x, 2) + Math.pow(a.y, 2) + Math.pow(a.y, 2));
	}
	
	public Vector3f copy() {
		return new Vector3f(x, y, z);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		
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
		
		Vector3f other = (Vector3f) obj;
		
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		return x + " " + y + " " + z;
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

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
	
	public Vector2f getXY() {
		return new Vector2f(x, y);
	}
}
