import com.Anix.Behaviours.Behaviour;
import com.Anix.Behaviours.SpriteRenderer;
import com.Anix.Engine.Graphics.Material;
import com.Anix.Engine.Graphics.Shader;

public class Player extends Behaviour {
	/*
	* Please ignore this tyvm :)
	*/
	private static final long serialVersionUID = 1L;
	
	public static Inventory inventory;
	
	@Override
	public void awake() {
		inventory = (Inventory)gameObject.getBehaviour(Inventory.class);
	}
	
	@Override
	public void start() {
		gameObject.getMesh().setMaterial(WorldManager.material);
	}
	
	@Override
	public void update() {
		
	}
}