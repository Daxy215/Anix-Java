import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.Camera;
import com.Anix.IO.Input;
import com.Anix.IO.KeyCode;
import com.Anix.IO.Time;

public class PlayerMovement extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public float speed = 5;
	
	@Override
	//Will be called on game start
	public void start() {
		//TODO: Code here..
	}
	
	@Override
	public void update() {
		if(Input.isKey(KeyCode.W)) {
			gameObject.addPosition(0, -speed * Time.deltaTime);
		} else if(Input.isKey(KeyCode.S)) {
			gameObject.addPosition(0, speed * Time.deltaTime);
		}
		
		if(Input.isKey(KeyCode.A)) {
			gameObject.addPosition(-speed * Time.deltaTime, 0);
		} else if(Input.isKey(KeyCode.D)) {
			gameObject.addPosition(speed * Time.deltaTime, 0);
		}
		
		Camera.main.followObject2D(gameObject, 0.05f);
	}
}