package com.Anix.Behaviours;

import java.util.ArrayList;
import java.util.List;

import com.Anix.Math.Rect;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

public final class Collider2D extends BoxCollider2D {
	private static final long serialVersionUID = -4525044150700190954L;
	
	public static class Collision {
		private byte dir;
		
		public GameObject other;
		
		public Collision(GameObject other, byte dir) {
			this.other = other;
			this.dir = dir;
		}
		
		/**
		 * 0 - Up<br>
		 * 1 - Bottom<br>
		 * 2 - Left<br>
		 * 3 - Right<br>
		 */
		public byte getDirection() {
			return dir;
		}
	}
	
	static List<BoxCollider2D> colliders = new ArrayList<BoxCollider2D>();
	
	private static List<BoxCollider2D> movers = new ArrayList<BoxCollider2D>();
	private static List<BoxCollider2D> solids = new ArrayList<BoxCollider2D>();
	
	public static void updateColliders() {
		for(int i = 0; i < colliders.size(); i++) {
			if(colliders.get(i).gameObject.shouldBeRemoved) {
				colliders.remove(i);
				
				continue;
			}
			
			if(colliders.get(i).gameObject.isDirty()) {
				movers.add(colliders.get(i));
			} else {
				solids.add(colliders.get(i));
			}
			
			colliders.get(i).gameObject.resetDirty();
		}
		
		for(int i = 0; i < movers.size() - 1; i++) {
			for(int j = 0; j < solids.size() - 1; j++) {
				checkCollision(movers.get(i), solids.get(j));
				checkCollision(movers.get(i + 1), solids.get(j));
				checkCollision(movers.get(i), movers.get(i + 1));
				checkCollision(solids.get(j), solids.get(j + 1));
			}
		}
		
		/*for(int i = 0; i < movers.size(); i++) {
			for(int j = 0; j < movers.size(); j++) {
				if(i == j)
					continue;
				
				checkCollision(movers.get(i), movers.get(j));
			}
		}
		
		for(int i = 0; i < movers.size(); i++) {
			for(int j = 0; j < solids.size(); j++) {
				checkCollision(movers.get(i), solids.get(j));
			}
		}
		
		for(int i = 0; i < solids.size(); i++) {
			for(int j = 0; j < solids.size(); j++) {
				if(i == j)
					continue;
				
				checkCollision(solids.get(i), solids.get(j));
			}
		}
		
		for(int i = 0; i < solids.size(); i++) {
			for(int j = 0; j < movers.size(); j++) {
				checkCollision(movers.get(j), solids.get(i));
			}
		}*/
		
		movers.clear();
		solids.clear();
	}
	
	private static boolean checkCollision(BoxCollider2D obj1, BoxCollider2D obj2) {
		if(!obj1.equals(obj2)) {
			Vector3f bounds1 = obj1.gameObject.getScale();
			Vector3f bounds2 = obj2.gameObject.getScale();
			
			Rect rect2 = new Rect(obj1.gameObject.getPosition().x - (bounds1.x * 0.5f), obj1.gameObject.getPosition().y - (bounds1.y * 0.5f), bounds1.x, bounds1.y);
			Rect rect1 = new Rect(obj2.gameObject.getPosition().x - (bounds2.x * 0.5f), obj2.gameObject.getPosition().y - (bounds2.y * 0.5f), bounds2.x, bounds2.y);
			
			Rect overlap = rect2.getIntersection(rect1);
			
			if(overlap == null) {
				if(obj2.gameObject == obj1.gameObject.collisionObject) {
					for(int i = 0; i < obj1.gameObject.getBehaviours().size(); i++) {
						obj1.gameObject.getBehaviours().get(i).onCollisionExit(obj1.gameObject.collisionObject);
					}
					
					obj1.gameObject.collisionObject = null;
				}
				
				if(obj1.gameObject == obj2.gameObject.collisionObject) {
					for(int i = 0; i < obj2.gameObject.getBehaviours().size(); i++) {
						obj2.gameObject.getBehaviours().get(i).onCollisionExit(obj2.gameObject.collisionObject);
					}
					
					obj2.gameObject.collisionObject = null;
				}
				
				return false;
			}
			
			boolean collided = false;
			
			/**dir
			 * 0 - Up<br>
			 * 1 - Bottom<br>
			 * 2 - Left<br>
			 * 3 - Right<br>
			 */
			byte dir = 0;
			
			if(overlap.width < overlap.height) { //Left | Right
				if(rect1.x < overlap.x) { //Left
					if(!obj1.isTrigger) {
						obj1.gameObject.addPosition(overlap.width, 0, 0);
					}
					
					collided = true;
					dir = 2;
				} else { //Right
					if(!obj1.isTrigger) {
						obj1.gameObject.addPosition(-overlap.width, 0, 0);
					}
					
					collided = true;
					dir = 4;
				}
			} else { //Top | Bottom
				if(rect1.y < overlap.y) { //Bottom
					if(!obj1.isTrigger) {
						obj1.gameObject.addPosition(0, overlap.height, 0);
					}
					
					collided = true;
					dir = 1;
				} else { //Top
					if(!obj1.isTrigger) {
						obj1.gameObject.addPosition(0, -overlap.height, 0);
					}
					
					collided = true;
					dir = 0;
				}
			}
			
			for(int i = 0; i < obj1.gameObject.getBehaviours().size(); i++) {
				if(obj1.gameObject.collisionObject == null) {
					obj1.gameObject.getBehaviours().get(i).onCollisionEnter(new Collision(obj2.gameObject, dir));
				}
				
				obj1.gameObject.getBehaviours().get(i).onCollisionStay(new Collision(obj2.gameObject, dir));
			}
			
			for(int i = 0; i < obj2.gameObject.getBehaviours().size(); i++) {
				if(obj2.gameObject.collisionObject == null) {
					obj2.gameObject.getBehaviours().get(i).onCollisionEnter(new Collision(obj1.gameObject, dir));
				}
				
				obj2.gameObject.getBehaviours().get(i).onCollisionStay(new Collision(obj1.gameObject, dir));
			}
			
			if(obj1.gameObject.collisionObject == null)
				obj1.gameObject.collisionObject = obj2.gameObject;
			if(obj2.gameObject.collisionObject == null)
				obj2.gameObject.collisionObject = obj1.gameObject;
			
			return collided;
		}
		
		return false;
	}
}
