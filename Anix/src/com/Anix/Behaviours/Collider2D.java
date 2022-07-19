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
	
	private static class Collider {
		public BoxCollider2D bx;
		public GameObject collisionObject;
		
		public Collider(BoxCollider2D boxCollider) {
			this.bx = boxCollider;
		}
	}
	
	static List<BoxCollider2D> colliders = new ArrayList<BoxCollider2D>();
	
	private static List<Collider> movers = new ArrayList<>();
	private static List<Collider> solids = new ArrayList<>();
	
	public static void updateColliders() {
		for(int i = 0; i < colliders.size(); i++) {
			if(colliders.get(i).gameObject.shouldBeRemoved) {
				colliders.remove(i);
				
				continue;
			}
			
			if(colliders.get(i).gameObject.isDirty()) {
				movers.add(new Collider(colliders.get(i)));
			} else {
				solids.add(new Collider(colliders.get(i)));
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
	
	private static boolean checkCollision(Collider c1, Collider c2) {
		if(!c1.bx.gameObject.equals(c2.bx.gameObject)) {
			GameObject obj1 = c1.bx.gameObject;
			GameObject obj2 = c2.bx.gameObject;
			Vector3f bounds1 = obj1.getScale();
			Vector3f bounds2 = obj2.getScale();
			
			Rect rect2 = new Rect(obj1.getPosition().x - (bounds1.x * 0.5f), obj1.getPosition().y - (bounds1.y * 0.5f), bounds1.x, bounds1.y);
			Rect rect1 = new Rect(obj2.getPosition().x - (bounds2.x * 0.5f), obj2.getPosition().y - (bounds2.y * 0.5f), bounds2.x, bounds2.y);
			
			Rect overlap = rect2.getIntersection(rect1);
			
			if(overlap == null) {
				if(obj2 == c1.collisionObject) {
					for(int i = 0; i < obj1.getBehaviours().size(); i++) {
						obj1.getBehaviours().get(i).onCollisionExit(c1.collisionObject);
					}
					
					c1.collisionObject = null;
				}
				
				if(obj1 == c2.collisionObject) {
					for(int i = 0; i < obj2.getBehaviours().size(); i++) {
						obj2.getBehaviours().get(i).onCollisionExit(c2.collisionObject);
					}
					
					c2.collisionObject = null;
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
					if(!c1.bx.isTrigger) {
						obj1.addPosition(overlap.width, 0, 0);
					}
					
					collided = true;
					dir = 2;
				} else { //Right
					if(!c1.bx.isTrigger) {
						obj1.addPosition(-overlap.width, 0, 0);
					}
					
					collided = true;
					dir = 4;
				}
			} else { //Top | Bottom
				if(rect1.y < overlap.y) { //Bottom
					if(!c1.bx.isTrigger) {
						obj1.addPosition(0, overlap.height, 0);
					}
					
					collided = true;
					dir = 1;
				} else { //Top
					if(!c1.bx.isTrigger) {
						obj1.addPosition(0, -overlap.height, 0);
					}
					
					collided = true;
					dir = 0;
				}
			}
			
			for(int i = 0; i < obj1.getBehaviours().size(); i++) {
				if(c1.collisionObject == null) {
					obj1.getBehaviours().get(i).onCollisionEnter(new Collision(obj2, dir));
				}
				
				obj1.getBehaviours().get(i).onCollisionStay(new Collision(obj2, dir));
			}
			
			for(int i = 0; i < obj2.getBehaviours().size(); i++) {
				if(c2.collisionObject == null) {
					obj2.getBehaviours().get(i).onCollisionEnter(new Collision(obj1, dir));
				}
				
				obj2.getBehaviours().get(i).onCollisionStay(new Collision(obj1, dir));
			}
			
			if(c1.collisionObject == null)
				c1.collisionObject = obj2;
			if(c2.collisionObject == null)
				c2.collisionObject = obj1;
			
			return collided;
		}
		
		return false;
	}
}
