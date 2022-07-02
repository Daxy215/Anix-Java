package com.Anix.Behaviours;

import com.Anix.Behaviours.Collider2D.Collision;
import com.Anix.IO.Time;
import com.Anix.Math.Vector2f;
import com.Anix.Objects.GameObject;

public class Physics2D extends Behaviour {
	private static final long serialVersionUID = -2911730552588220134L;

	private transient float currentGravity;
	
	public float friction = 1f;
	public float gravity = -9.807f;
	public float mass = 0.25f;
	
	public boolean useGravity = true;
	
	public boolean isGrounded;
	
	public Vector2f velocity = new Vector2f();
	public Vector2f force = new Vector2f();
	private transient GameObject collidedObject;
	
	@Override
	public void start() {
		currentGravity = gravity;
	}
	
	@Override
	public void update() {
		if(velocity == null) {
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
		
		if(velocity.x > 0.0f) {
			velocity.x -= friction*Time.getDeltaTime();
		} else if(velocity.x < 0.0f) {
			velocity.x += friction*Time.getDeltaTime();
		}
		
		if(velocity.y > 0.0f) {
			velocity.y -= friction*Time.getDeltaTime();
		} else if(velocity.y < 0.0f) {
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
		}
	}
	
	@Override
	public void onCollisionEnter(Collision collision) {
		if(collision.getDirection() == 0) {
			force.y = 0;
			
			if(velocity.y > 0)
				velocity.y = 0;
		}
		
		if(collision.getDirection() == 1) {
			isGrounded = true;
			
			collidedObject = collision.other;
		}
	}
	
	@Override
	public void onCollisionExit(GameObject other) {
		if(collidedObject != null) {
			if(collidedObject.equals(other)) {
				collidedObject = null;
				isGrounded = false;
			}
		}
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
	
	public void addVelocity(float x, float y) {
		velocity.x += x;
		velocity.y += y;
	}
	
	public void setForce(float x, float y) {
		force.x = x;
		force.y = y;
	}
	
	public void addForce(float x, float y) {
		force.add(x, y);
	}
	
	private float roundOffTo2DecPlaces(float val) {
	    return Float.parseFloat(String.format("%.2f", val));
	}
}
