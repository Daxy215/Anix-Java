
import com.Anix.Behaviours.Physics2D;
import com.Anix.GUI.Windows.Console;
import com.Anix.IO.Time;
import com.Anix.Math.MathD;
import com.Anix.Math.Vector2f;

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
		super();
	}
	
	@Override
	public void start() {
		currentState = State.Idle;
	}
	
	@Override
	public void update() {
		switch(currentState) {
		case Idle:
			timer -= Time.deltaTime;
			
			if(timer <= 0) {
				idleMovement = new Vector2f(MathD.getRandomNumberBetweenF(-1, 1) * Time.deltaTime * speed, MathD.getRandomNumberBetweenF(-1, 1));
				
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