package Managers;

import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.Camera;
import com.Anix.IO.Input;

public class GameManager extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;	
	
	@Override
	public void start() {
		requestUpdate();
	}
	
	@Override
	public void update() {
		Camera.main.gameObject.getPosition().z -= Input.getScrollY();
	}
}