package Enums;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.BoxCollider2D;
import com.Anix.Behaviours.Camera;
import com.Anix.Behaviours.Physics2D;
import com.Anix.Behaviours.SpriteRenderer;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;
import com.Anix.Objects.GameObject;

public class test extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;	
	
	@Override
	//Will be called on game start
	public void start() {
		requestUpdate();
	}
	
	@Override
	//Will be called once per tick
	public void update() {
		if(Input.isKey(KeyCode.J)) {
			Vector2f pos = Camera.main.convertScreenToWorldSpace();
			
			GameObject obj = new GameObject("Bruh", new Vector3f(pos.x, pos.y, 0));
			obj.addBehaviour(SpriteRenderer.class);
			obj.addBehaviour(Physics2D.class);
			obj.addBehaviour(BoxCollider2D.class);
		}
	}
}