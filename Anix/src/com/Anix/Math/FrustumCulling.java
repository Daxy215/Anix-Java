package com.Anix.Math;

public class FrustumCulling {
	private static final float[] frustum = new float[16];

	public static boolean isObjectVisible(Matrix4f projectionMatrix, Matrix4f viewMatrix, Vector3f objectPosition, float objectRadius) {
		createFrustum(projectionMatrix, viewMatrix);
		return sphereInFrustum(objectPosition, objectRadius);
	}

	private static void createFrustum(Matrix4f projectionMatrix, Matrix4f viewMatrix) {
		Matrix4f clip = Matrix4f.multiply(projectionMatrix, viewMatrix);
		frustum[0] = clip.m00 - clip.m30;
		frustum[1] = clip.m01 - clip.m31;
		frustum[2] = clip.m02 - clip.m32;
		frustum[3] = clip.m03 - clip.m33;
		
		frustum[4] = clip.m00 + clip.m30;
		frustum[5] = clip.m01 + clip.m31;
		frustum[6] = clip.m02 + clip.m32;
		frustum[7] = clip.m03 + clip.m33;
		
		frustum[8] = clip.m10 - clip.m30;
		frustum[9] = clip.m11 - clip.m31;
		frustum[10] = clip.m12 - clip.m32;
		frustum[11] = clip.m13 - clip.m33;
		
		frustum[12] = clip.m10 + clip.m30;
		frustum[13] = clip.m11 + clip.m31;
		frustum[14] = clip.m12 + clip.m32;
		frustum[15] = clip.m13 + clip.m33;
		
		normalizeFrustumPlanes();
	}
	
	private static void normalizeFrustumPlanes() {
		for (int i = 0; i < 16; i += 4) {
			float t = (float) Math.sqrt(frustum[i] * frustum[i] + frustum[i + 1] * frustum[i + 1] + frustum[i + 2] * frustum[i + 2]);
			frustum[i] /= t;
			frustum[i + 1] /= t;
			frustum[i + 2] /= t;
			frustum[i + 3] /= t;
		}
	}
	
	private static boolean sphereInFrustum(Vector3f position, float radius) {
		for (int i = 0; i < 16; i += 4) {
			if (frustum[i] * position.x + frustum[i + 1] * position.y + frustum[i + 2] * position.z + frustum[i + 3] <= -radius) {
				return false;
			}
		}
		
		return true;
	}
}
