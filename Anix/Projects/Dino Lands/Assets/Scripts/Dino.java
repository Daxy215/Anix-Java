package Scripts;
	
import com.Anix.Behaviours.Camera;
import com.Anix.Behaviours.Physics2D;
import com.Anix.GUI.UI;
import com.Anix.IO.Time;
import com.Anix.Math.Color;
import com.Anix.Math.MathD;
import com.Anix.Math.Vector2f;
import com.Anix.Math.Vector3f;

//@Require(Physics2D.class)
public class Dino extends StatsSystem {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public static enum State {
		Idle, Chasing, Attacking, Dying
	}
	
	public State currentState;
	
	private float timer;
	
	private Vector2f idleMovement;
	
	public Dino() {
	}
	
	@Override
	public void start() {
		currentState = State.Idle;
		System.out.println("starting");
	}
	
	@Override
	public void update() {
		Vector3f pos = Camera.main.convertWorldToScreenSpace(gameObject.getPosition());
		System.out.println("bro? " + pos);
		UI.drawBox(pos.x, pos.y, 0.6f, 150, 150, "", 0, 0, 0, 0, 0, Color.red, Color.black);
		
		switch(currentState) {
		case Idle:
			timer -= Time.deltaTime;
			
			if(timer <= 0) {
				idleMovement = new Vector2f(MathD.getRandomNumberBetweenF(-50, 50) * Time.deltaTime * getSpeed(), MathD.getRandomNumberBetweenF(-50, 50));
				
				((Physics2D)gameObject.getBehaviour(Physics2D.class)).addForce(idleMovement);
				
				timer = MathD.getRandomNumberBetweenF(2, 8);
			}
			
			
			break;
		case Chasing:
			
			break;
		case Attacking:
			
			break;
		case Dying:
			
			break;
		default:
			System.err.println("[INGAME-ERROR] Couldn't find dino state of: " + currentState.name());
			
			break;
		}
	}
}