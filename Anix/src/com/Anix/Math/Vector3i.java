package com.Anix.Math;

import java.io.Serializable;

public class Vector3i implements Serializable {
	private static final long serialVersionUID = -7035318170091819570L;
	
	public int x, y, z;
	
	public Vector3i() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Vector3i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void set(Vector3i position) {
		this.x = position.x;
		this.y = position.y;
		this.z = position.z;
	}
	
	public Vector3i add(Vector3i other) {
		x += other.x;
		y += other.y;
		z += other.z;
		
		return this;
	}
	
	public static Vector3i add(Vector3i vector1, Vector3i vector2) {
		return new Vector3i(vector1.getX() + vector2.getX(), vector1.getY() + vector2.getY(), vector1.getZ() + vector2.getZ());
	}
	
	public static Vector3i sub(Vector3i vector1, Vector3i vector2) {
		return new Vector3i(vector1.getX() - vector2.getX(), vector1.getY() - vector2.getY(), vector1.getZ() - vector2.getZ());
	}
	
	public static Vector3i multiply(Vector3i vector1, Vector3i vector2) {
		return new Vector3i(vector1.getX() * vector2.getX(), vector1.getY() * vector2.getY(), vector1.getZ() * vector2.getZ());
	}
	
	public Vector3f multiply(Vector3i other) {
		return new Vector3f(x * other.x, y * other.y, z * other.z);
	}
	
	public Vector3f multiply(float other) {
		return new Vector3f(x * other, y * other, z * other);
	}
	
	public static Vector3i divide(Vector3i vector1, Vector3i vector2) {
		return new Vector3i(vector1.getX() / vector2.getX(), vector1.getY() / vector2.getY(), vector1.getZ() / vector2.getZ());
	}
	
	public static int length(Vector3i vector) {
		return (int)Math.sqrt(vector.getX() * vector.getX() + vector.getY() * vector.getY() + vector.getZ() * vector.getZ());
	}
	
	public static Vector3i normlize(Vector3i vector) {
		int len = Vector3i.length(vector);
		
		return Vector3i.divide(vector, new Vector3i(len, len, len));
	}
	
	public static int dot(Vector3i vector1, Vector3i vector2) {
		return vector1.getX() * vector2.getX() + vector1.getY() * vector2.getY() + vector1.getZ() * vector2.getZ();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
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
		Vector3i other = (Vector3i) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return x + " " + y + " " + z;
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

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
}
