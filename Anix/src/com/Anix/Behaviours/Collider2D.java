package com.Anix.Behaviours;

import java.util.ArrayList;
import java.util.List;

import com.Anix.Math.Rect;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

public final class Collider2D extends BoxCollider2D {
	private static final long serialVersionUID = -4525044150700190954L;
	
	/*public static class Collision {
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
		 *//*
		public byte getDirection() {
			return dir;
		}
	}
	
	private static class Collider {
		public BoxCollider2D bx;
		public GameObject collisionObject;

		public Collider() {

		}

		public Collider(BoxCollider2D boxCollider) {
			this.bx = boxCollider;
		}
	}*/
	
	static List<BoxCollider2D> colliders = new ArrayList<>();
	
	//private static List<Collider> movers = new ArrayList<>();
	//private static List<Collider> solids = new ArrayList<>();
	
	public static void updateColliders() {
		// loop through all game objects
		for (int i = 0; i < colliders.size(); i++) {
			if(!colliders.get(i).isEnabled)
				continue;
			
			BoxCollider2D bc1 = colliders.get(i);
			GameObject obj1 = colliders.get(i).gameObject;
			
			// check for collisions with other game objects
			for (int j = i + 1; j < colliders.size(); j++) {
				if(!colliders.get(j).isEnabled)
					continue;
				
				BoxCollider2D bc2 = colliders.get(j);
				GameObject obj2 = colliders.get(j).gameObject;
				
				// calculate distance between the two objects
				//Vector2f obj1Pos = obj1.getPosition().getXY();
				//Vector2f obj2Pos = obj2.getPosition().getXY();
				//float distance = (float) MathD.distanceBetweenVector2(obj1Pos, obj2Pos) * 1.5f;
				
				// calculate sum of the scales of the two objects
				//Vector2f obj1Scale = obj1.getScale().getXY().mul(0.5f);
				//Vector2f obj2Scale = obj2.getScale().getXY().mul(0.5f);
				//float scaleSum = obj1Scale.length() + obj2Scale.length();
				
				// check if the distance is less than the scale sum
				//if (distance < scaleSum) {
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
    
	//public static void updateColliders() {
	/*for(int i = 0; i < colliders.size(); i++) {
			if(colliders.get(i).bx.gameObject.shouldBeRemoved) {
				colliders.remove(i);

				continue;
			}

			Collider c = new Collider(colliders.get(i).bx);
			c.collisionObject = colliders.get(i).collisionObject;

			if(colliders.get(i).bx.gameObject.isDirty()) {
				movers.add(c);
			} else {
				solids.add(c);
			}

			colliders.get(i).bx.gameObject.resetDirty();
		}

		for(int i = 0; i < movers.size() - 1; i++) {
			for(int j = 0; j < solids.size() - 1; j++) {
				checkCollision(movers.get(i), solids.get(j));
				checkCollision(movers.get(i + 1), solids.get(j));
				checkCollision(movers.get(i), movers.get(i + 1));
				checkCollision(solids.get(j), solids.get(j + 1));
			}
		}*/

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

	//movers.clear();
	//solids.clear();
	//}
    
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
		
		if(overlap.width < overlap.height) { //Left | Right
			if(rect1.x < overlap.x) { //Left
				if(!bc1.isTrigger)
					obj1.addPosition(overlap.width, 0, 0);
				
				return true;
			} else { //Right
				if(!bc1.isTrigger)
					obj1.addPosition(-overlap.width, 0, 0);
				
				return true;
			}
		} else { //Top | Bottom
			if(rect1.y < overlap.y) { //Bottom
				if(!bc1.isTrigger)
					obj1.addPosition(0, overlap.height, 0);
				
				return true;
			} else { //Top
				if(!bc1.isTrigger)
					obj1.addPosition(0, -overlap.height, 0);
				
				return true;
			}
		}
	}
    
	public static void addCollider(BoxCollider2D b) {
		colliders.add(b);
	}
}
