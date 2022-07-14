package com.Anix.Behaviours;

public class BoxCollider2D extends Behaviour {
	private static final long serialVersionUID = 2163378829355854306L;
	
	public boolean isTrigger = false;
		
	@Override
	public void start() {
		Collider2D.colliders.add(this);
		System.err.println("starting");
	}
}
