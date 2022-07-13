package com.Anix.Math;

import java.io.Serializable;
import java.util.Arrays;

import com.Anix.Behaviours.Camera;

public class Matrix4f implements Serializable {
	private static final long serialVersionUID = 7175356164920201157L;
	
	public static final int SIZE = 4;
	private float[] elements = new float[SIZE * SIZE];
	
	public static Matrix4f identity() {
		Matrix4f result = new Matrix4f();
		
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				result.set(i, j, 0);
			}
		}
		
		result.set(0, 0, 1);
		result.set(1, 1, 1);
		result.set(2, 2, 1);
		result.set(3, 3, 1);
		
		return result;
	}
	
	public static Matrix4f translate(Vector3f translate) {
		Matrix4f result = Matrix4f.identity();
		
		result.set(3, 0, translate.getX());
		result.set(3, 1, translate.getY());
		result.set(3, 2, translate.getZ());
		
		return result;
	}
	
	public void translateLocal(Vector3f translate) {
		set(3, 0, translate.getX());
		set(3, 1, translate.getY());
		set(3, 2, translate.getZ());
	}
	
	public static Matrix4f rotate(float angle, Vector3f axis) {
		Matrix4f result = Matrix4f.identity();
		
		float cos = (float) Math.cos(Math.toRadians(angle));
		float sin = (float) Math.sin(Math.toRadians(angle));
		float C = 1f - cos;
		
		result.set(0, 0, cos + axis.getX() * axis.getX() * C);
		result.set(0, 1, axis.getX() * axis.getY() * C - axis.getZ() * sin);
		result.set(0, 2, axis.getX() * axis.getZ() * C + axis.getY() * sin);
		result.set(1, 0, axis.getY() * axis.getX() * C + axis.getZ() * sin);
		result.set(1, 1, cos + axis.getY() * axis.getY() * C);
		result.set(1, 2, axis.getY() * axis.getZ() * C - axis.getX() * sin);
		result.set(2, 0, axis.getZ() * axis.getX() * C - axis.getY() * sin);
		result.set(2, 1, axis.getZ() * axis.getY() * C + axis.getX() * sin);
		result.set(2, 2, cos + axis.getZ() * axis.getZ() * C);
		
		return result;
	}
	
	public void rotateLocal(float angle, Vector3f axis) {
		float cos = (float) Math.cos(Math.toRadians(angle));
		float sin = (float) Math.sin(Math.toRadians(angle));
		float C = 1f - cos;
		
		set(0, 0, get(0, 0) * cos + axis.getX() * axis.getX() * C);//
		set(0, 1, get(0, 1) * axis.getX() * axis.getY() * C - axis.getZ() * sin);
		set(0, 2, get(0, 2) * axis.getX() * axis.getZ() * C + axis.getY() * sin);
		
		//Inverted these.
		set(1, 0, get(1, 0) * axis.getY() * axis.getX() * C + axis.getZ() * sin);
		set(1, 1, get(1, 1) * cos + axis.getY() * axis.getY() * C);//
		set(1, 2, get(1, 2) * axis.getY() * axis.getZ() * C - axis.getX() * sin);
		
		set(2, 0, get(2, 0) * axis.getZ() * axis.getX() * C - axis.getY() * sin);
		set(2, 1, get(2, 1) * axis.getZ() * axis.getY() * C + axis.getX() * sin);
		set(2, 2, get(2, 2) * cos + axis.getZ() * axis.getZ() * C);//
	}
	
	public static Matrix4f scale(Vector3f scalar) {
		Matrix4f result = Matrix4f.identity();
		
		result.set(0, 0, scalar.getX());
		result.set(1, 1, scalar.getY());
		result.set(2, 2, scalar.getZ());
		//result.set(3, 3, 1);
		
		return result;
	}
	
	public void scaleLocal(Vector3f scalar) {
		set(0, 0, get(0, 0) * scalar.getX());
		set(1, 1, get(1, 1) * scalar.getY());
		set(2, 2, get(2, 2) * scalar.getZ());
		
		/*set(0, 0, (get(0, 0) * scalar.x) + (get(1, 0) * scalar.y) + (get(2, 0) * scalar.z) + get(3, 0));
		set(1, 1, (get(0, 1) * scalar.x) + (get(1, 1) * scalar.y) + (get(2, 1) * scalar.z) + get(3, 1));
		set(2, 2, (get(0, 2) * scalar.x) + (get(1, 2) * scalar.y) + (get(2, 2) * scalar.z) + get(3, 2));
		set(3, 3, 1);*/
	}
	
	public static Matrix4f transform(Vector3f position, Vector3f rotation, Vector3f scale) {
		if(position == null || rotation == null || scale == null) {
			System.err.println("[ERROR] Cannot update trasnformation! A value is null!");
			
			return null;
		}
		
		Matrix4f translationMatrix = Matrix4f.identity();
		translationMatrix.translateLocal(position);
		translationMatrix.rotateLocal(rotation.getX(), new Vector3f(1, 0, 0));
		translationMatrix.rotateLocal(rotation.getY(), new Vector3f(0, 1, 0));
		translationMatrix.rotateLocal(rotation.getZ(), new Vector3f(0, 0, 1));
		//translationMatrix.scaleLocal(scale); //TODO: upon rotation, if scale isn't 1. If rotation was 90, then it'll be 1.
		
		return translationMatrix.multiply(Matrix4f.scale(scale));
	}
	
	public static Matrix4f projection(float fov, float aspect, float near, float far) {
		Matrix4f result = Matrix4f.identity();
		
		float tanFOV = (float) Math.tan(Math.toRadians(fov * 0.5f));
		float range = far - near;
		
		result.set(0, 0, 1.0f / (aspect * tanFOV));
		result.set(1, 1, 1.0f / tanFOV);
		result.set(2, 2, -((far + near) / range));
		result.set(2, 3, -1.0f);
		result.set(3, 2, -((2 * far * near) / range));
		result.set(3, 3, 0f);
		
		return result;
	}
	
	public static Matrix4f orthographics(float left, float right, float top, float bottom, float near, float far) {
		Matrix4f result = Matrix4f.identity();
		
		result.set(0, 0, 2.0f / (right - left));
		result.set(1, 1, 2.0f / (top - bottom));
		result.set(2, 2, -2.0f / (far - near));
		result.set(3, 0, -(right + left) / (left - right));
		result.set(3, 1, -(top + bottom) / (top-bottom));
		result.set(3, 2, (far + near) / (far - near));
		result.set(3, 3, 1 + (Camera.main != null ? Camera.main.gameObject.getPosition().z * 0.5f : 0));
		
		return result;
	}
	
	public static Vector3f multPointMatrix(Vector3f in, Matrix4f M) {
		Vector3f out = new Vector3f();
		
	    //out = in * Mproj;
	    out.x   = in.x * M.get(0, 0) + in.y * M.get(1, 0) + in.z * M.get(2, 0) + /* in.z = 1 */ M.get(3, 0);
	    out.y   = in.x * M.get(0, 1) + in.y * M.get(1, 1) + in.z * M.get(2, 1) + /* in.z = 1 */ M.get(3, 1);
	    out.z   = in.x * M.get(0, 2) + in.y * M.get(1, 2) + in.z * M.get(2, 2) + /* in.z = 1 */ M.get(3, 2);
	    
	    float w = in.x * M.get(0, 3) + in.y * M.get(1, 3) + in.z * M.get(2, 3) + /* in.z = 1 */ M.get(3, 3);
	    
	    // normalize if w is different than 1 (convert from homogeneous to Cartesian coordinates)
	    if (w != 1) {
	        out.x /= w;
	        out.y /= w;
	        out.z /= w;
	    }
	    
	    return out;
	}
	
	public static Matrix4f view(Vector3f position, Vector3f rotation) {
		Vector3f negative = new Vector3f(-position.x, -position.y, -position.z);
		Matrix4f translationMatrix = Matrix4f.translate(negative);
		Matrix4f rotXMatrix = Matrix4f.rotate(rotation.getX(), new Vector3f(1, 0, 0));
		Matrix4f rotYMatrix = Matrix4f.rotate(rotation.getY(), new Vector3f(0, 1, 0));
		Matrix4f rotZMatrix = Matrix4f.rotate(rotation.getZ(), new Vector3f(0, 0, 1));
		
		Matrix4f rotationMatrix = Matrix4f.multiply(rotYMatrix, Matrix4f.multiply(rotZMatrix, rotXMatrix));
		
		return Matrix4f.multiply(translationMatrix, rotationMatrix);
	}
	
	public static Matrix4f multiply(Matrix4f matrix, Matrix4f other) {
		Matrix4f result = Matrix4f.identity();
		
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				result.set(i, j, matrix.get(i, 0) * other.get(0, j) +
								 matrix.get(i, 1) * other.get(1, j) +
								 matrix.get(i, 2) * other.get(2, j) +
								 matrix.get(i, 3) * other.get(3, j));
			}
		}
		
		return result;
	}
	
	public Vector4f mul(Vector4f l) {
		Vector4f results = new Vector4f
				(
						l.x*get(0, 0)+l.x*get(0, 1)+l.x*get(0, 2)+l.x*get(0, 3),
						l.y*get(1, 0)+l.y*get(1, 1)+l.y*get(1, 2)+l.y*get(1, 3),
						l.z*get(2, 0)+l.z*get(2, 1)+l.z*get(2, 2)+l.z*get(2, 3),
						l.w*get(3, 0)+l.w*get(3, 1)+l.w*get(3, 2)+l.w*get(3, 3)
				);
		
		return results;
	}
	
	public Matrix4f multiply(Matrix4f matrix) {
		Matrix4f result = Matrix4f.identity();
		
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				result.set(i, j, get(i, 0) * matrix.get(0, j) +
								 get(i, 1) * matrix.get(1, j) +
								 get(i, 2) * matrix.get(2, j) +
								 get(i, 3) * matrix.get(3, j));
			}
		}
		
		return result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(elements);
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
		
		Matrix4f other = (Matrix4f) obj;
		if (!Arrays.equals(elements, other.elements))
			return false;
		
		return true;
	}
	
	@Override
	public String toString() {
		String str = "";
		
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				str += get(i, j) + " ";
			}
			
			str += "\n";
		}
		
		return str;
	}
	
	public float get(int x, int y) {
		return elements[y * SIZE + x];
	}
	
	public void set(int x, int y, float value) {
		elements[y * SIZE + x] = value;
	}
	
	public void add(int x, int y, float value) {
		elements[y * SIZE + x] += value;
	}
	
	public float[] getAll() {
		return elements;
	}
}
