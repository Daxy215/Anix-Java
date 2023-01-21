package com.Anix.Engine;

import java.util.ArrayList;
import java.util.List;

import com.Anix.Behaviours.Physics2D;
import com.Anix.IO.Time;
import com.Anix.Math.Vector2f;

public class PhysicsEngine {
	public static List<Physics2D> bodies = new ArrayList<>();
	
	public static void update() {
		for(int i = 0; i < bodies.size(); i++) {
			Physics2D b = bodies.get(i);
			
			if(b == null || b.gameObject.shouldBeRemoved) {
				bodies.remove(b);
				
				continue;
			}
			
			Vector2f acceleration = new Vector2f((float)(b.force.x / b.mass), (float)(b.force.y / b.mass));
			b.velocity.x += acceleration.x * Time.getDeltaTime();
			b.velocity.y += acceleration.y * Time.getDeltaTime();
			
			b.gameObject.addPosition(b.velocity.x*Time.getDeltaTime(), b.velocity.y*Time.getDeltaTime(), 0);
			
			//b.velocity.x = roundOffTo2DecPlaces(b.velocity.x);
			//b.velocity.y = roundOffTo2DecPlaces(b.velocity.y);
			
			if(b.velocity.x > 0.0000000f) {
				b.velocity.x -= b.friction * Time.getDeltaTime();
			} else if(b.velocity.x < 0.0000000f) {
				b.velocity.x += b.friction * Time.getDeltaTime();
			}
			
			if(b.velocity.y > 0.0000000f) {
				b.velocity.y -= b.friction * Time.getDeltaTime();
			} else if(b.velocity.y < 0.0000000f) {
				b.velocity.y += b.friction * Time.getDeltaTime();
			}
			
			b.force.x = 0;
			b.force.y = 0;
			
			if(!b.isGrounded && b.useGravity) {
				b.force.y += b.mass * b.currentGravity;
				
				b.currentGravity -= b.mass;
			} else {
				if(b.currentGravity != b.gravity) {
					b.force.x = 0;
					b.force.y = 0;
					
					b.velocity.x = 0;
					b.velocity.y = 0;
				}
				
				b.currentGravity = b.gravity;
			}
			
			if(b.isGrounded && b.velocity.y < 0) {
				b.velocity.y = 0;
			}
		}
	}
}
