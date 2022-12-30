package Scripts.Player;

import com.Anix.Behaviours.Behaviour;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.IO.Time;

public class PlayerMovement extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public float movementSpeed = 5;
	
	@Override
	public void start() {
		
	}
	
	@Override
	public void update() {
		if(Input.isKey(KeyCode.W)) {
			gameObject.addPosition(0, movementSpeed * Time.deltaTime);
		} else if(Input.isKey(KeyCode.S)) {
			gameObject.addPosition(0, -movementSpeed * Time.deltaTime);
		}
		
		if(Input.isKey(KeyCode.A)) {
			gameObject.addPosition(-movementSpeed * Time.deltaTime, 0);
		} else if(Input.isKey(KeyCode.D)) {
			gameObject.addPosition(movementSpeed * Time.deltaTime, 0);
		}
	}
}