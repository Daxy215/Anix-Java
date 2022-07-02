package com.Anix.Math;

import com.Anix.Behaviours.Camera;
import com.Anix.Objects.GameObject;
import com.Anix.SceneManager.SceneManager;

public class RayCast {
	/**
	 * PITCH - X
	 * YAW - Y
	 * ROLL - Z
	 */
	
	private static final int MAX_ITERATIONS = 169;
	
	public static GameObject rayCast(int distance) {
		float x = -(float)Math.sin(Math.toRadians(Camera.main.gameObject.getRotation().getY()));
		float y = (float)Math.sin(Math.toRadians(Camera.main.gameObject.getRotation().getX()));
		float z = -(float)Math.cos(Math.toRadians(Camera.main.gameObject.getRotation().getY()));
		
		Vector3f pos = new Vector3f(x, y, z).add(Camera.main.gameObject.getPosition());
		
		//Limit distance.
		if(distance > MAX_ITERATIONS)
			distance = MAX_ITERATIONS;
		
		while(distance > 0) {
			//Only collide with objects that has "BoxCollider3D"
			for(int i = 0; i < SceneManager.getCurrentScene().getGameObjects().size(); i++) {
				GameObject obj = SceneManager.getCurrentScene().getGameObjects().get(i);
				
				if(obj.uuid.equals(Camera.main.gameObject.uuid))
					continue;
				
				if(MathD.AABBCollision(obj.getPosition(), obj.getScale(), pos, new Vector3f(1))) {
					return obj;
				}
			}
			
			//No objects where found.
			//Move ray along direction.
			pos.x += x;
			pos.y += y;
			pos.z += z;
			
			distance--;
		}
		
		//No objects were found.
		return null;
	}
}
