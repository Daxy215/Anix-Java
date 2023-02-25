package com.Anix.Behaviours;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Math.Rect;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

public final class Collider2D {
	static final int borderWidth = 6, borderHeight = 6;
	
	public static int size = 0;
	
	public static Map<Vector2f, List<BoxCollider2D>> collidersM = new HashMap<>();
	static List<BoxCollider2D> colliders = new ArrayList<>();
	
	public static void updateColliders(List<BoxCollider2D> c) {
		for (int i = 0; i < c.size(); i++) {
			if(!c.get(i).isEnabled)
				continue;
			
			if(c.get(i).gameObject.shouldBeRemoved) {
				c.remove(i);
				i--;
				continue;
			}
			
			BoxCollider2D bc1 = c.get(i);
			GameObject obj1 = c.get(i).gameObject;
			
			// check for collisions with other game objects
			for (int j = i + 1; j < c.size(); j++) {
				if(!c.get(j).isEnabled)
					continue;
				
				if(c.get(j).gameObject.shouldBeRemoved) {
					c.remove(j);
					j--;
					continue;
				}
				
				BoxCollider2D bc2 = c.get(j);
				GameObject obj2 = c.get(j).gameObject;
				
				checkCollisionBetween(bc1, bc2, obj1, obj2);
			}
		}
	}
	
	public static void updateColliders() {
		if(Input.isKeyDown(KeyCode.Z))
			System.err.println("ypdating: " + collidersM.size() + " -  " + size);
		
		// loop through all game objects
		/*for (int i = 0; i < colliders.size(); i++) {
			if(!colliders.get(i).isEnabled)
				continue;
			
			if(colliders.get(i).gameObject.shouldBeRemoved) {
				colliders.remove(i);
				i--;
				continue;
			}
			
			BoxCollider2D bc1 = colliders.get(i);
			GameObject obj1 = colliders.get(i).gameObject;
			
			// check for collisions with other game objects
			for (int j = i + 1; j < colliders.size(); j++) {
				if(!colliders.get(j).isEnabled)
					continue;
				
				if(colliders.get(j).gameObject.shouldBeRemoved) {
					colliders.remove(j);
					j--;
					continue;
				}
				
				BoxCollider2D bc2 = colliders.get(j);
				GameObject obj2 = colliders.get(j).gameObject;
				
				checkCollisionBetween(bc1, bc2, obj1, obj2);
			}
		}*/
	}
	
	private static void checkCollisionBetween(BoxCollider2D bc1, BoxCollider2D bc2, GameObject obj1, GameObject obj2) {
		// check if the distance is less than the scale sum
		if(checkCollision(bc1, bc2)) {
			// the two objects are colliding
			OnCollisionStay(obj1, obj2);
			OnCollisionStay(obj2, obj1);
			
			// check if this is the first frame of collision
			if (!obj1.isColliding(obj2)) {
				OnCollisionEnter(obj1, obj2);
				OnCollisionEnter(obj2, obj1);
				obj1.addCollidingObject(obj2);
				obj2.addCollidingObject(obj1);
			}
		} else {
			// the two objects are not colliding
			if (obj1.isColliding(obj2)) {
				OnCollisionExit(obj1, obj2);
				OnCollisionExit(obj2, obj1);
				obj1.removeCollidingObject(obj2);
				obj2.removeCollidingObject(obj1);
			}
		}
	}
	
	public static void OnCollisionStay(GameObject self, GameObject other) {
        for (Behaviour behaviour : self.getBehaviours()) {
            behaviour.onCollisionStay(other);
        }
    }
	
    public static void OnCollisionEnter(GameObject self, GameObject other) {
        for (Behaviour behaviour : self.getBehaviours()) {
            behaviour.onCollisionEnter(other);
        }
    }
    
    public static void OnCollisionExit(GameObject self, GameObject other) {
        for (Behaviour behaviour : self.getBehaviours()) {
            behaviour.onCollisionExit(other);
        }
    }
    
	private static boolean checkCollision(BoxCollider2D bc1, BoxCollider2D bc2) {
		GameObject obj1 = bc1.gameObject;
		GameObject obj2 = bc2.gameObject;
		Vector3f bounds1 = obj1.getScale();
		Vector3f bounds2 = obj2.getScale();
		
		Rect rect2 = new Rect(obj1.getPosition().x - (bounds1.x * 0.5f), obj1.getPosition().y - (bounds1.y * 0.5f), bounds1.x, bounds1.y);
		Rect rect1 = new Rect(obj2.getPosition().x - (bounds2.x * 0.5f), obj2.getPosition().y - (bounds2.y * 0.5f), bounds2.x, bounds2.y);
		
		Rect overlap = rect2.getIntersection(rect1);
		
		if(overlap == null) {
			return false;
		}
		
		int invert = 1;
		GameObject obj1ToEdit = obj1;
		
		if(obj1.getName().equals("NO")) {
			obj1ToEdit = obj2;
			invert = -1;
		}
		
		if(overlap.width < overlap.height) { //Left | Right
			if(rect1.x < overlap.x) { //Left
				if(!bc1.isTrigger)
					obj1ToEdit.addPosition(overlap.width * invert, 0, 0);
				
				return true;
			} else { //Right
				if(!bc1.isTrigger)
					obj1ToEdit.addPosition(-overlap.width * invert, 0, 0);
				
				return true;
			}
		} else { //Top | Bottom
			if(rect1.y < overlap.y) { //Bottom
				if(!bc1.isTrigger)
					obj1ToEdit.addPosition(0, overlap.height * invert, 0);
				
				return true;
			} else { //Top
				if(!bc1.isTrigger)
					obj1ToEdit.addPosition(0, -overlap.height * invert, 0);
				
				return true;
			}
		}
	}
    
    public static void updateObject(BoxCollider2D b) {
		Vector2f pos = getXY(b.gameObject.getPosition().getXY());
		
		 List<BoxCollider2D> bs = collidersM.get(pos);
		 
		 if(bs != null) {
			 System.err.println("updating");
			 
			 if(collidersM.get(pos).remove(b)) {
				 //size--;
				 addCollider(b);
			 }
		 }
    }
	
	public static void addCollider(BoxCollider2D b) {
		colliders.add(b);
		
		Vector2f pos = getXY(b.gameObject.getPosition().getXY());
		List<BoxCollider2D> batch = collidersM.get(pos);
		
		if(batch != null) {
			batch.add(b);
			size++;
		} else {
			List<BoxCollider2D> newBatch = new ArrayList<BoxCollider2D>();
			newBatch.add(b);
			collidersM.put(pos, newBatch);
		}
	}
	
	public static Vector2f getXY(Vector2f v) {
		int x = (int) v.x;
		int y = (int) v.y;
		//int tx = (int)(Math.round(x / borderWidth) * borderWidth);
        //int ty = (int)(Math.round(y / borderHeight) * borderHeight);
        
		int gx = (int) Math.round(x / borderWidth);
		int gy = (int) Math.round(y / borderHeight);
		
		/*
		 * obj.position.x = (gridX + 0.5f) * cellWidth;
    	 * obj.position.y = (gridY + 0.5f) * cellHeight;   
		 */
		
		return new Vector2f(gx, gy);
	}
}
