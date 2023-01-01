package Scripts.Player;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.Camera;
import com.Anix.Math.MathD;

public class Player extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;	
	
	public static Player instance;
	
	public float health = 100;
	
	@Override
	public void start() {
		Player.instance = this;
	}
	
	@Override
	public void update() {
		health = MathD.clamp(health, 0, 100);
		
		Camera.main.followObject2D(gameObject, 0.05f);
	}
}