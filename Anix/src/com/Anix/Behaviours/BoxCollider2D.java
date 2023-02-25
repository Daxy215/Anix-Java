package com.Anix.Behaviours;

import java.util.List;

import com.Anix.Math.Vector2f;

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
		
		requestUpdate();
	}
	
	@Override
	public void update() {
		if(gameObject.isDirty()) {
			gameObject.resetDirty();
			
			Vector2f pos = Collider2D.getXY(gameObject.getPosition().getXY());
			
			List<BoxCollider2D> bs = Collider2D.collidersM.get(pos);
			
			if(bs != null)
				Collider2D.updateColliders(bs);
			else
				Collider2D.updateObject(this);
		}
	}
}
