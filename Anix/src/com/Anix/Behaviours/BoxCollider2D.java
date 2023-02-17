package com.Anix.Behaviours;

public class BoxCollider2D extends Behaviour {
	private static final long serialVersionUID = 2163378829355854306L;
	
	public boolean isTrigger = false;

	public BoxCollider2D() {
		
	}

	public BoxCollider2D(boolean isTrigger) {
		this.isTrigger = isTrigger;
	}
	
	@Override
	public void start() {
		Collider2D.addCollider(this);
	}
}
