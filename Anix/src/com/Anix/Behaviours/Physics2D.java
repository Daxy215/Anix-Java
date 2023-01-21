package com.Anix.Behaviours;

import com.Anix.Annotation.HideFromInspector;
import com.Anix.Behaviours.Collider2D.Collision;
import com.Anix.Engine.PhysicsEngine;
import com.Anix.Math.Vector2f;
import com.Anix.Objects.GameObject;

public class Physics2D extends Behaviour {
	private static final long serialVersionUID = -2911730552588220134L;
	
	@HideFromInspector
	public float currentGravity;
	
	public float friction = 1f;
	public float gravity = -9.807f;
	public double mass = 0.25f;
	
	public boolean useGravity = true;
	public boolean isGrounded;
	
	public Vector2f velocity = new Vector2f();
	public Vector2f force = new Vector2f();
	public Vector2f acceleration = new Vector2f();
	
	//private transient GameObject collidedObject;
	
	@Override
	public void start() {
		PhysicsEngine.bodies.add(this);
		currentGravity = gravity;
	}
	
	@Override
	public void update() {
		//gameObject.addPosition(Vector2f.multiply(velocity, Time.deltaTime)
		//		.add(Vector2f.multiply(acceleration, 0.5f * Time.deltaTime * Time.deltaTime)));
		//velocity.add(Vector2f.multiply(acceleration, Time.deltaTime));
		
		/*if(velocity == null) {
			return;
		}
		
		if(collidedObject != null && (collidedObject.shouldBeRemoved || !collidedObject.isEnabled())) {
			collidedObject = null;
			isGrounded = false;
		}
		
		Vector2f acceleration = new Vector2f(force.x / mass, force.y / mass);
		velocity.x += acceleration.x * Time.getDeltaTime();
		velocity.y += acceleration.y * Time.getDeltaTime();
		
		gameObject.addPosition(velocity.x*Time.getDeltaTime(), velocity.y*Time.getDeltaTime(), 0);
		
		velocity.x = roundOffTo2DecPlaces(velocity.x);
		velocity.y = roundOffTo2DecPlaces(velocity.y);
		
		if(velocity.x > 0.0000000f) {
			velocity.x -= friction*Time.getDeltaTime();
		} else if(velocity.x < 0.0000000f) {
			velocity.x += friction*Time.getDeltaTime();
		}
		
		if(velocity.y > 0.0000000f) {
			velocity.y -= friction*Time.getDeltaTime();
		} else if(velocity.y < 0.0000000f) {
			velocity.y += friction*Time.getDeltaTime();
		}
		
		force.x = 0;
		force.y = 0;
		
		if(!isGrounded && useGravity) {
			force.y += mass * currentGravity;
			
			currentGravity -= mass;
		} else {
			if(currentGravity != gravity) {
				force.x = 0;
				force.y = 0;
				
				velocity.x = 0;
				velocity.y = 0;
			}
			
			currentGravity = gravity;
		}
		
		if(isGrounded && velocity.y < 0) {
			velocity.y = 0;
		}*/
	}
	
	@Override
 	public void onRemove() {
		
	}
	
	@Override
	public void onDestroy() {
		
	}
	
	@Override
	public void onCollisionEnter(Collision collision) {
		/*if(collision.getDirection() == 0) {
			force.y = 0;
			
			if(velocity.y > 0)
				velocity.y = 0;
		}
		
		if(collision.getDirection() == 1) { //Bottom
			isGrounded = true;
			
			collidedObject = collision.other;
		}*/
	}
	
	@Override
	public void onCollisionExit(GameObject other) {
		/*if(collidedObject != null) {
			if(collidedObject.equals(other)) {
				collidedObject = null;
				isGrounded = false;
			}
		}*/
	}
	
	public boolean isGrounded() {
		return isGrounded;
	}
	
	public void setVelocity(float x, float y) {
		velocity.x = x;
		velocity.y = y;
	}
	
	public void setVelocity(Vector2f velocity) {
		this.velocity = velocity;
	}
	
	public void addVelocity(Vector2f value) {
		addVelocity(value.x, value.y);
	}
	
	public void addVelocity(float x, float y) {
		velocity.x += x;
		velocity.y += y;
	}
	
	public void setForce(Vector2f value) {
		setForce(value.x, value.y);
	}
	
	public void setForce(float x, float y) {
		force.x = x;
		force.y = y;
	}
	
	public void addForce(Vector2f value) {
		addForce(value.x, value.y);
	}
	
	public void addForce(float x, float y) {
		force.add(x, y);
	}
	
	public Vector2f getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector2f acceleration) {
		this.acceleration = acceleration;
	}
	
	public void addAcceleration(Vector2f value) {
		addAcceleration(value.x, value.y);
	}
	
	public void addAcceleration(float x, float y) {
		acceleration.add(x, y);
	}

	/*private float roundOffTo2DecPlaces(float val) {
	    return Float.parseFloat(String.format("%.2f", val));
	}*/
}
