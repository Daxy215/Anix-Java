package com.Anix.Engine;

import java.util.ArrayList;
import java.util.List;

import com.Anix.Behaviours.Physics2D;
import com.Anix.IO.Time;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;

public class PhysicsEngine {
	public static class QuadTree {
		private QuadTree[] children;
		private Physics2D body;
		private Vector2f center;
		private double size;
		
		public QuadTree() {
			children = new QuadTree[4];
			body = null;
			center = new Vector2f();
			size = 0;
		}
		
		public void insert(Physics2D b) {
			if(size == 0) {
				body = b;	
			} else {
				Vector2f pos = b.gameObject.getPosition().getXY();
				int quadrant = getQuadRant(pos);
				
				if(children[quadrant] == null) {
					children[quadrant] = new QuadTree();
				}
				
				children[quadrant].insert(b);
			}
		}
		
		public void computeForce(Physics2D b, Vector2f force) {
			// If this is an external node, add the force from this body
			if (size == 0) {
				if (body != b) {
					force.add(body.computeForce(b));
				}
			} else {
				// Otherwise, compute the distance between the body and the center of this quadtree
				Vector2f r = Vector2f.sub(b.gameObject.getPosition().getXY(), center);
				double r2 = r.dot(r);  // distance squared
				double s2 = size * size;  // size of quadtree squared

				// If the quadtree is far enough away, treat it as a single body
				if (4 * s2 > r2) {
					force.add(body.computeForce(b));
				} else {
					// Otherwise, recurse into the quadrants
					for (QuadTree q : children) {
						if (q != null) {
							q.computeForce(b, force);
						}
					}
				}
			}
		}
		
		public int getQuadRant(Vector2f pos) {
			double x = pos.getX();
			double y = pos.getY();
			double cx = center.getX();
			double cy = center.getY();
			if (x < cx) {
				if (y < cy) {
					return 0;
				} else {
					return 1;
				}
			} else {
				if (y < cy) {
					return 2;
				} else {
					return 3;
				}
			}
		}
	}
	
	private static QuadTree tree = new QuadTree();
	
	public static List<Physics2D> bodies = new ArrayList<>();
	
	public static void update() {
		for(Physics2D b : bodies) {
			if(b == null)
				continue;
			
			b.gameObject.setPosition(Vector3f.add(b.gameObject.getPosition(), Vector3f.multiply(b.velocity, Time.deltaTime)));
			
			Vector2f netForce = new Vector2f();
			tree.computeForce(b, netForce);
			b.setForce(netForce);
			
			b.setVelocity(Vector2f.add(b.velocity, Vector2f.multiply(netForce, (float)(Time.deltaTime / b.mass))));
		}
	}
	
	public static void addBody(Physics2D body) {
		bodies.add(body);
		tree.insert(body);
	}
	
	public static void removeBody(Physics2D body) {
		bodies.remove(body);
		//tree.remove(body);
	}
}
