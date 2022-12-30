
import com.Anix.Behaviours.Physics2D;
import com.Anix.GUI.Windows.Console;
import com.Anix.IO.Time;
import com.Anix.Math.MathD;

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
	
	public Dino() {
		super();
	}
	
	@Override
	public void start() {
		currentState = State.Idle;
		System.err.println("Sstate: " + currentState);
	}
	
	@Override
	public void update() {
		System.err.println("Updating");
		
		switch(currentState) {
		case Idle:
			timer -= Time.deltaTime;
			
			System.err.println("timer: " + timer);
			
			if(timer <= 0) {
				Console.Log("Should move");
				
				((Physics2D)gameObject.getBehaviour(Physics2D.class)).addForce(MathD.getRandomNumberBetweenF(-1, 1), MathD.getRandomNumberBetweenF(-1, 1));
				
				timer = 2;
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