package com.Anix.Math;

import java.io.Serializable;

public class Vector4f implements Serializable {
	private static final long serialVersionUID = 1349661942148429777L;
	
	public float x, y, z, w;
	
	public Vector4f() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
		this.w = 0;
	}
	
	public Vector4f(float value) {
		this.x = value;
		this.y = value;
		this.z = value;
		this.w = value;
	}
	
	public Vector4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public void set(Vector4f position) {
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;
		this.w = position.w;
	}
	
	public void set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	public void set(float value) {
		x = value;
		y = value;
		z = value;
		w = value;
	}
	
	public static Vector4f add(Vector4f vector1, Vector4f vector2) {
		return new Vector4f(vector1.getX() + vector2.getX(), vector1.getY() + vector2.getY(), vector1.getZ() + vector2.getZ(), vector1.getW() + vector2.getW());
	}
	
	public void add(float x, float y, float z, float w) {
		this.x += x;
		this.y += y;
		this.z += z;
		this.w += w;
	}
	
	public void add(float value) {
		this.x += value;
		this.y += value;
		this.z += value;
		this.w += value;
	}
	
	public void scale(float scale) {
		x *= scale;
		y *= scale;
		z *= scale;
		w *= scale;
	}
	
	public static Vector4f sub(Vector4f vector1, Vector4f vector2) {
		return new Vector4f(vector1.getX() - vector2.getX(), vector1.getY() - vector2.getY(), vector1.getZ() - vector2.getZ(), vector1.getW() - vector2.getW());
	}
	
	public static Vector4f multiply(Vector4f vector1, Vector4f vector2) {
		return new Vector4f(vector1.getX() * vector2.getX(), vector1.getY() * vector2.getY(), vector1.getZ() * vector2.getZ(), vector1.getW() * vector2.getW());
	}
	
	public static Vector4f divide(Vector4f vector1, Vector4f vector2) {
		return new Vector4f(vector1.getX() / vector2.getX(), vector1.getY() / vector2.getY(), vector1.getZ() / vector2.getZ(), vector1.getW() / vector2.getW());
	}
	
	public static float length(Vector4f vector) {
		return (float)Math.sqrt(vector.getX() * vector.getX() + vector.getY() * vector.getY() + vector.getZ() * vector.getZ());
	}
	
	public static Vector4f normlize(Vector4f vector) {
		float len = Vector4f.length(vector);
		
		return Vector4f.divide(vector, new Vector4f(len, len, len, len));
	}
	
	public static float dot(Vector4f vector1, Vector4f vector2) {
		return vector1.getX() * vector2.getX() + vector1.getY() * vector2.getY() + vector1.getZ() * vector2.getZ();
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
		
		Vector4f other = (Vector4f) obj;
		
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
		return x + " " + y + " " + z + " " + w;
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
	
	public Vector2f getXY() {
		return new Vector2f(x, y);
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
	
	public Vector3f getXYZ() {
		return new Vector3f(x, y, z);
	}
	
	public float getW() {
		return w;
	}

	public void setW(float w) {
		this.w = w;
	}
}
