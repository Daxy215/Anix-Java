package com.Anix.Math;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Matrix4f;

import com.Anix.Behaviours.Camera;

public class MathD {
	@SuppressWarnings("unchecked")
	public static <T> T[] reverse(T input[]) {
		T[] reversed = (T[]) new Object[input.length];
		
		int j = input.length;
		for (int i = 0; i < input.length; i++) { 
			reversed[j - 1] = input[i];
			j--;
		}
		
		return reversed;
	}
	
	
	public static <T> List<T> reverse(List<T> input) {
	    var reversed = new ArrayList<T>();
	    for (int i = input.size() - 1; i >= 0; i--) {
	        reversed.add(input.get(i));
	    }
	    return reversed;
	}
	
	public static boolean isArrayEmpty(Object[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if(arr[i] != null) {
				return false;
			}
		}
		
		return true;
	}
	
	public static Object[] covertArrayToList(List<Object> array) {
		Object[] obj = new Object[array.size()];
		
		for(int i = 0; i < obj.length; i++) {
			obj[i] = array.get(i);
		}
		
		return obj;
	}
	
	public static float fract(float value) {
		return value - (long)value;
	}
	
	public static int ensureRange(int value, int min, int max) {
		return Math.min(Math.max(value, min), max);
	}
	
	public static boolean inRange(int value, int min, int max) {
		return (value >= min) && (value <= max);
	}
	
	public static float pingPong(float start, float stop, float dist) {
		float d = stop-start;
		
		if((int)(Math.abs(dist)/d) % 2 == 0) {
			return start + Math.abs(dist) % d;
		} else {
			return stop - Math.abs(dist) % d;
		}
	}
	
	public static double distanceBetweenVector2(Vector2f pos1, Vector2f pos2) {
		return StrictMath.sqrt(Math.pow(pos1.getX() - pos2.getX(), 2)
				+ Math.pow(pos1.getY() - pos2.getY(), 2));
	}
	
	public static double distanceBetweenVector2(Vector2i pos1, Vector2f pos2) {
		return Math.sqrt(Math.pow(pos1.getX() - pos2.getX(), 2)
				+ Math.pow(pos1.getY() - pos2.getY(), 2));
	}
	
	public static double distanceBetweenVector2(Vector2i pos1, Vector2i pos2) {
		return Math.sqrt(Math.pow(pos1.getX() - pos2.getX(), 2)
				+ Math.pow(pos1.getY() - pos2.getY(), 2));
	}
	
	public static double distanceBetweenVector3(Vector3f pos1, Vector3f pos2) {
		return Math.sqrt(Math.pow(pos1.getX() - pos2.getX(), 2)
				+ Math.pow(pos1.getY() - pos2.getY(), 2)
				+ Math.pow(pos1.getZ() - pos2.getZ(), 2));
	}
	
	public static double distanceBetweenVector3(Vector3i pos1, Vector3f pos2) {
		return Math.sqrt(Math.pow(pos1.getX() - pos2.getX(), 2)
				+ Math.pow(pos1.getY() - pos2.getY(), 2)
				+ Math.pow(pos1.getZ() - pos2.getZ(), 2));
	}
	
	public static double distanceBetweenVector3(Vector3f pos1, Vector3i pos2) {
		return Math.sqrt(Math.pow(pos1.getX() - pos2.getX(), 2)
				+ Math.pow(pos1.getY() - pos2.getY(), 2)
				+ Math.pow(pos1.getZ() - pos2.getZ(), 2));
	}
	
	public static double distanceBetweenVector3(Vector3i pos1, Vector3i pos2) {
		return Math.sqrt(Math.pow(pos1.getX() - pos2.getX(), 2)
				+ Math.pow(pos1.getY() - pos2.getY(), 2)
				+ Math.pow(pos1.getZ() - pos2.getZ(), 2));
	}
	
	public static double distanceBetween2Points(double p1, double p2) {
		return Math.sqrt(Math.pow(p1 - p2, 2));
	}
	
	public static boolean AABBCollision(Vector3f pos0, Vector3f size0, Vector3f pos1, Vector3f size1) {
		return pos0.x - size0.x / 2.f < pos1.x + size1.x / 2.f && pos0.x + size0.x / 2.f > pos1.x - size1.x / 2.f &&
			   pos0.y - size0.y / 2.f < pos1.y + size1.y / 2.f && pos0.y + size0.y / 2.f > pos1.y - size1.y / 2.f &&
			   pos0.z - size0.z / 2.f < pos1.z + size1.z / 2.f && pos0.z + size0.z / 2.f > pos1.z - size1.z / 2.f;
	}
	
	public static float hermite(float start, float end, float value) {
		return lerp(start, end, value * value * (3.0f - 2.0f * value));
	}
	
	public static float sinerp(float start, float end, float value) {
		return lerp(start, end, (float)Math.sin(value * Math.PI * 0.5f));
	}
	
	public static float coserp(float start, float end, float value) {
		return lerp(start, end, 1.0f - (float)Math.cos(value * Math.PI * 0.5f));
	}
	
	public static float lerp(float start, float end, float value) {
		return ((1.0f - value) * start) + (value * end);
	}
	
	public static float invLerp(float a, float b, float v) {
		return (v - a) / (b - a);
	}
	
	public static float remap(float iMin, float iMax, float oMin, float oMax, float v) {
		float t = invLerp(iMin, iMax, v);
		
		return lerp(oMin, oMax, t);
	}
	
	public static float sqr(float in) {
		return (in * in);
	}
	
	public static float smoothstep(float edge0, float edge1, float x) {
		// Scale, bias and saturate x to 0..1 range
		x = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f); 
		
		// Evaluate polynomial
		return x * x * (3.0f - 2.0f * x);
	}
	
	public static float smooth(float x) {
		return x * x * (3 - 2 * 2);
	}
	
	public static float clamp(float x, float lowerlimit, float upperlimit) {
		if (x < lowerlimit)
			x = lowerlimit;
		if (x > upperlimit)
			x = upperlimit;

		return x;
	}
	
	public static float clamp01(float x) {
		if (x < 0)
			x = 0;
		if (x > 1)
			x = 1;

		return x;
	}
	
	public static float positionToAngle(Vector3f pos) {
		if(Camera.main == null) {
			return -1;
		}
		
		Vector2f newPos = Camera.main.convertScreenToWorldSpace();
		
		float angle = (float) Math.atan2(newPos.x - pos.x, newPos.y - pos.y);
		angle = (float) (angle * (180 / Math.PI));
		
		return angle;
	}
	
	public static float positionToAngle(Vector3f lookAtPos, Vector3f pos) {
		float angle = (float) Math.atan2(lookAtPos.x - pos.x, lookAtPos.y - pos.y);
		angle = (float) (angle * (180 / Math.PI));
		
		return angle;
	}
	
	public static float positionToAngle(Vector3f lookAtPos, Vector3i pos) {
		float angle = (float) Math.atan2(lookAtPos.x - pos.x, lookAtPos.y - pos.y);
		angle = (float) (angle * (180 / Math.PI));

		return angle;
	}
	
	public static float perlin3D(float x, float y, float z, FastNoise fs) {
		float ab = fs.GetPerlin(x, y);
		float bc = fs.GetPerlin(y, z);
		float ac = fs.GetPerlin(x, z);
		
		float ba = fs.GetPerlin(y, z);
		float cb = fs.GetPerlin(z, y);
		float ca = fs.GetPerlin(z, x);
		
		float abc = ab + bc + ac + ba + cb + ca;
		return abc / 6f;
	}
	
	public static float noise3D(float x, float y, float z, FastNoise fs) {
		float ab = fs.GetNoise(x, y);
		float bc = fs.GetNoise(y, z);
		float ac = fs.GetNoise(x, z);
		
		float ba = fs.GetNoise(y, z);
		float cb = fs.GetNoise(z, y);
		float ca = fs.GetNoise(z, x);
		
		float abc = ab + bc + ac + ba + cb + ca;
		return abc / 6f;
	}
	
	/*public static org.lwjglx.util.vector.Matrix4f convertMatrix(Matrix4f l) {
		org.lwjglx.util.vector.Matrix4f m = new org.lwjglx.util.vector.Matrix4f();
		
		
		m.m00 = l.get(0, 0); m.m10 = l.get(1, 0); m.m20 = l.get(2, 0); m.m30 = l.get(3, 0);
		m.m01 = l.get(0, 1); m.m11 = l.get(1, 1); m.m21 = l.get(2, 1); m.m31 = l.get(3, 1);
		m.m02 = l.get(0, 2); m.m12 = l.get(1, 2); m.m22 = l.get(2, 2); m.m32 = l.get(3, 2);
		m.m03 = l.get(0, 3); m.m13 = l.get(1, 3); m.m23 = l.get(2, 3); m.m33 = l.get(3, 3);
		
		return m;
	}
	
	public static Matrix4f convertMatrix(org.lwjglx.util.vector.Matrix4f l) {
		Matrix4f m = new Matrix4f();

		
		m.set(0, 0, l.m00); m.set(0, 1, l.m01); m.set(0, 2, l.m02); m.set(0, 3, l.m03);
		m.set(1, 0, l.m10); m.set(1, 1, l.m11); m.set(1, 2, l.m12); m.set(1, 3, l.m13);
		m.set(2, 0, l.m20); m.set(2, 1, l.m21); m.set(2, 2, l.m22); m.set(2, 3, l.m23);
		m.set(3, 0, l.m30); m.set(3, 1, l.m31); m.set(3, 2, l.m32); m.set(3, 3, l.m33);

		return m;
	}
	
	public static Matrix4f invert(Matrix4f l) {
		Matrix4f rm = Matrix4f.identity();
		org.lwjglx.util.vector.Matrix4f m = new org.lwjglx.util.vector.Matrix4f();
		
		
		m.m00 = l.get(0, 0); m.m10 = l.get(1, 0); m.m20 = l.get(2, 0); m.m30 = l.get(3, 0);
		m.m01 = l.get(0, 1); m.m11 = l.get(1, 1); m.m21 = l.get(2, 1); m.m31 = l.get(3, 1);
		m.m02 = l.get(0, 2); m.m12 = l.get(1, 2); m.m22 = l.get(2, 2); m.m32 = l.get(3, 2);
		m.m03 = l.get(0, 3); m.m13 = l.get(1, 3); m.m23 = l.get(2, 3); m.m33 = l.get(3, 3);
		
		m.invert();
		
		rm.set(0, 0, m.m00); rm.set(1, 0, m.m10); rm.set(2, 0, m.m20); rm.set(3, 0, m.m30);
		rm.set(0, 1, m.m01); rm.set(1, 1, m.m11); rm.set(2, 1, m.m21); rm.set(3, 1, m.m31);
		rm.set(0, 2, m.m02); rm.set(1, 2, m.m12); rm.set(2, 2, m.m22); rm.set(3, 2, m.m32);
		rm.set(0, 3, m.m03); rm.set(1, 3, m.m13); rm.set(2, 3, m.m23); rm.set(3, 3, m.m33);
		
		return rm;
	}*/
	
	public static int getRandomNumberBetweenI(int low, int high) {
		Random r = new Random();
		
		int result = r.nextInt(high-low) + low;
		
		return result;
	}
	
	public static int getRandomNumberBetweenI(int low, int high, int seed) {
		Random r = new Random(seed);

		int result = r.nextInt(high-low) + low;

		return result;
	}
	
	public static int getRandomNumberBetweenI(int low, int high, Random r) {
		int result = r.nextInt(high-low) + low;

		return result;
	}
	
	public static float getRandomNumberBetweenF(float min, float max) {
		Random r = new Random();

		float result = min + r.nextFloat() * (max - min);

		return result;
	}
	
	public static float getRandomNumberBetweenF(float min, float max, int seed) {
		Random r = new Random(seed);
		
		float result = min + r.nextFloat() * (max - min);
		
		return result;
	}
	
	public static float getRandomNumberBetweenF(float min, float max, Random r) {
		float result = min + r.nextFloat() * (max - min);
		
		return result;
	}
	
	public static Vector3f convertVectorIToVectorF(Vector3i vector) {
		return new Vector3f(vector.x, vector.y, vector.z);
	}
	
	public static Vector3i convertVectorFToVectorI(Vector3f vector) {
		return new Vector3i(Math.round(vector.x), Math.round(vector.y), Math.round(vector.z));
	}
	
	public static Vector3f sub(Vector3f left, Vector3f right) {
		return new Vector3f(left.x - right.x, left.y - right.y, left.z - right.z);
	}
	
	public static Vector3f add(Vector3f left, Vector3f right) {
		return new Vector3f(left.x + right.x, left.y + right.y, left.z + right.z);
	}
	
	public static Vector3f mul(Vector3f left, Vector3f right) {
		return new Vector3f(left.x * right.x, left.y * right.y, left.z * right.z);
	}
	
	public static Vector3f mul(Vector3f left, float right) {
		return new Vector3f(left.x * right, left.y * right, left.z * right);
	}
	
	public static Vector4f mul(Vector4f l, Matrix4f r) {
		Vector4f results = new Vector4f
				(
						l.x*r.get(0, 0)+l.x*r.get(0, 1)+l.x*r.get(0, 2)+l.x*r.get(0, 3),
						l.y*r.get(1, 0)+l.y*r.get(1, 1)+l.y*r.get(1, 2)+l.y*r.get(1, 3),
						l.z*r.get(2, 0)+l.z*r.get(2, 1)+l.z*r.get(2, 2)+l.z*r.get(2, 3),
						l.w*r.get(3, 0)+l.w*r.get(3, 1)+l.w*r.get(3, 2)+l.w*r.get(3, 3)
						);
		
		return results;
	}
	
	/*public static Vector4f mulRawMajorOrder(Vector4f l, Matrix4f r) {
		Vector4f results = new Vector4f
				(
						l.x*r.get(0, 0)+l.y*r.get(0, 1)+l.z*r.get(0, 2)+l.w*r.get(0, 3),
						l.x*r.get(1, 0)+l.y*r.get(1, 1)+l.z*r.get(1, 2)+l.w*r.get(1, 3),
						l.x*r.get(2, 0)+l.y*r.get(2, 1)+l.z*r.get(2, 2)+l.w*r.get(2, 3),
						l.x*r.get(3, 0)+l.y*r.get(3, 1)+l.z*r.get(3, 2)+l.w*r.get(3, 3)
						);
	
		return results;
	}*/
	
	public static Matrix4f view(Vector3f position, Vector3f rotation) {
		/*Matrix4f view = new Matrix4f();
		
		/*view = view.identity().rotate(rotation.getX(), 1, 0, 0).rotate(rotation.getY(), 0, 1, 0)
		.rotate(rotation.getZ(), 0, 0, 1).translate(-position.x, -position.y, -position.z);*//*
		
		Matrix4f translationMatrix = new Matrix4f();
		translationMatrix.translation(new org.joml.Vector3f(-position.x, -position.y, -position.z));
		Matrix4f rotXMatrix = new Matrix4f(); rotXMatrix.rotate((float)Math.toRadians(rotation.getX()), 1, 0, 0);
		Matrix4f rotYMatrix = new Matrix4f(); rotYMatrix.rotate((float)Math.toRadians(rotation.getY()), 0, 1, 0);
		Matrix4f rotZMatrix = new Matrix4f(); rotZMatrix.rotate((float)Math.toRadians(rotation.getZ()), 0, 0, 1);
		
		//Matrix4f rotationMatrix = Matrix4f.multiply(rotYMatrix, Matrix4f.multiply(rotZMatrix, rotXMatrix));
		Matrix4f rotationMatrix = rotXMatrix.mul(rotYMatrix).mul(rotZMatrix).mul(rotXMatrix);
		
		//return Matrix4f.multiply(translationMatrix, rotationMatrix);
		
		return view.mul(translationMatrix).mul(rotationMatrix);*/
		
		Matrix4f m = new Matrix4f()
			     .perspective((float) Math.toRadians(45.0f), 1.0f, 0.01f, 100.0f)
			     .lookAt(0.0f, 0.0f, 10.0f,
			             0.0f, 0.0f, 0.0f,
			             0.0f, 1.0f, 0.0f);
		
		return m;
	}
	
	public static Matrix4f transform(Vector3f position, Vector3f rotation, Vector3f scale) {
		Matrix4f transform = new Matrix4f();
		
		transform.translation(new org.joml.Vector3f(position.x, position.y, position.z));
		transform.rotate(rotation.getX(), 1, 0, 0).rotate(rotation.getY(), 0, 1, 0)
		.rotate(rotation.getZ(), 0, 0, 1).scale(scale.x, scale.y, scale.z);
		
		return transform;
	}
	
	public static Vector3f div(Vector3f left, Vector3f right) {
		return new Vector3f(left.x / right.x, left.y / right.y, left.z / right.z);
	}
}